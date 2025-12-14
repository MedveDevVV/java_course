package autoservice.config;

import autoservice.annotation.ConfigProperty;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.*;

public class AnnotationConfigurator {

    public static void configure(Object configObject) throws Exception {
        Class<?> clazz = configObject.getClass();
        Properties properties = loadProperties(configObject);

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigProperty.class)) {
                ConfigProperty annotation = field.getAnnotation(ConfigProperty.class);
                applyProperty(configObject, field, annotation, properties);
            }
        }
    }

    private static Properties loadProperties(Object configObject) throws Exception {
        String configFile = "config.properties";
        for (Field field : configObject.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigProperty.class)) {
                String fileName = field.getAnnotation(ConfigProperty.class).configFileName();
                if (!fileName.equals("config.properties")) {
                    configFile = fileName;
                    break;
                }
            }
        }

        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(configFile)) {
            properties.load(input);
        } catch (IOException e) {
            System.err.println("Ошибка загрузки конфигурации: " + e.getMessage());
        }
        return properties;
    }

    private static Collection<Object> createCollection(Class<?> collectionType) {
        if (collectionType == List.class || collectionType == ArrayList.class) {
            return new ArrayList<>();
        } else if (collectionType == Set.class || collectionType == HashSet.class) {
            return new HashSet<>();
        } else if (collectionType == LinkedList.class) {
            return new LinkedList<>();
        }
        return new ArrayList<>();
    }

    private static String getPropertyName(ConfigProperty annotation, Field field) {
        if (!annotation.propertyName().isEmpty()) {
            return annotation.propertyName();
        }
        return field.getDeclaringClass().getSimpleName().toLowerCase()
                + "." + field.getName();
    }

    private static Object convertToArray(String value, Class<?> componentType) {
        if (value.trim().isEmpty()) {
            return null;
        }

        String[] stringValues = value.split(";");

        if (componentType == String.class) {
            return stringValues;
        } else if (componentType == int.class) {
            int[] array = new int[stringValues.length];
            for (int i = 0; i < stringValues.length; i++) {
                array[i] = Integer.parseInt(stringValues[i].trim());
            }
            return array;
        } else if (componentType == Integer.class) {
            Integer[] array = new Integer[stringValues.length];
            for (int i = 0; i < stringValues.length; i++) {
                array[i] = Integer.valueOf(stringValues[i].trim());
            }
            return array;
        } else if (componentType == boolean.class) {
            boolean[] array = new boolean[stringValues.length];
            for (int i = 0; i < stringValues.length; i++) {
                array[i] = Boolean.parseBoolean(stringValues[i].trim());
            }
            return array;
        } else if (componentType == Boolean.class) {
            Boolean[] array = new Boolean[stringValues.length];
            for (int i = 0; i < stringValues.length; i++) {
                array[i] = Boolean.valueOf(stringValues[i].trim());
            }
            return array;
        }

        throw new IllegalArgumentException("Неподдерживаемый тип массива: " + componentType);
    }

    private static Collection<?> convertToCollection(String value, Class<?> collectionType) {
        if (value.trim().isEmpty()) {
            return createCollection(collectionType);
        }

        String[] stringValues = value.split(";");
        Collection<Object> collection = createCollection(collectionType);

        for (String str : stringValues) {
            collection.add(str.trim());
        }

        return collection;
    }

    private static Object convertValue(String stringValue, Class<?> annotationType, Field field) {
        Class<?> fieldType = field.getType();
        Class<?> targetType;
        if (annotationType != Void.class) {
            targetType = annotationType;
        } else if (fieldType == Object.class) {
            targetType = String.class;
        } else {
            targetType = fieldType;
        }

        return convertStringToType(stringValue, targetType, field);
    }

    private static Object convertStringToType(String value, Class<?> targetType, Field field) {
        if (targetType == String.class) {
            return value;
        } else if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (targetType == int.class || targetType == Integer.class) {
            return Integer.parseInt(value);
        } else if (targetType == double.class || targetType == Double.class) {
            return Double.parseDouble(value);
        } else if (targetType.isArray()) {
            return convertToArray(value, targetType.getComponentType());
        } else if (Collection.class.isAssignableFrom(targetType)) {
            return convertToCollection(value, targetType);
        }
        throw new IllegalArgumentException(
                String.format("Переменная '%s' не может быть сохранена в поле '%s' с типом '%s'",
                        value, field.getName(), field.getType()));
    }

    private static void applyProperty(Object configObject, Field field,
                                      ConfigProperty annotation, Properties properties) throws IllegalAccessException {
        field.setAccessible(true);

        String propertyName = getPropertyName(annotation, field);
        String stringValue = properties.getProperty(propertyName);

        if (stringValue == null) {
            return;
        }

        Object value = convertValue(stringValue, annotation.type(), field);
        field.set(configObject, value);
    }
}