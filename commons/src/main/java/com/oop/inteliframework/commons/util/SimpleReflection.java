package com.oop.inteliframework.commons.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.internal.Primitives;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

@UtilityClass
public final class SimpleReflection {

    private static final Map<Class<?>, Constructor<?>> CONSTRUCTOR_MAP = new HashMap<>();
    private static final Map<Class<?>, Map<String, Method>> METHOD_MAP = new HashMap<>();
    private static final Map<String, Class<?>> CLASS_MAP = new HashMap<>();
    private static final Map<Class<?>, Map<String, Field>> FIELD_MAP = new HashMap<>();

    public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... parameterTypes) throws Exception {
        if (CONSTRUCTOR_MAP.containsKey(clazz)) return CONSTRUCTOR_MAP.get(clazz);

        for (Constructor<?> constructor : merge(clazz.getDeclaredConstructors(), clazz.getConstructors())) {
            if (!compareClasses(parameterTypes, constructor.getParameterTypes()))
                continue;

            constructor.setAccessible(true);
            CONSTRUCTOR_MAP.put(clazz, constructor);
            return constructor;
        }

        throw new NoSuchMethodException("There is no such constructor in this class with the specified parameter types");
    }

    public static Constructor<?> getConstructor(String className, Package packageType, Class<?>... parameterTypes) throws Exception {
        return getConstructor(packageType.getClass(className), parameterTypes);
    }

    public static Object initializeObject(Class<?> clazz, Object... arguments) throws Exception {
        return getConstructor(clazz, Arrays.stream(arguments).filter(Objects::nonNull).map(Object::getClass).toArray(Class[]::new)).newInstance(arguments);
    }

    public static Object initializeObject(String className, Package packageType, Object... arguments) throws Exception {
        return initializeObject(packageType.getClass(className), arguments);
    }

    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        if (METHOD_MAP.containsKey(clazz) && METHOD_MAP.get(clazz).containsKey(methodName))
            return METHOD_MAP.get(clazz).get(methodName);

        for (Method method : merge(clazz.getMethods(), clazz.getDeclaredMethods())) {
            if (!method.getName().equalsIgnoreCase(methodName) || !compareClasses(parameterTypes, method.getParameterTypes()))
                continue;

            method.setAccessible(true);

            if (METHOD_MAP.containsKey(clazz))
                METHOD_MAP.get(clazz).put(methodName, method);
            else {
                Map<String, Method> methodMap = new HashMap<>();
                methodMap.put(methodName, method);
                METHOD_MAP.put(clazz, methodMap);
            }
            return method;
        }

        return null;
    }

    private static boolean compareClasses(Class[] first, Class[] second) {
        if (first.length != second.length) return false;
        for (int i = 0; i < first.length; i++) {
            Class c1 = Primitives.unwrap(first[i]);
            Class c2 = Primitives.unwrap(second[i]);
            if (c1 != c2 && !PrimitivesCaster.isCastable(c1, c2)) return false;
        }
        return true;
    }

    public static Class<?> findClass(String path) {
        path = path.replace("{cb}", Package.CB.getPath());
        path = path.replace("{nms}", Package.NMS.getPath());

        Class<?> clazz = CLASS_MAP.get(path);
        if (clazz != null) return clazz;

        try {
            clazz = Class.forName(path);
            CLASS_MAP.put(path, clazz);
        } catch (Throwable thrw) {
            throw new IllegalStateException("Failed to find class at " + path, thrw);
        }

        return clazz;
    }

    public static Method getMethod(String className, Package packageType, String methodName, Class<?>... parameterTypes) throws Exception {
        return getMethod(packageType.getClass(className), methodName, parameterTypes);
    }

    public static Object invokeMethod(Object instance, String methodName, Object... arguments) throws Exception {
        return getMethod(instance.getClass(), methodName, Arrays.stream(arguments).filter(Objects::nonNull).map(Object::getClass).toArray(Class[]::new)).invoke(instance, arguments);
    }

    public static Object invokeMethod(Object instance, Class<?> clazz, String methodName, Object... arguments) throws Exception {
        return getMethod(clazz, methodName, Arrays.stream(arguments).filter(Objects::nonNull).map(Object::getClass).toArray(Class[]::new)).invoke(instance, arguments);
    }

    public static Object invokeMethod(Object instance, String className, Package packageType, String methodName, Object... arguments) throws Exception {
        return invokeMethod(instance, packageType.getClass(className), methodName, arguments);
    }

    public static Field getField(Class<?> clazz, String fieldName) {
        Map<String, Field> stringFieldMap = FIELD_MAP.computeIfAbsent(clazz, clazz2 -> new HashMap<>());
        Field field = stringFieldMap.get(fieldName);
        if (field != null) return field;

        try {
            field = clazz.getDeclaredField(fieldName);
            if (field == null)
                field = clazz.getField(fieldName);
            field.setAccessible(true);
            stringFieldMap.put(fieldName, field);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return field;
    }

    public static <T> Field getField(Class<?> target, String name, Class<T> fieldType, int index) {
        for (final Field field : target.getDeclaredFields()) {
            if ((name == null || field.getName().equals(name)) && fieldType.isAssignableFrom(field.getType()) && index-- <= 0) {
                field.setAccessible(true);
                return field;
            }
        }

        // Search in parent classes
        if (target.getSuperclass() != null)
            return getField(target.getSuperclass(), name, fieldType, index);

        throw new IllegalArgumentException("Cannot find field with type " + fieldType);
    }

    public static Field getField(Class clazz, Class type) {
        try {
            return FieldUtils.getAllFieldsList(clazz)
                    .stream()
                    .filter(field -> {
                        field.setAccessible(true);
                        return field.getType().isAssignableFrom(type) || type == field.getType();
                    })
                    .findFirst()
                    .orElse(null);
        } catch (Throwable thrw) {
            throw new IllegalStateException("Failed to find field with type " + type.getSimpleName());
        }
    }

    private static <T> Set<T> merge(T[]... arrays) {
        Set<T> set = Sets.newHashSet();
        for (T[] array : arrays) {
            set.addAll(Lists.newArrayList(array));
        }

        return set;
    }

    public static Object executeMethod(Method method, Object ofWho, Object... args) {
        try {
            args = checkArgs(method.getParameterTypes(), args);
            return method.invoke(ofWho, args);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    public static Object initializeObject(Constructor constructor, Object... args) {
        try {
            args = checkArgs(constructor.getParameterTypes(), args);
            return constructor.newInstance(args);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    private static Object[] checkArgs(Class[] a1, Object[] a2) throws IllegalStateException {
        Preconditions.checkArgument(a1.length == a2.length, "Failed to validate arguments cause lengths doesn't match");
        int size = a1.length;
        for (int i = 0; i < size; i++) {
            Class c1 = Primitives.unwrap(a1[i]);
            Object o1 = a2[i];
            Class c2 = Primitives.unwrap(o1.getClass());

            if (c1 != c2 && !PrimitivesCaster.isCastable(c1, c2))
                throw new IllegalStateException("Argument miss match at " + i + " found " + c2.getSimpleName() + ", required: " + c1.getSimpleName());

            else {
                if (a1[i] == o1.getClass()) continue;
                a2[i] = PrimitivesCaster.cast(c1, o1);
            }
        }
        return a2;
    }

    public static void setValue(Object object, String field, Object value) {
        try {
            getField(object.getClass(), field).set(object, value);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public static Field getField(String className, Package packageType, String fieldName) throws Exception {
        return getField(packageType.getClass(className), fieldName);
    }

    public static Method getTypedMethod(Class<?> clazz, String methodName, Class<?> returnType, Class<?>... params) {
        for (final Method method : clazz.getDeclaredMethods()) {
            if ((methodName == null || method.getName().equals(methodName))
                    && (returnType == null || method.getReturnType().equals(returnType))
                    && Arrays.equals(method.getParameterTypes(), params)) {
                method.setAccessible(true);
                return method;
            }
        }

        // Search in every superclass
        if (clazz.getSuperclass() != null)
            return getMethod(clazz.getSuperclass(), methodName, params);

        throw new IllegalStateException(StringFormat.format("Unable to find method {} ({}).", methodName, Arrays.asList(params)));
    }

    public enum Package {
        NMS("net.minecraft.server." + getServerVersion()),
        CB("org.bukkit.craftbukkit." + getServerVersion()),
        CB_BLOCK(CB, "block"),
        CB_CHUNKIO(CB, "chunkio"),
        CB_COMMAND(CB, "command"),
        CB_CONVERSATIONS(CB, "conversations"),
        CB_ENCHANTMENS(CB, "enchantments"),
        CB_ENTITY(CB, "entity"),
        CB_EVENT(CB, "event"),
        CB_GENERATOR(CB, "generator"),
        CB_HELP(CB, "help"),
        CB_INVENTORY(CB, "inventory"),
        CB_MAP(CB, "map"),
        CB_METADATA(CB, "metadata"),
        CB_POTION(CB, "potion"),
        CB_PROJECTILES(CB, "projectiles"),
        CB_SCHEDULER(CB, "scheduler"),
        CB_SCOREBOARD(CB, "scoreboard"),
        CB_UPDATER(CB, "updater"),
        CB_UTIL(CB, "interfaces");

        private final String path;

        Package(String path) {
            this.path = path;
        }

        Package(Package parent, String path) {
            this(parent + "." + path);
        }

        public static String getServerVersion() {
            return Bukkit.getServer().getClass().getName().split("\\.")[3];
        }

        public String getPath() {
            return path;
        }

        public Class<?> getClass(String className) throws Exception {
            if (CLASS_MAP.containsKey(className)) return CLASS_MAP.get(className);

            Class<?> clazz = Class.forName(this + "." + className);
            CLASS_MAP.put(className, clazz);
            return clazz;

        }

        public Class<?> getClassIfFoundInCache(String className) {
            return CLASS_MAP.get(className);
        }

        @Override
        public String toString() {
            return path;
        }
    }

    public static class Player {

        private static final Class<?>
                CRAFT_PLAYER_CLASS,
                ENTITY_PLAYER_CLASS,
                PLAYER_CONNECTION_CLASS,
                PACKET_CLASS;

        private static final Method
                SEND_PACKET_METHOD,
                GET_HANDLE_METHOD;

        private static final Field
                PLAYER_CONNECTION_FIELD;

        static {
            try {

                CRAFT_PLAYER_CLASS = Package.CB_ENTITY.getClass("CraftPlayer");
                ENTITY_PLAYER_CLASS = Package.NMS.getClass("EntityPlayer");
                PLAYER_CONNECTION_CLASS = Package.NMS.getClass("PlayerConnection");
                PACKET_CLASS = Package.NMS.getClass("Packet");

                PLAYER_CONNECTION_FIELD = getField(ENTITY_PLAYER_CLASS, "playerConnection");

                SEND_PACKET_METHOD = getMethod(PLAYER_CONNECTION_CLASS, "sendPacket", PACKET_CLASS);
                GET_HANDLE_METHOD = getMethod(CRAFT_PLAYER_CLASS, "getHandle");

            } catch (Exception ex) {
                throw new IllegalStateException(ex);
            }
        }

        public static void sendPacket(org.bukkit.entity.Player player, Object packet) {
            try {

                Object entityPlayer = GET_HANDLE_METHOD.invoke(player);
                Object connection = PLAYER_CONNECTION_FIELD.get(entityPlayer);

                SEND_PACKET_METHOD.invoke(connection, packet);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class PrimitivesCaster {
        private static Map<Class, Function<String, Object>> casters = new HashMap<>();

        static {
            casters.put(int.class, in -> {
                if (in.contains("."))
                    return Math.round(Double.parseDouble(in));
                else
                    return Integer.parseInt(in);
            });
            casters.put(long.class, in -> {
                if (in.contains("."))
                    return Math.round(Double.parseDouble(in));
                else
                    return Long.parseLong(in);
            });
            casters.put(double.class, Double::parseDouble);
            casters.put(short.class, in -> {
                if (in.contains("."))
                    return Math.round(Double.parseDouble(in));
                else
                    return Short.parseShort(in);
            });
            casters.put(float.class, Float::parseFloat);
            casters.put(byte.class, Byte::parseByte);
            casters.put(boolean.class, Boolean::parseBoolean);
        }

        public static boolean isCastable(Class from, Class to) {
            return casters.containsKey(from) && casters.containsKey(to);
        }

        public static Object cast(Class to, Object what) {
            Function<String, Object> stringObjectFunction = casters.get(Primitives.unwrap(to));
            Preconditions.checkArgument(stringObjectFunction != null, "Failed to find caster for " + to.getSimpleName());

            return stringObjectFunction.apply(what.toString());
        }
    }
}