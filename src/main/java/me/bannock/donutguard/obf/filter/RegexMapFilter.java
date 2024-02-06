package me.bannock.donutguard.obf.filter;

import java.util.ArrayList;
import java.util.Map;

public class RegexMapFilter {

    private final Map<String, ArrayList<String>> matches;

    /**
     * @param matches A list of regex matchers that this filter will use to match
     */
    public RegexMapFilter(Map<String, ArrayList<String>> matches){
        this.matches = matches;
    }

    /**
     * @param path The path to compare
     * @return True if the path matches one of the regex matchers, otherwise false
     */
    public boolean matches(String key, String path){
        if (!this.matches.containsKey(key))
            return false;
        for (String matcher : this.matches.get(key)){
            if (path.matches(matcher))
                return true;
        }
        return false;
    }

}
