package me.bannock.donutguard.ui.obf.settings.list.impl;

import me.bannock.donutguard.obf.ConfigDTO;
import me.bannock.donutguard.ui.obf.settings.list.ListSetting;

import java.io.File;
import java.util.List;

public class FileListSetting extends ListSetting<File> {


    public FileListSetting(String name, ConfigDTO config, List<File> value, String fieldName) {
        super(name, config, value, fieldName);
    }

    @Override
    protected File getInputFieldValue() throws RuntimeException {
        return new File(getInputField().getText());
    }

}
