package autoservice.di;

import autoservice.di.annotation.Inject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class DIContext {
    private final Map<Class<?>, Object> singletons = new HashMap<>();
    private final Map<Class<?>, Class<?>> implementations = new HashMap<>();

    @SuppressWarnings("unchecked")
    private <T> T getOrCreateSingleton(Class<T> type) throws Exception {
        Object instance = singletons.get(type);
        if (instance != null) {
            return (T) instance;
        }
        instance = createInstance(type);
        singletons.put(type, instance);
        return (T) instance;
    }

    @SuppressWarnings("unchecked")
    private <T> T createInstance(Class<T> type) throws Exception {
        Class<?> targetClass = implementations.getOrDefault(type, type);
        Constructor<?> constructor = targetClass.getDeclaredConstructor();
        constructor.setAccessible(true);

        T instance = (T) constructor.newInstance();
        injectDependencies(instance);
        return instance;
    }

    private void injectDependencies(Object instance) throws IllegalAccessException {
        Class<?> clazz = instance.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                field.setAccessible(true);
                Class<?> fieldType = field.getType();
                Object dependency = getInstance(fieldType);
                field.set(instance, dependency);
            }
        }
    }

    public <T> T getInstance(Class<T> type) {
        try {
            if (singletons.containsKey(type)) {
                return getOrCreateSingleton(type);
            }
            return createInstance(type);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка создания экземпляра класса: " + type.getName(), e);
        }
    }

    public <T> void registerSingletonType(Class<T> type) {
        singletons.put(type, null); // null как маркер "будет создан лениво"
    }

    public <T> void registerImplementation(Class<T> interfaceType, Class<? extends T> implementationType) {
        implementations.put(interfaceType, implementationType);
    }

    public <T> void registerSingleton(Class<T> type, T instance) {
        singletons.put(type, instance);
    }
}