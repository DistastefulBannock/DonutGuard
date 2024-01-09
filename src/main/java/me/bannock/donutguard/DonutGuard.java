package me.bannock.donutguard;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import me.bannock.donutguard.obf.ConfigDTO;
import me.bannock.donutguard.ui.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class DonutGuard {

    static{
        // First, we set the look and feel because it looks better this way
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception ignored) {}

        // I hate large scrollbars, so this is the soulution
        UIManager.put("ScrollBar.width", 5);
    }

    private final Injector injector;
    private ConfigDTO config;

    @Inject
    public DonutGuard(Injector injector) {
        this.injector = injector;
        this.config = new ConfigDTO();
    }

    private void start(){
        // We start the gui on the swing thread
        SwingUtilities.invokeLater(() -> injector.getInstance(MainFrame.class).start());
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
        Guice.createInjector(new DonutGuardModule()).getInstance(DonutGuard.class).start();
    }

}
