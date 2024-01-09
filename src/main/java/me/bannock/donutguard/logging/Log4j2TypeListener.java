package me.bannock.donutguard.logging;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;

public class Log4j2TypeListener implements TypeListener {

    @Override
    public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
        Class<?> clazz = type.getRawType();
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getType() == Logger.class &&
                        field.isAnnotationPresent(InjectLogger.class)) {
                    encounter.register(new Log4JMembersInjector<>(field));
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

}
