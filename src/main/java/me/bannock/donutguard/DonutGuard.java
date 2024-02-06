package me.bannock.donutguard;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import me.bannock.donutguard.obf.ConfigDTO;
import me.bannock.donutguard.obf.ObfuscatorModule;
import me.bannock.donutguard.ui.MainFrame;
import me.bannock.donutguard.ui.UiModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

public class DonutGuard {

    static{
        // First, we set the look and feel because it looks better this way
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception ignored) {}

        // I hate large scrollbars, so this is the solution
        UIManager.put("ScrollBar.width", 10);

        // I think the tooltip delay on swing is too long
        ToolTipManager.sharedInstance().setInitialDelay(500);
        // The dismiss delay is stupid as well
        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
    }

    private final Logger logger = LogManager.getLogger();
    private final String mainApplicationThreadId = "Main Application"; // This is used for logging purposes

    private final Injector injector;
    private ConfigDTO config;

    @Inject
    public DonutGuard(Injector injector) {
        ThreadContext.put("threadId", mainApplicationThreadId);
        this.injector = injector;
        this.config = new ConfigDTO();
    }

    private void start(){
        logger.info("Starting DonutGuard ui...");
        // We start the gui on the swing thread
        SwingUtilities.invokeLater(() -> {
            ThreadContext.put("threadId", mainApplicationThreadId);
            injector.getInstance(MainFrame.class).start();
        });
        logger.info("DonutGuard ui successfully started");
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
        Guice.createInjector(
                Modules.override(new ObfuscatorModule(() -> null)).with(new DonutGuardModule()),
                new UiModule()
        ).getInstance(DonutGuard.class).start();
    }

}
