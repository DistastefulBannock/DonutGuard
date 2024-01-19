package me.bannock.donutguard.ui.obf.models;

import com.google.inject.Inject;
import me.bannock.donutguard.obf.ConfigDTO;
import me.bannock.donutguard.ui.obf.settings.impl.BooleanSetting;
import me.bannock.donutguard.ui.obf.settings.impl.FileSetting;
import me.bannock.donutguard.ui.obf.settings.impl.IntegerSetting;
import me.bannock.donutguard.ui.obf.settings.list.impl.FileListSetting;
import me.bannock.donutguard.ui.obf.settings.list.impl.StringListSetting;
import me.bannock.donutguard.ui.obf.views.ObfuscatorSettingsView;

import javax.swing.JComponent;
import java.util.LinkedHashMap;
import java.util.Map;

public class ObfuscatorModelImpl implements ObfuscatorModel {

    private final ConfigDTO config;

    @Inject
    public ObfuscatorModelImpl(ConfigDTO config) {
        this.config = config;
    }

    @Override
    public Map<String, JComponent> getObfuscatorViews() {
        Map<String, JComponent> obfuscatorViews = new LinkedHashMap<>();
        obfuscatorViews.put("IO Settings", new ObfuscatorSettingsView(
                new FileSetting("input jar", config, config.input, "input",
                        ".jar, .zip","jar", "zip"),
                new FileSetting("output jar", config, config.output, "output",
                        ".jar, .zip","jar", "zip")
        ));
        obfuscatorViews.put("Libraries", new ObfuscatorSettingsView(
                new FileListSetting("Libraries", config, config.libraries, "libraries")
        ));
        obfuscatorViews.put("ClassWriter Settings", new ObfuscatorSettingsView(
                new BooleanSetting("Compute Frames (Very Resource Intensive)", config,
                        config.computeFrames, "computeFrames"),
                new BooleanSetting("Compute Maxes", config, config.computeMaxes, "computeMaxes"),
                new BooleanSetting("Include libs in output", config, config.includeLibsInOutput,
                        "includeLibsInOutput")
        ));
        obfuscatorViews.put("Blacklist", new ObfuscatorSettingsView(
                new StringListSetting("Blacklist", config, config.blacklist, "blacklist")
        ));
        obfuscatorViews.put("Whitelist", new ObfuscatorSettingsView(
                new StringListSetting("Whitelist", config, config.whitelist, "whitelist")
        ));
        obfuscatorViews.put("Dictionary Settings", new ObfuscatorSettingsView());
        obfuscatorViews.put("NOP Spammer", new ObfuscatorSettingsView(
                new BooleanSetting("Mutator Enabled", config, config.nopSpammerEnabled,
                        "nopSpammerEnabled"),
                new IntegerSetting("NOPs per instruction", config, config.nopsPerInstruction,
                        "nopsPerInstruction", 1, 100, 1)
        ));
        return obfuscatorViews;
    }
}
