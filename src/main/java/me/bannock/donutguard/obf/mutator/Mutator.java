package me.bannock.donutguard.obf.mutator;

import me.bannock.donutguard.obf.asm.entry.impl.ClassEntry;
import me.bannock.donutguard.obf.asm.entry.impl.ResourceEntry;

/**
 * The mutator base class.
 * Non-mutable entries will not be passed into mutators.
 * Methods should be called in this order:
 * <ol>
 * <li>setup (called on every mutator before proceeding)</li>
 * <li>firstClassPassTransform (called for every class entry; does not yet move to next mutator)</li>
 * <li>firstPassResourceTransform (called for every resource entry; does not yet move to next mutator)</li>
 * <li>intermission (called once; does not yet move to next mutator)</li>
 * <li>secondClassPassTransform (called for every class entry; does not yet move to next mutator)</li>
 * <li>secondPassResourceTransform (called for every resource entry; finally moves to next mutator)</li>
 * <li>cleanup (called on every mutator; still called before class writer consumers)</li>
 * </ol>
 * <br/>
 * This class is created once per ObfuscatorJob. Its enabled state should not change after creation.
 * It is only used for the specific job it was created for. It may run twice if the same job is rerun,
 * but otherwise its usually discarded.
 */
public abstract class Mutator {

    private final String name;
    private final boolean enabled;

    public Mutator(String name, boolean enabled){
        this.name = name;
        this.enabled = enabled;
    }

    /**
     * The setup method. Called first on every mutator when obfuscating.
     */
    public void setup(){}

    /**
     * The first class transformation pass.
     * @param entry The entry currently being transformed
     */
    public void firstPassClassTransform(ClassEntry entry){}

    /**
     * The first resource transformation pass.
     * @param entry The entry being transformed
     */
    public void firstPassResourceTransform(ResourceEntry entry){}

    /**
     * Called between the first and second passes.
     */
    public void intermission(){}

    /**
     * The second class transformation pass.
     * @param entry The entry currently being transformed
     */
    public void secondPassClassTransform(ClassEntry entry){}

    /**
     * The second resource transformation pass.
     * @param entry The entry being transformed
     */
    public void secondPassResourceTransform(ResourceEntry entry){}

    /**
     * The cleanup method. Called last but still before any class writer consumers.
     */
    public void cleanup(){}

    /**
     * @return The name of the mutator.
     */
    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isDisabled() {
        return !isEnabled();
    }

}
