package me.bannock.donutguard.obf.mutator.impl.string;

import com.google.inject.Inject;
import me.bannock.donutguard.obf.asm.entry.impl.ClassEntry;
import me.bannock.donutguard.obf.config.Configuration;
import me.bannock.donutguard.obf.config.DefaultConfigGroup;
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
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
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
    private int stringsEncrypted = 0, lineNumbersRemoved = 0, methodsCreated = 0, fieldsCreated = 0;
    private Map<MethodNode, MethodMetadata> methodMetadataMap;
    private Set<StringMetadata> encryptedStrings;
    private ClassNode decryptMembersOwner;
    private MethodNode decryptMethod;
    private FieldNode encryptedStringsField, decryptedValueCacheArray;

    @Inject
    public LineNumberStringLiteralMutator(Configuration config) {
        super("String Literal Encryption",
                DefaultConfigGroup.STRING_ENC_ENABLED.get(config)
                        && DefaultConfigGroup.STRING_ENC_TYPE.get(config) ==
                        StringLiteralEncryptionType.IDENTIFIERS_VIA_LINE_NUMBERS_AND_INTS);
        this.config = config;
    }

    @Override
    public void setup() {
        stringsEncrypted = 0;
        lineNumbersRemoved = 0;
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

        final int INDEX_PARAM = 0;
        final int LINE_NUM_INDEX = 1;

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

        insnList.add(new VarInsnNode(Opcodes.ILOAD, INDEX_PARAM)); // I
        insnList.add(new VarInsnNode(Opcodes.ILOAD, LINE_NUM_INDEX)); // I, I
        insnList.add(new InsnNode(Opcodes.IXOR)); // I
        insnList.add(new VarInsnNode(Opcodes.ISTORE, LINE_NUM_INDEX));

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
            insns.add(new LdcInsnNode(encryptString(meta))); // String, int, [String, [String
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

    /**
     * Encrypts a string with its metadata
     * @param meta The metadata object for the string
     * @return The encrypted string
     */
    private String encryptString(StringMetadata meta){
        Objects.requireNonNull(meta);

        char[] chars = meta.getValue().toCharArray();
        for (int i = 0; i < chars.length; i++){
            chars[i] = (char)(chars[i] ^ (meta.getInc() * i * meta.getKey()));
        }

        return new String(chars).intern();
    }

    private int currentArrayIndex = 0;
    /**
     * Replaces LDC instructions that load strings with method calls to
     * our decryption method
     * @param entry The class entry to process
     */
    private void replaceStringConstantLoads(ClassEntry entry){
        currentArrayIndex = 0;
        AsmUtils.loopOverAllInsn(entry, (methodNode, abstractInsnNode) -> {
            if (!(abstractInsnNode instanceof LdcInsnNode)
                    || !(((LdcInsnNode)abstractInsnNode).cst instanceof String))
                return;

            LdcInsnNode stringConstantLoad = (LdcInsnNode) abstractInsnNode;
            StringMetadata meta = null;
            do{
                meta = new StringMetadata((String)stringConstantLoad.cst,
                        currentArrayIndex++,
                        ThreadLocalRandom.current().nextInt(3621, 36210),
                        ThreadLocalRandom.current().nextInt(1, 255),
                        methodMetadataMap.get(methodNode));
            }while(encryptedStrings.contains(meta));
            encryptedStrings.add(meta);

            // We now replace the string load with a method call to our decrypt method
            InsnList callDecryptMethod = new InsnList();

            callDecryptMethod.add(AsmUtils.toInsnNode(meta.getIndex() ^
                    meta.getParentMethod().getXorValue()));
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

            final int hideLineNumIncrementXor = ThreadLocalRandom.current().nextInt(3621, 10000);
            MethodMetadata meta = new MethodMetadata(startingLineNumber,
                    incrementAmount, hideLineNumIncrementXor);
            methodMetadataMap.put(methodNode, meta); // Needed to keep track of changes

            // We xor the number to make the numbers seem more random
            int lineNumber = (currentLineNumber += incrementAmount) ^ hideLineNumIncrementXor;
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
        logger.info(String.format("Created %s methods", methodsCreated));
        logger.info(String.format("Created %s fields", fieldsCreated));
    }

}
