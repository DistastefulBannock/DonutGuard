package me.bannock.donutguard.ui.obf.models;


import javax.swing.JComponent;
import java.util.Map;

public interface ObfuscatorModel {

    /**
     * @return The different views for the obfuscator
     * @throws Exception If something goes wrong while getting the views
     */
    Map<String, JComponent> getObfuscatorViews() throws Exception;

}
