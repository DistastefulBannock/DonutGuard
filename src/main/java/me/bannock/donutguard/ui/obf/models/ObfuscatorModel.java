package me.bannock.donutguard.ui.obf.models;


import javax.swing.JComponent;
import java.util.Map;

public interface ObfuscatorModel {

    /**
     * @return The different views for the obfuscator
     */
    Map<String, JComponent> getObfuscatorViews();

}