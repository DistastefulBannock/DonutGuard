package me.bannock.donutguard.obf.mutator;

import me.bannock.donutguard.obf.asm.impl.ClassEntry;
import me.bannock.donutguard.obf.asm.impl.ResourceEntry;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.function.Consumer;

/**
 * The mutator base class.
 * Non-mutable entries will not be passed into mutators.
 * Methods should be called in this order:
 * 1. setup (called on every mutator before proceeding)
 * 2. firstClassPassTransform (called for every class entry; does not yet move to next mutator)
 * 3. firstPassResourceTransform (called for every resource entry; does not yet move to next mutator)
 * 4. intermission (called once; does not yet move to next mutator)
 * 5. secondClassPassTransform (called for every class entry; does not yet move to next mutator)
 * 6. secondPassResourceTransform (called for every resource entry; finally moves to next mutator)
 * 7. cleanup (called on every mutator; still called before class writer consumers)
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

    /**
     * Iterates over every method in a ClassNode
     * @param node The ClassNode to fetch methods from
     * @param consumer The consumer to call back to
     */
    protected void loopOverMethods(ClassNode node, Consumer<MethodNode> consumer){
        node.methods.forEach(consumer);
    }

    /**
     * Iterates over every method in a ClassEntry's ClassNode
     * @param entry The entry to pull the ClassNode from
     * @param consumer The consumer to call back to
     */
    protected void loopOverMethods(ClassEntry entry, Consumer<MethodNode> consumer){
        loopOverMethods(entry.getContent(), consumer);
    }

}
