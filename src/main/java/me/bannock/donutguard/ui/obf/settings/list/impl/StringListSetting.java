package me.bannock.donutguard.ui.obf.settings.list.impl;

import me.bannock.donutguard.obf.ConfigDTO;
import me.bannock.donutguard.ui.obf.settings.list.ListSetting;

import java.util.List;

public class StringListSetting extends ListSetting<String> {

    public StringListSetting(String name, ConfigDTO config, List<String> value, String fieldName) {
        super(name, config, value, fieldName);
    }

    @Override
    protected String getInputFieldValue() throws RuntimeException {
        return getInputField().getText();
    }

}
