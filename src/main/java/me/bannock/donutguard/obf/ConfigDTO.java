package me.bannock.donutguard.obf;

import java.io.File;

public class ConfigDTO {

    // The java file class is really weird because "input.jar" will be a relative path,
    // but it will also not work properly in oddly specific edge cases. Creating a new
    // file and using the absolute path string is a word-around to this.
    public File input = new File(new File("input.jar").getAbsolutePath()),
            output = new File(new File("output.jar").getAbsolutePath());
    public boolean computeFrames = true, computeMaxes = true;

}
