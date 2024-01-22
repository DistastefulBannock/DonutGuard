package me.bannock.donutguard.obf.filter;

import java.util.Collection;

public class RegexListFilter {

    private final Collection<String> matches;

    /**
     * @param matches A list of regex matchers that this filter will use to match
     */
    public RegexListFilter(Collection<String> matches){
        this.matches = matches;
    }

    /**
     * @param path The path to compare
     * @return True if the path matches one of the regex matchers, otherwise false
     */
    public boolean matches(String path){
        for (String matcher : this.matches){
            if (path.matches(matcher))
                return true;
        }
        return false;
    }

}
