package poa.poalib.reflection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


@SuppressWarnings("unused")
public class ReflectionUtility {

    private static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
    public static boolean NEW_NMS = true;

    public static Class<?> getOBCClass(String obcClassString) {
        String name = "org.bukkit.craftbukkit." + VERSION + obcClassString;
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static Class<?> getPluginClass(String className, String packageName){
        try {
            return Class.forName(packageName + "." + className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public static Class<?> getNMSClass(String nmsClass, String nmsPackage) {
        try {
            if (NEW_NMS) {
                return Class.forName(nmsPackage + "." + nmsClass);
            } else {
                return Class.forName("net.minecraft.server." + VERSION + nmsClass);
            }
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static Object getNMSEntity(Entity entity) {
        try {
            Method getHandle = entity.getClass().getMethod("getHandle");
            return getHandle.invoke(entity);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {

            return null;
        }
    }

    public static Object getField(String field, Class<?> clazz, Object object) {
        try {
            Field f = clazz.getDeclaredField(field);
            f.setAccessible(true);
            return f.get(object);
        } catch (IllegalAccessException | NoSuchFieldException ex) {

            return null;
        }
    }

    public static void setField(String field, Class<?> clazz, Object object, Object toSet) {
        try {
            Field f = clazz.getDeclaredField(field);
            f.setAccessible(true);
            f.set(object, toSet);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException ignored) {
        }
    }

    public static void setField(String field, Object object, Object toSet) {
        try {
            Field f = object.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(object, toSet);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException ignored) {
        }
    }


}
