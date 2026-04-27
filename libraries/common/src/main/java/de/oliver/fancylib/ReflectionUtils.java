package de.oliver.fancylib;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectionUtils {

    private static final Map<String, Field> fieldCache = new ConcurrentHashMap<>();
    private static final Map<String, Method> methodCache = new ConcurrentHashMap<>();

    public static Object getValue(Object instance, String name) {
        try {
            Field field = getField(instance.getClass(), name);
            return field != null ? field.get(instance) : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getStaticValue(Class clazz, String name) {
        try {
            Field field = getField(clazz, name);
            return field != null ? field.get(null) : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setValue(Object instance, String name, Object value) {
        try {
            Field field = getField(instance.getClass(), name);
            if (field != null) {
                field.set(instance, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Method getMethod(Object instance, String methodName) {
        return getMethod(instance.getClass(), methodName);
    }

    private static Field getField(Class clazz, String name) {
        String key = clazz.getName() + "#" + name;
        Field cached = fieldCache.get(key);
        if (cached != null) return cached;

        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            fieldCache.put(key, field);
            return field;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Method getMethod(Class clazz, String name) {
        String key = clazz.getName() + "#" + name;
        Method cached = methodCache.get(key);
        if (cached != null) return cached;

        try {
            Method method = clazz.getDeclaredMethod(name);
            method.setAccessible(true);
            methodCache.put(key, method);
            return method;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
