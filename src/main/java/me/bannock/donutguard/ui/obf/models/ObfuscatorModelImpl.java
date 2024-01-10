package me.bannock.donutguard.ui.obf.models;

import com.google.inject.Inject;
import me.bannock.donutguard.obf.ConfigDTO;
import me.bannock.donutguard.ui.obf.settings.impl.BooleanSetting;
import me.bannock.donutguard.ui.obf.settings.impl.FileSetting;
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
        obfuscatorViews.put("ClassWriter Settings", new ObfuscatorSettingsView(
                new BooleanSetting("Compute Frames", config, config.computeFrames, "computeFrames"),
                new BooleanSetting("Compute Maxes", config, config.computeMaxes, "computeMaxes")
        ));
        obfuscatorViews.put("IO Settings", new ObfuscatorSettingsView(
                new FileSetting("input jar", config, config.input, "input",
                        ".jar, .zip","jar", "zip"),
                new FileSetting("output jar", config, config.output, "output",
                        ".jar, .zip","jar", "zip")
        ));
        obfuscatorViews.put("Dictionary Settings", new ObfuscatorSettingsView());
        return obfuscatorViews;
    }
}
