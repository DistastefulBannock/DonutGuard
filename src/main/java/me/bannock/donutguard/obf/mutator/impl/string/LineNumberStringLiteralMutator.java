package me.bannock.donutguard.obf.mutator.impl.string;

import com.google.inject.Inject;
import me.bannock.donutguard.obf.ConfigDTO;
import me.bannock.donutguard.obf.asm.impl.ClassEntry;
import me.bannock.donutguard.obf.mutator.Mutator;
import me.bannock.donutguard.obf.mutator.impl.string.linenum.MethodMetadata;
import me.bannock.donutguard.obf.mutator.impl.string.linenum.StringMetadata;
import me.bannock.donutguard.utils.AsmUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Modifier;
import java.util.HashMap;
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
    private final ConfigDTO config;
    private int stringsEncrypted = 0, lineNumbersRemoved = 0, methodsCreated = 0, fieldsCreated = 0;
    private Map<MethodNode, MethodMetadata> methodMetadataMap;
    private Set<StringMetadata> encryptedStrings;
    private MethodNode decryptMethod;
    private FieldNode encryptedStringsField, decryptedValueCacheArray;

    @Inject
    public LineNumberStringLiteralMutator(ConfigDTO config) {
        super("String Literal Encryption", config.stringLiteralEncryptionEnabled &&
                config.stringLiteralEncryptionType ==
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
        if (Modifier.isAbstract(entry.getContent().access))
            return;
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

        // We now go on to replace all LDC (load constant) instructions
        // that load strings with calls to our decryption method. We also generate
        // the keys used by the decryption method in this step
        replaceStringConstantLoads(entry);

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
            StringMetadata meta = new StringMetadata((String)stringConstantLoad.cst,
                    currentArrayIndex++,
                    ThreadLocalRandom.current().nextInt(3621, 36210),
                    ThreadLocalRandom.current().nextInt(1, 255),
                    methodMetadataMap.get(methodNode));
            encryptedStrings.add(meta);

            // We now replace the string load with a method call to our decrypt method
        });
    }

    private String encryptString(StringMetadata stringMeta){
        Objects.requireNonNull(stringMeta);

        return "";
    }

    private String decryptMethodName;
    /**
     * Creates our decryption method as well as the fields it needs to function
     * @param entry The class entry to check generated names against
     */
    private void createDecryptMethodVariables(ClassEntry entry){
        decryptMethodName = this.config.methodDict.uniqueMethod();
        while (entry.getContent().methods.stream()
                .anyMatch(methodNode -> methodNode.name.equals(decryptMethodName))){
            decryptMethodName = this.config.methodDict.uniqueMethod();
        }
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
            if (Modifier.isAbstract(methodNode.access))
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
