package me.bannock.donutguard.obf;

import me.bannock.donutguard.obf.dictionary.Dictionary;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ConfigDTO implements Serializable {

    // Don't change this or old configs will not be able to load anymore
    private static final long serialVersionUID = 3621L;

    // Java's File class is really weird because "input.jar" will be a relative path,
    // but it will also not work properly in oddly specific edge cases. Creating a new
    // file and using the absolute path string is kind of like a patch for the odd bugs.
    public File input = new File(new File("input.jar").getAbsolutePath()),
            output = new File(new File("output.jar").getAbsolutePath());
    public Boolean computeFrames = false, computeMaxes = true, includeLibsInOutput = false;
    public List<String> whitelist = new ArrayList<>(),
            blacklist = new ArrayList<>(Arrays.asList(
                    "me/example/(.+/Main\\.class|Main\\.class)", "obf/classWatermark\\.txt",
                    "me/example.*"));

    public List<File> libraries = new ArrayList<>();

    public Dictionary localVariableDict = Dictionary.ALPHABET;
    public Dictionary methodDict = Dictionary.ALPHABET;
    public Dictionary fieldDict = Dictionary.ALPHABET;
    public Dictionary classDict = Dictionary.ALPHABET;
    public Dictionary packageDict = Dictionary.ALPHABET;

    // Nop spammer mutator
    public Boolean nopSpammerEnabled = false;
    public Integer nopsPerInstruction = 2;


}
