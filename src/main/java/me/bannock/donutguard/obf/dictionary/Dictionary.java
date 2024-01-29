package me.bannock.donutguard.obf.dictionary;

public enum Dictionary {

    ALPHABET("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"),
    UPPERCASE_ALPHABET("ABCDEFGHIJKLMNOPQRSTUVWXYZ"),
    LOWERCASE_ALPHABET("abcdefghijklmnopqrstuvwxyz");

    private final char[] localVarChars, methodChars, fieldChars, classChars, packageChars;

    Dictionary(String allChars){
        this(allChars, allChars, allChars, allChars, allChars);
    }

    Dictionary(String localVarChars, String methodChars,
               String fieldChars, String classChars,
               String packageChars){
        this.localVarChars = localVarChars.toCharArray();
        this.methodChars = methodChars.toCharArray();
        this.fieldChars = fieldChars.toCharArray();
        this.classChars = classChars.toCharArray();
        this.packageChars = packageChars.toCharArray();
    }

    /**
     * Creates and returns a unique local variable name
     * @return A unique local variable name
     */
    public String uniqueLocal(){
        return uniqueSequence(this.localVarChars);
    }

    /**
     * Creates and returns a unique method name
     * @return A unique method name
     */
    public String uniqueMethod(){
        return uniqueSequence(this.methodChars);
    }

    /**
     * Creates and returns a unique field name
     * @return A unique field name
     */
    public String uniqueField(){
        return uniqueSequence(this.fieldChars);
    }

    /**
     * Creates and returns a unique class name
     * @return A unique class name
     */
    public String uniqueClass(){
        return uniqueSequence(this.classChars);
    }

    /**
     * Creates and returns a unique package name
     * @return A unique package name
     */
    public String uniquePackage(){
        return uniqueSequence(this.packageChars);
    }

    private String uniqueSequence(char[] chars){
        long nanos = System.nanoTime();
        if (nanos < 0)
            nanos += Long.MAX_VALUE;
        int radix = chars.length;

        // First, we need to get the maximum number of bases for the given nanos
        int bases = 1;
        while (Math.pow(radix, bases) < Math.abs(nanos)){
            bases++;
        }
        bases--;

        // Now we calculate the number, using our chars in place of something like
        // octal or hex
        StringBuilder sequenceBuilder = new StringBuilder();
        while (bases >= 0){
            int value = (int)Math.floorDiv(nanos, (long)Math.pow(radix, bases));
            nanos -= ((long)value * (long)Math.pow(radix, bases));
            sequenceBuilder.append(chars[value]);
            bases--;
        }
        return sequenceBuilder.toString();
    }

    public char[] getLocalVarChars() {
        return localVarChars;
    }

    public char[] getMethodChars() {
        return methodChars;
    }

    public char[] getFieldChars() {
        return fieldChars;
    }

    public char[] getClassChars() {
        return classChars;
    }

    public char[] getPackageChars() {
        return packageChars;
    }

}
