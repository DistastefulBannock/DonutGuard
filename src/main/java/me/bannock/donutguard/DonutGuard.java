package me.bannock.donutguard;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.google.inject.Guice;
import com.google.inject.Inject;
import me.bannock.donutguard.obf.ConfigDTO;
import me.bannock.donutguard.views.MainFrame;

import javax.swing.SwingUtilities;

public class DonutGuard {

    // TODO: Create a worker thread for an obfuscator object

    private ConfigDTO config;

    private void start(){
        // We start the gui on the swing thread
        SwingUtilities.invokeLater(() -> {
            Guice.createInjector(new DonutGuardModule()).getInstance(MainFrame.class).start();
        });
    }

    @Inject
    public DonutGuard(ConfigDTO config){
        this.config = config;
    }

    /**
     * @return The config data transfer object
     */
    public ConfigDTO getConfig() {
        return config;
    }

    /**
     * Sets the config data transfer object
     * @param config The config DTO
     */
    public void setConfig(ConfigDTO config) {
        this.config = config;
    }

    public static void main(String[] args) {
        // Uncomment this if the look of the jbuttons make you want to puke
//        FlatLightLaf.setup();
        Guice.createInjector(new DonutGuardModule()).getInstance(DonutGuard.class).start();
    }

}
