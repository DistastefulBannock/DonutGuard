package me.bannock.donutguard.obf;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;


public class ConfigDTO implements Serializable {

    // Don't change this or old configs will not be able to load anymore
    private static final long serialVersionUID = 3621L;

    // The java file class is really weird because "input.jar" will be a relative path,
    // but it will also not work properly in oddly specific edge cases. Creating a new
    // file and using the absolute path string is a work-around to this.
    public File input = new File(new File("input.jar").getAbsolutePath()),
            output = new File(new File("output.jar").getAbsolutePath());
    public boolean computeFrames = true, computeMaxes = true;
    public ArrayList<String> whitelist = new ArrayList<>(), blacklist = new ArrayList<>();


}
