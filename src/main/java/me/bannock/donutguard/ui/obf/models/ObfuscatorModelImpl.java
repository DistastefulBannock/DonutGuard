package me.bannock.donutguard.ui.obf.models;

import com.google.inject.Inject;
import me.bannock.donutguard.obf.ConfigDTO;
import me.bannock.donutguard.ui.components.HelpButton;
import me.bannock.donutguard.ui.obf.settings.impl.BooleanSetting;
import me.bannock.donutguard.ui.obf.settings.impl.FileSetting;
import me.bannock.donutguard.ui.obf.settings.impl.IntegerSetting;
import me.bannock.donutguard.ui.obf.settings.list.impl.FileListSetting;
import me.bannock.donutguard.ui.obf.settings.list.impl.StringListSetting;
import me.bannock.donutguard.ui.obf.views.ObfuscatorSettingsView;
import me.bannock.donutguard.utils.ResourceUtils;

import javax.swing.JComponent;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

public class ObfuscatorModelImpl implements ObfuscatorModel {

    private final ConfigDTO config;

    @Inject
    public ObfuscatorModelImpl(ConfigDTO config) {
        this.config = config;
    }

    @Override
    public Map<String, JComponent> getObfuscatorViews() throws Exception {
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
        obfuscatorViews.put("Blacklist (regex)", new ObfuscatorSettingsView(
                new StringListSetting("Blacklist (regex)", config, config.blacklist, "blacklist")
                        .withHelpComponent(new HelpButton(
                                ResourceUtils.readString("ui/obf/blacklistToolTip.html"))
                                .withLinkAction(new URI("https://regex101.com/")))
        ));
        obfuscatorViews.put("Whitelist (regex)", new ObfuscatorSettingsView(
                new StringListSetting("Whitelist (regex)", config, config.whitelist, "whitelist")
                        .withHelpComponent(new HelpButton(
                                ResourceUtils.readString("ui/obf/whitelistToolTip.html"))
                                .withLinkAction(new URI("https://regex101.com/")))
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
