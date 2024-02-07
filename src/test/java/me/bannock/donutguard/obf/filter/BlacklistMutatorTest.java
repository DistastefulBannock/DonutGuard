package me.bannock.donutguard.obf.filter;

import com.google.inject.Inject;
import me.bannock.donutguard.obf.asm.entry.impl.ClassEntry;
import me.bannock.donutguard.obf.asm.entry.impl.ResourceEntry;
import me.bannock.donutguard.obf.mutator.Mutator;

public class BlacklistMutatorTest extends Mutator {

    @Inject
    public BlacklistMutatorTest() {
        super("BlacklistMutatorTest", true);
    }

    @Override
    public void firstPassClassTransform(ClassEntry entry) {
        entry.removeNode();
    }

    @Override
    public void firstPassResourceTransform(ResourceEntry entry) {
        entry.removeNode();
    }

}
