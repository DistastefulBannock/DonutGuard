package me.bannock.donutguard.obf.mutator.impl.string;

import com.google.inject.Inject;
import me.bannock.donutguard.obf.asm.entry.impl.ClassEntry;
import me.bannock.donutguard.obf.config.Configuration;
import me.bannock.donutguard.obf.mutator.Mutator;
import me.bannock.donutguard.obf.mutator.impl.string.linenum.MethodMetadata;
import me.bannock.donutguard.obf.mutator.impl.string.linenum.StringMetadata;
import me.bannock.donutguard.utils.AsmUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Removes string literals and replaces them with a call to a method.
 * Works like so:
 * <ol>
 *     <li>Removes all line numbers from the class</li>
 *     <li>Injects its own line numbers into each method.
 *     Line numbers are used by the decryption method to locate decryption values</li>
 *     <li>Removes LDC nodes and replaces them with method calls; logs needed information
 *     while doing so</li>
 *     <li>Creates the needed decryption methods and fields.
 *     Actual encryption is simple Vigenère cipher</li>
 * </ol>
 */
public class LineNumberStringLiteralMutator extends Mutator {

    private final Logger logger = LogManager.getLogger();
    private final Configuration config;
    private int stringsEncrypted = 0, lineNumbersRemoved = 0, classesCreated,
            methodsCreated = 0, fieldsCreated = 0;
    private Map<MethodNode, MethodMetadata> methodMetadataMap;
    private Set<StringMetadata> encryptedStrings;
    private ClassNode decryptMembersOwner;
    private MethodNode decryptMethod;
    private FieldNode encryptedStringsField, decryptedValueCacheArray;

    @Inject
    public LineNumberStringLiteralMutator(Configuration config) {
        super("String Literal Encryption",
                StringEncConfigGroup.STRING_ENC_ENABLED.get(config)
                        && StringEncConfigGroup.STRING_ENC_TYPE.get(config) ==
                        StringLiteralEncryptionType.IDENTIFIERS_VIA_LINE_NUMBERS_AND_INTS);
        this.config = config;
    }

    @Override
    public void setup() {
        stringsEncrypted = 0;
        lineNumbersRemoved = 0;
        classesCreated = 0;
        methodsCreated = 0;
        fieldsCreated = 0;
    }

    @Override
    public void firstPassClassTransform(ClassEntry entry) {
        // Because this mutator contains everything in the class that gets passed in,
        // we create a new map for every class rather than letting the data use memory for
        // no reason
        methodMetadataMap = new HashMap<>();
        encryptedStrings = new LinkedHashSet<>();

        // We first remove line numbers as we use them as we will use them as an identifier
        // in our decryption method
        scrubLineNumbers(entry);

        // Before we do anything, we must make sure that this class even has strings for us to encrypt
        boolean foundString = isFoundString(entry);
        if (!foundString)
            return;

        // We now give a new line number to each method. This value will later be
        // used to decrypt our string literals
        assignLineNumbersToEveryMethod(entry);

        // Creating the decrypt method before encrypting the strings is easier since
        // we'll have the method reference in a variable. However, we still populate
        // it later
        createDecryptMembers(entry);

        // We now go on to replace all LDC (load constant) instructions
        // that load strings with calls to our decryption method. We also generate
        // the keys used by the decryption method in this step
        replaceStringConstantLoads(entry);

        // Now that we have all the information we need, we're able to populate and
        // add the decrypt method to the class
        populateDecryptMembers(entry);

    }

    private void populateDecryptMembers(ClassEntry entry){
        ClassNode node = entry.getContent();

        // We need to modify our clinit to
        MethodNode clinit = null;
        for (MethodNode methodNode : node.methods){
            if (!methodNode.name.equals("<clinit>"))
                continue;
            clinit = methodNode;
            break;
        }
        if (clinit == null){ // The class doesn't contain the <clinit> method, so we create one
            clinit = new MethodNode(
                    Opcodes.ACC_STATIC,
                    "<clinit>",
                    "()V",
                    null,
                    new String[0]
            );
            clinit.instructions = new InsnList();
            clinit.instructions.add(new InsnNode(Opcodes.RETURN));
            node.methods.add(clinit);
        }

        // We split these into different methods so we don't have to worry about
        // adding too many instructions to a method (causes a crash)
        Set<MethodNode> calledDecryptionMethods = createExtraArrayPopulationMethods(entry);

        // Now we populate the clinit method with multiple calls at the very beginning
        // of it that call to all the array population methods
        clinit.instructions.insertBefore(clinit.instructions.getFirst(),
                createClinitInstructions(calledDecryptionMethods));

        // We finally creat the decryption method
        populateDecryptionMethod(entry);

    }

    /**
     * Populates the decryption method with the instructions needed to decrypt the strings
     * @param entry The class entry
     */
    private void populateDecryptionMethod(ClassEntry entry){

        final int ENCRYPTED_INDEX_PARAM = 0;
        final int INDEX = 1;
        final int LINE_NUM_INDEX = 2;
        final int ENCRYPTED_STRING = 3;
        final int ENCRYPTION_KEY = 4;
        final int ENCRYPTION_INC = 5;
        final int LOOP_INDEX = 6;
        final int FINAL_CHARS = 7;

        // First, we must get the method's line number so we can get the array index
        InsnList insnList = new InsnList();
        insnList.add(new TypeInsnNode(Opcodes.NEW, "java/lang/Throwable")); // A
        insnList.add(new InsnNode(Opcodes.DUP)); // A, A
        insnList.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/Throwable",
                "<init>", "()V")); // A

        insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Throwable",
                "getStackTrace", "()[Ljava/lang/StackTraceElement;")); // [A
        insnList.add(AsmUtils.toInsnNode(1)); // I, [A
        insnList.add(new InsnNode(Opcodes.AALOAD)); // A
        insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
                "java/lang/StackTraceElement", "getLineNumber", "()I")); // I
        insnList.add(new VarInsnNode(Opcodes.ISTORE, LINE_NUM_INDEX));

        insnList.add(new VarInsnNode(Opcodes.ILOAD, ENCRYPTED_INDEX_PARAM)); // I
        insnList.add(new VarInsnNode(Opcodes.ILOAD, LINE_NUM_INDEX)); // I, I
        insnList.add(new InsnNode(Opcodes.IXOR)); // I
        insnList.add(new VarInsnNode(Opcodes.ISTORE, INDEX));

        LabelNode endCacheIf = new LabelNode();
        insnList.add(new FieldInsnNode(Opcodes.GETSTATIC, decryptMembersOwner.name,
                decryptedValueCacheArray.name, decryptedValueCacheArray.desc)); // [String
        insnList.add(new VarInsnNode(Opcodes.ILOAD, INDEX)); // I, [String
        insnList.add(new InsnNode(Opcodes.AALOAD)); // String
        insnList.add(new InsnNode(Opcodes.DUP)); // String, String
        insnList.add(new JumpInsnNode(Opcodes.IFNULL, endCacheIf)); // String
        insnList.add(new InsnNode(Opcodes.DUP)); // String, String
        insnList.add(new InsnNode(Opcodes.DUP)); // String, String, String
        insnList.add(new InsnNode(Opcodes.ARETURN)); // String, String
        insnList.add(endCacheIf); // String
        insnList.add(new FrameNode(Opcodes.F_NEW, 3,
                new Object[]{1, 1, 1},
                1, new Object[]{"java/lang/String"}));
        insnList.add(new InsnNode(Opcodes.POP));

        // Now we have to get the encrypted string from the class's array. The number we
        // xored with the line number beforehand is the index for the encrypted value
        insnList.add(new FieldInsnNode(Opcodes.GETSTATIC, decryptMembersOwner.name,
                encryptedStringsField.name, encryptedStringsField.desc)); // [String
        insnList.add(new VarInsnNode(Opcodes.ILOAD, INDEX)); // I, [String
        insnList.add(new InsnNode(Opcodes.AALOAD)); // String
        insnList.add(new VarInsnNode(Opcodes.ASTORE, ENCRYPTED_STRING));

        // Now we must grab the key and increment values from this huge switch statement
        int[] keys = new int[encryptedStrings.size()];
        LabelNode[] labels = new LabelNode[encryptedStrings.size()];
        {
            int i = 0;
            for (StringMetadata meta : encryptedStrings){
                keys[i] = meta.getIndex();
                labels[i] = new LabelNode();

                i++;
            }
        }
        LabelNode defaultLabel = new LabelNode();
        LabelNode endSwitchLabel = new LabelNode();
        insnList.add(AsmUtils.toInsnNode(
                ThreadLocalRandom.current().nextInt(0, 3621000))); // I
        insnList.add(new VarInsnNode(Opcodes.ISTORE, ENCRYPTION_KEY));
        insnList.add(AsmUtils.toInsnNode(
                ThreadLocalRandom.current().nextInt(0, 3621000))); // I
        insnList.add(new VarInsnNode(Opcodes.ISTORE, ENCRYPTION_INC));
        insnList.add(new VarInsnNode(Opcodes.ILOAD, INDEX)); // I
        insnList.add(new LookupSwitchInsnNode(defaultLabel, keys, labels));
        {
            int i = 0;
            for (StringMetadata meta : encryptedStrings){
                insnList.add(labels[i]);
                insnList.add(new FrameNode(Opcodes.F_NEW, 6,
                        new Object[]{1, 1, 1, "java/lang/String", 1, 1},
                        0, new Object[0]));
                insnList.add(AsmUtils.toInsnNode(meta.getKey() ^
                        meta.getParentMethod().getStartingLineNumber())); // I
                insnList.add(new VarInsnNode(Opcodes.ILOAD, LINE_NUM_INDEX)); // I, I
                insnList.add(new InsnNode(Opcodes.IXOR)); // I
                insnList.add(new VarInsnNode(Opcodes.ISTORE, ENCRYPTION_KEY));
                insnList.add(AsmUtils.toInsnNode(meta.getInc() ^
                        meta.getParentMethod().getStartingLineNumber())); // I
                insnList.add(new VarInsnNode(Opcodes.ILOAD, LINE_NUM_INDEX)); // I, I
                insnList.add(new InsnNode(Opcodes.IXOR)); // I
                insnList.add(new VarInsnNode(Opcodes.ISTORE, ENCRYPTION_INC));
                insnList.add(new JumpInsnNode(Opcodes.GOTO, endSwitchLabel));

                i++;
            }
        }
        insnList.add(defaultLabel);
        insnList.add(new FrameNode(Opcodes.F_NEW, 6,
                new Object[]{1, 1, 1, "java/lang/String", 1, 1},
                0, new Object[0]));
        insnList.add(new VarInsnNode(Opcodes.ILOAD, INDEX));
        insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/String",
                "valueOf", "(I)Ljava/lang/String;"));
        insnList.add(new InsnNode(Opcodes.ARETURN));
        insnList.add(endSwitchLabel);
        insnList.add(new FrameNode(Opcodes.F_NEW, 6,
                new Object[]{1, 1, 1, "java/lang/String", 1, 1},
                0, new Object[0]));

        // Now we write the decryption routine
        LabelNode loopStart = new LabelNode();
        LabelNode loopEnd = new LabelNode();
        insnList.add(AsmUtils.toInsnNode(0)); // I
        insnList.add(new VarInsnNode(Opcodes.ISTORE, LOOP_INDEX));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, ENCRYPTED_STRING)); // String
        insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String",
                "toCharArray", "()[C")); // [C
        insnList.add(loopStart);
        insnList.add(new FrameNode(Opcodes.F_NEW, 7,
                new Object[]{1, 1, 1, "java/lang/String", 1, 1, 1},
                1, new Object[]{"[C"}));
        insnList.add(new InsnNode(Opcodes.DUP)); // [C, [C
        insnList.add(new InsnNode(Opcodes.ARRAYLENGTH)); // I, [C
        insnList.add(new VarInsnNode(Opcodes.ILOAD, LOOP_INDEX)); // I, I, [C
        insnList.add(new InsnNode(Opcodes.SWAP)); // I, I, [C
        insnList.add(new JumpInsnNode(Opcodes.IF_ICMPGE, loopEnd)); // [C
        insnList.add(new InsnNode(Opcodes.DUP)); // [C, [C
        insnList.add(new InsnNode(Opcodes.DUP)); // [C, [C, [C
        insnList.add(new VarInsnNode(Opcodes.ILOAD, LOOP_INDEX)); // I, [C, [C, [C
        insnList.add(new InsnNode(Opcodes.CALOAD)); // C, [C, [C
        insnList.add(new VarInsnNode(Opcodes.ILOAD, ENCRYPTION_INC)); // I, C, [C, [C
        insnList.add(new VarInsnNode(Opcodes.ILOAD, LOOP_INDEX)); // I, I, C, [C, [C
        insnList.add(AsmUtils.toInsnNode(1)); // I, I, I, C, [C, [C
        insnList.add(new InsnNode(Opcodes.IADD)); // I, I, C, [C, [C
        insnList.add(new VarInsnNode(Opcodes.ILOAD, ENCRYPTION_KEY)); // I, I, I, C, [C, [C
        insnList.add(new InsnNode(Opcodes.IMUL)); // I, I, C, [C, [C
        insnList.add(new InsnNode(Opcodes.IMUL)); // I, C, [C, [C
        insnList.add(new InsnNode(Opcodes.IXOR)); // I, [C, [C
        insnList.add(new InsnNode(Opcodes.I2C)); // C, [C, [C
        insnList.add(new VarInsnNode(Opcodes.ILOAD, LOOP_INDEX)); // I, C, [C, [C
        insnList.add(new InsnNode(Opcodes.SWAP)); // C, I, [C, [C
        insnList.add(new InsnNode(Opcodes.CASTORE)); // [C
        insnList.add(new IincInsnNode(LOOP_INDEX, 1));
        insnList.add(new JumpInsnNode(Opcodes.GOTO, loopStart));
        insnList.add(loopEnd);
        insnList.add(new FrameNode(Opcodes.F_NEW, 7,
                new Object[]{1, 1, 1, "java/lang/String", 1, 1, 1},
                1, new Object[]{"[C"}));

        // Create new string and return
        insnList.add(new VarInsnNode(Opcodes.ASTORE, FINAL_CHARS));
        insnList.add(new TypeInsnNode(Opcodes.NEW, "java/lang/String")); // String
        insnList.add(new InsnNode(Opcodes.DUP)); // String, String
        insnList.add(new VarInsnNode(Opcodes.ALOAD, FINAL_CHARS)); // [C, String, String
        insnList.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/String",
                "<init>", "([C)V")); // String
        insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String",
                "intern", "()Ljava/lang/String;")); // String
        insnList.add(new InsnNode(Opcodes.DUP)); // String, String
        insnList.add(new FieldInsnNode(Opcodes.GETSTATIC, decryptMembersOwner.name,
                decryptedValueCacheArray.name, decryptedValueCacheArray.desc)); // [String, String, String
        insnList.add(new InsnNode(Opcodes.SWAP)); // String, [String, String
        insnList.add(new VarInsnNode(Opcodes.ILOAD, INDEX)); // I, String, [String, String
        insnList.add(new InsnNode(Opcodes.SWAP)); // String, I, [String, String
        insnList.add(new InsnNode(Opcodes.AASTORE)); // String
        insnList.add(new InsnNode(Opcodes.ARETURN));

        decryptMethod.instructions = insnList;
    }

    /**
     * Creates the instructions that should be injected into the top of the \<clinit\> method
     * @return An InsnList containing the needed instructions
     */
    private InsnList createClinitInstructions(Set<MethodNode> calledDecryptionMethods){
        InsnList clinitInsns = new InsnList();

        // Start by creating the new arrays
        clinitInsns.add(AsmUtils.toInsnNode(encryptedStrings.size())); // I
        clinitInsns.add(new TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/String")); // [String
        clinitInsns.add(new FieldInsnNode(Opcodes.PUTSTATIC, decryptMembersOwner.name,
                encryptedStringsField.name, encryptedStringsField.desc));
        clinitInsns.add(AsmUtils.toInsnNode(encryptedStrings.size())); // I
        clinitInsns.add(new TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/String")); // [String
        clinitInsns.add(new FieldInsnNode(Opcodes.PUTSTATIC, decryptMembersOwner.name,
                decryptedValueCacheArray.name, decryptedValueCacheArray.desc));

        // Now we call all the population methods
        for (MethodNode methodNode : calledDecryptionMethods){
            clinitInsns.add(AsmUtils.generateMethodCallFromNode(methodNode,
                    decryptMembersOwner, false));
        }
        return clinitInsns;
    }

    /**
     * Creates extra methods that populate the encrypted strings array.
     * We do this to ensure we never cause a MethodTooLargeException.
     * @param entry The class entry that these methods will be going into
     * @return A set of all the created decryption methods
     */
    private Set<MethodNode> createExtraArrayPopulationMethods(ClassEntry entry){
        Set<MethodNode> calledDecryptionMethods = new HashSet<>();
        MethodNode currentMethod = null;
        int i = 0;
        for (StringMetadata meta : encryptedStrings){
            if (i % 75 == 0){
                if (currentMethod != null){
                    // At this point we have one instance of the array left on our stack,
                    // so we pop and return to end our method
                    currentMethod.instructions.add(new InsnNode(Opcodes.POP));
                    currentMethod.instructions.add(new InsnNode(Opcodes.RETURN));
                }
                currentMethod = new MethodNode(
                        Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                        AsmUtils.getUniqueMethodName(entry, config),
                        "()V",
                        null,
                        new String[0]);
                currentMethod.instructions = new InsnList();
                // Loads our array from the params
                currentMethod.instructions.add(new FieldInsnNode(Opcodes.GETSTATIC,
                        decryptMembersOwner.name, encryptedStringsField.name,
                        encryptedStringsField.desc));
                calledDecryptionMethods.add(currentMethod);
                decryptMembersOwner.methods.add(currentMethod);
            }

            InsnList insns = currentMethod.instructions;
            insns.add(new InsnNode(Opcodes.DUP)); // [String, [String
            insns.add(AsmUtils.toInsnNode(i)); // int, [String, [String
            insns.add(new LdcInsnNode(meta.getEncrypted())); // String, int, [String, [String
            insns.add(new InsnNode(Opcodes.AASTORE)); // [String
            stringsEncrypted++;

            i++;
        }
        if (currentMethod == null)
            return calledDecryptionMethods;

        // At this point we have one instance of the array left on our stack,
        // so we pop and return to end our method
        currentMethod.instructions.add(new InsnNode(Opcodes.POP));
        currentMethod.instructions.add(new InsnNode(Opcodes.RETURN));

        return calledDecryptionMethods;
    }

    private int currentArrayIndex = 0;
    /**
     * Replaces LDC instructions that load strings with method calls to
     * our decryption method
     * @param entry The class entry to process
     */
    private void replaceStringConstantLoads(ClassEntry entry){
        currentArrayIndex = 0;
        final int MAX_UTF8_STRING_SIZE = 65535; // This is a value pulled from inside ASM.
                                                // If we write new strings larger than that
                                                // then ASM will crash.
        AsmUtils.loopOverAllInsn(entry, (methodNode, abstractInsnNode) -> {
            if (!(abstractInsnNode instanceof LdcInsnNode)
                    || !(((LdcInsnNode)abstractInsnNode).cst instanceof String)
                    || ((String)(((LdcInsnNode)abstractInsnNode).cst))
                    .toCharArray().length >= MAX_UTF8_STRING_SIZE / 8) // 8 bytes per character.
                return;

            LdcInsnNode stringConstantLoad = (LdcInsnNode) abstractInsnNode;
            StringMetadata meta = null;
            final int maxAttempts = 256;
            int attempts = 0;
            do{
                meta = new StringMetadata((String)stringConstantLoad.cst,
                        currentArrayIndex,
                        ThreadLocalRandom.current().nextInt(3621, 362100),
                        ThreadLocalRandom.current().nextInt(1, 255),
                        methodMetadataMap.get(methodNode));
                attempts++;
                if (attempts >= maxAttempts){
                    logger.warn(String.format("String \"%s\" could not be encrypted " +
                            "without potentially compromising the integrity of the string " +
                            "decrypter.%nThis may happen if a unique encrypted value " +
                            "cannot be generated (String too short/too many strings in class)",
                            (String)stringConstantLoad.cst));
                    return;
                }
            }while(encryptedStrings.contains(meta));
            currentArrayIndex++;
            encryptedStrings.add(meta);

            // We now replace the string load with a method call to our decrypt method
            InsnList callDecryptMethod = new InsnList();

            callDecryptMethod.add(AsmUtils.toInsnNode(meta.getIndex() ^
                    meta.getParentMethod().getStartingLineNumber()));
            callDecryptMethod.add(AsmUtils.generateMethodCallFromNode(decryptMethod,
                    decryptMembersOwner, false));

            methodNode.instructions.insertBefore(abstractInsnNode, callDecryptMethod);
            methodNode.instructions.remove(abstractInsnNode);

        });
    }

    /**
     * Creates the member objects needed to decrypt the strings
     * @param entry The class entry to process
     */
    private void createDecryptMembers(ClassEntry entry){
        encryptedStringsField = new FieldNode(
                Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                AsmUtils.getUniqueFieldName(entry, config), "[Ljava/lang/String;",
                null,
                null
        );
        decryptedValueCacheArray = new FieldNode(
                Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                AsmUtils.getUniqueFieldName(entry, config), "[Ljava/lang/String;",
                null,
                null
        );
        decryptMethod = new MethodNode(
                Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                AsmUtils.getUniqueMethodName(entry, config),
                "(I)Ljava/lang/String;",
                null, new String[0]
        );
        decryptMembersOwner = entry.getContent();

        // We can't add fields or static methods to interfaces, so we instead
        // create a new class just for this one. (non-static default methods
        // may still contain String LDCs)
        if (Modifier.isInterface(entry.getContent().access)
                || Modifier.isAbstract(entry.getContent().access)){
            decryptMembersOwner = new ClassNode();
            decryptMembersOwner.visit(
                    entry.getContent().version,
                    Opcodes.ACC_PUBLIC,
                    AsmUtils.getUniqueClassPath(entry, config),
                    null,
                    "java/lang/Object",
                    new String[0]
            );
            classesCreated++;

            // This adds the new class to the output jar. Needed if we're making
            // new classes because otherwise we won't have the class
            entry.addNodeToEnd(new ClassEntry(decryptMembersOwner.name,
                    true/* Will not create an infinite loop because we
                    only create class for interfaces (the created class
                    is not an interface)*/,
                    decryptMembersOwner));
        }

        decryptMembersOwner.methods.add(decryptMethod);
        decryptMembersOwner.fields.add(encryptedStringsField);
        decryptMembersOwner.fields.add(decryptedValueCacheArray);
        methodsCreated++;
        fieldsCreated += 2;
    }

    private int currentLineNumber = 0;
    /**
     * Assigns a new line number to each method
     * @param entry The class entry to modify
     */
    private void assignLineNumbersToEveryMethod(ClassEntry entry) {
        currentLineNumber = ThreadLocalRandom.current().nextInt(3621, 10000);
        final int startingLineNumber = currentLineNumber;
        final int incrementAmount = ThreadLocalRandom.current().nextInt(0b1111, 0b11111111);
        AsmUtils.loopOverMethods(entry, methodNode -> {
            if (Modifier.isAbstract(methodNode.access)
                    || methodNode.instructions == null
                    || methodNode.instructions.size() == 0)
                return;

            currentLineNumber += incrementAmount;
            int lineNumber = currentLineNumber;
            MethodMetadata meta = new MethodMetadata(lineNumber,
                    incrementAmount);
            methodMetadataMap.put(methodNode, meta); // Needed to keep track of changes
            InsnList addLineNumber = new InsnList();
            LabelNode lineLabel = new LabelNode();
            LineNumberNode lineNumberNode = new LineNumberNode(lineNumber, lineLabel);
            addLineNumber.add(lineLabel);
            addLineNumber.add(lineNumberNode);

            AbstractInsnNode firstNode = methodNode.instructions.getFirst();
            methodNode.instructions.insertBefore(firstNode, addLineNumber);
        });
    }

    /**
     * Scans a class for string constant loads and returns true if any are found
     * @param entry The entry to scan
     * @return True if string constant loads were found, otherwise false
     */
    private static boolean isFoundString(ClassEntry entry) {
        for (MethodNode methodNode : entry.getContent().methods){
            for (AbstractInsnNode abstractInsnNode : methodNode.instructions){
                if (!(abstractInsnNode instanceof LdcInsnNode))
                    continue;
                LdcInsnNode ldc = (LdcInsnNode) abstractInsnNode;
                if (!(ldc.cst instanceof String))
                    continue;
                return true;
            }
        }
        return false;
    }

    /**
     * Scrubs all line number nodes from a class
     * @param entry The class entry to scrub
     */
    private void scrubLineNumbers(ClassEntry entry){
        AsmUtils.loopOverAllInsn(entry, ((methodNode, abstractInsnNode) -> {
            if (!(abstractInsnNode instanceof LineNumberNode))
                return;
            methodNode.instructions.remove(abstractInsnNode);
            lineNumbersRemoved++;
        }));
    }

    @Override
    public void cleanup() {
        methodMetadataMap = new HashMap<>();
        encryptedStrings = new LinkedHashSet<>();
        decryptMethod = null;
        encryptedStringsField = null;
        decryptedValueCacheArray = null;

        logger.info(String.format("Encrypted %s string literals", stringsEncrypted));
        logger.info(String.format("Removed %s line number nodes", lineNumbersRemoved));
        logger.info(String.format("Created %s classes", classesCreated));
        logger.info(String.format("Created %s methods", methodsCreated));
        logger.info(String.format("Created %s fields", fieldsCreated));
    }

}
