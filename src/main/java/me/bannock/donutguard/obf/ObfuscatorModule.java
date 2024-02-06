package me.bannock.donutguard.obf;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.Multibinder;
import me.bannock.donutguard.obf.mutator.Mutator;
import me.bannock.donutguard.obf.mutator.impl.NopSpammerMutator;
import me.bannock.donutguard.obf.mutator.impl.TestMutator;
import me.bannock.donutguard.obf.mutator.impl.string.LineNumberStringLiteralMutator;

import java.util.function.Supplier;


public class ObfuscatorModule extends AbstractModule {

    private final Supplier<ConfigDTO> configSupplier;

    /**
     * @param configSupplier A supplier that will be called to provide
     *                       the ConfigDTO. Useful if your implementation
     *                       uses an ever-changing field or similar.
     */
    public ObfuscatorModule(Supplier<ConfigDTO> configSupplier){
        this.configSupplier = configSupplier;
    }

    /**
     * @param configInstance Uses this instance of the ConfigDTO object. Useful if you
     *                       only intend to use a single object reference for your
     *                       implementation
     */
    public ObfuscatorModule(ConfigDTO configInstance){
        this.configSupplier = () -> configInstance;
    }

    @Override
    protected void configure() {
        bindMutators();
    }

    private void bindMutators(){
        Multibinder<Mutator> mutatorMultibinder = Multibinder.newSetBinder(binder(), Mutator.class);
        mutatorMultibinder.addBinding().to(NopSpammerMutator.class);
        mutatorMultibinder.addBinding().to(LineNumberStringLiteralMutator.class);

        mutatorMultibinder.addBinding().to(TestMutator.class); // It's for testing so it's last
    }

    @Provides
    public ConfigDTO provideConfig(){
        return this.configSupplier.get();
    }

}
