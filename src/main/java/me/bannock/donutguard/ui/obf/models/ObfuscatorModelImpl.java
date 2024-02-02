package me.bannock.donutguard.ui.obf.models;

import com.google.inject.Inject;
import me.bannock.donutguard.obf.ConfigDTO;
import me.bannock.donutguard.obf.dictionary.Dictionary;
import me.bannock.donutguard.obf.mutator.impl.string.StringLiteralEncryptionType;
import me.bannock.donutguard.ui.components.HelpButton;
import me.bannock.donutguard.ui.obf.settings.impl.BooleanSetting;
import me.bannock.donutguard.ui.obf.settings.impl.EnumSetting;
import me.bannock.donutguard.ui.obf.settings.impl.FileSetting;
import me.bannock.donutguard.ui.obf.settings.impl.IntegerSetting;
import me.bannock.donutguard.ui.obf.settings.list.impl.file.FileListSetting;
import me.bannock.donutguard.ui.obf.settings.list.impl.StringListSetting;
import me.bannock.donutguard.ui.obf.settings.list.impl.file.impl.MavenPomFileHandlerImpl;
import me.bannock.donutguard.ui.obf.settings.list.impl.file.impl.ZipAndJarRecursiveDirSearcherImpl;
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

        // This code is a mess, but it isn't difficult to follow.
        // The strings in the list are used to create buttons, which display
        // their associated JComponent when clicked.
        // ObfuscatorSettingsView is a view that holds and manages setting objects.
        Map<String, JComponent> obfuscatorViews = new LinkedHashMap<>();
        obfuscatorViews.put("IO Settings", new ObfuscatorSettingsView(
                new FileSetting("input jar", config, config.input, "input",
                        ".jar, .zip","jar", "zip"),
                new FileSetting("output jar", config, config.output, "output",
                        ".jar, .zip","jar", "zip"),
                new BooleanSetting("Include libs in output", config, config.includeLibsInOutput,
                        "includeLibsInOutput"),
                new BooleanSetting("Suppress \"Node of same path\" error", config,
                        config.suppressNodeOfSamePathError, "suppressNodeOfSamePathError")
        ));
        obfuscatorViews.put("Libraries", new ObfuscatorSettingsView(
                new FileListSetting("Libraries", config, config.libraries, "libraries",
                        ".zip, .jar", "zip", "jar") // TODO: Once finished, add xml for poms
                        .withFileHandlers(
                                new ZipAndJarRecursiveDirSearcherImpl(),
                                new MavenPomFileHandlerImpl()
                        )
                        .withHelpComponent(new HelpButton(
                                ResourceUtils.readString("ui/tooltips/librariesToolTip.html")))
        ));
        obfuscatorViews.put("ClassWriter Settings", new ObfuscatorSettingsView(
                new BooleanSetting("Compute Frames (Very Resource Intensive)", config,
                        config.computeFrames, "computeFrames"),
                new BooleanSetting("Compute Maxes", config, config.computeMaxes, "computeMaxes")
        ));
        obfuscatorViews.put("Blacklist (regex)", new ObfuscatorSettingsView(
                new StringListSetting("Blacklist (regex)", config, config.blacklist, "blacklist")
                        .withHelpComponent(new HelpButton(
                                ResourceUtils.readString("ui/tooltips/blacklistToolTip.html"))
                                .withLinkAction(new URI("https://regex101.com/")))
        ));
        obfuscatorViews.put("Whitelist (regex)", new ObfuscatorSettingsView(
                new StringListSetting("Whitelist (regex)", config, config.whitelist, "whitelist")
                        .withHelpComponent(new HelpButton(
                                ResourceUtils.readString("ui/tooltips/whitelistToolTip.html"))
                                .withLinkAction(new URI("https://regex101.com/")))
        ));
        obfuscatorViews.put("Dictionary Settings", new ObfuscatorSettingsView(
                new EnumSetting<>("Local variable dict", config,
                        config.localVariableDict, "localVariableDict",
                        Dictionary.values()),
                new EnumSetting<>("Method dict", config,
                        config.methodDict, "methodDict",
                        Dictionary.values()),
                new EnumSetting<>("Field dict", config,
                        config.fieldDict, "fieldDict",
                        Dictionary.values()),
                new EnumSetting<>("Class dict", config,
                        config.classDict, "classDict",
                        Dictionary.values()),
                new EnumSetting<>("Package dict", config,
                        config.packageDict, "packageDict",
                        Dictionary.values()),
                new IntegerSetting("Nested Package Count", config, config.nestedPackages,
                        "nestedPackages", 0, 50, 1)
        ));
        obfuscatorViews.put("NOP Spammer", new ObfuscatorSettingsView(
                new BooleanSetting("Mutator Enabled", config, config.nopSpammerEnabled,
                        "nopSpammerEnabled"),
                new IntegerSetting("NOPs per instruction", config, config.nopsPerInstruction,
                        "nopsPerInstruction", 1, 100, 1)
        ));
        obfuscatorViews.put("String Encryption", new ObfuscatorSettingsView(
                new BooleanSetting("String Literal Encryption Enabled",
                        config, config.stringLiteralEncryptionEnabled,
                        "stringLiteralEncryptionEnabled"),
                new EnumSetting<>("String Encryption Variant", config,
                        config.stringLiteralEncryptionType, "stringLiteralEncryptionType",
                        StringLiteralEncryptionType.values())
        ));
        return obfuscatorViews;
    }
}
