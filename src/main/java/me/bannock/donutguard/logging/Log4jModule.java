package me.bannock.donutguard.logging;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

public class Log4jModule extends AbstractModule {

    @Override
    protected void configure() {
        bindListener(Matchers.any(), new Log4j2TypeListener());
    }

}
