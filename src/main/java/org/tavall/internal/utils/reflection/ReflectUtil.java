package org.tavall.internal.utils.reflection;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;

/**
 * Reflection helpers for class lookup, member discovery, trusted lookup access, and runtime library
 * attachment.
 *
 * <p>Several operations deliberately bypass ordinary Java access checks. Callers should use these
 * helpers only where Tavall owns the runtime assumptions involved, because JDK internals and
 * class-loader capabilities can vary between runtimes.</p>
 */
public class ReflectUtil {

    /**
     * Resolves a class by binary name without propagating lookup failures.
     *
     * @param name binary class name accepted by {@link Class#forName(String)}
     * @return the resolved class, or {@code null} when resolution fails
     */
    public static Class<?> getClass(String name) {
        try {
            return Class.forName(name);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Obtains the JDK's trusted {@code IMPL_LOOKUP} by reading it through {@code sun.misc.Unsafe}.
     *
     * <p>The returned lookup can bypass normal reflective access restrictions and is intended for
     * Tavall-owned runtime integration code that cannot be implemented with a normal lookup.</p>
     *
     * @return the trusted method-handle lookup
     * @throws ClassNotFoundException if the required Unsafe class cannot be resolved
     * @throws IllegalAccessException if reflective field access is rejected
     * @throws InvocationTargetException if an invoked Unsafe method fails
     * @throws NoSuchFieldException if a required JDK field cannot be located
     * @throws NoSuchMethodException if a required Unsafe method cannot be located
     */
    public static MethodHandles.Lookup getSuperLookup() throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchFieldException, NoSuchMethodException {
        Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
        Field theUnsafeField = getField(unsafeClass, unsafeClass, true);
        Method theUnsafeGetObjectMethod = getMethod(unsafeClass, "getObject", false, new Class[] { Object.class, long.class });
        Method theUnsafeStaticFieldOffsetMethod = getMethod(unsafeClass, "staticFieldOffset", false, new Class[] { Field.class });
        Object theUnsafe = theUnsafeField.get(null);
        Field implLookup = getField(MethodHandles.Lookup.class, "IMPL_LOOKUP", false);
        return (MethodHandles.Lookup)theUnsafeGetObjectMethod.invoke(theUnsafe, new Object[] { MethodHandles.Lookup.class, theUnsafeStaticFieldOffsetMethod.invoke(theUnsafe, new Object[] { implLookup }) });
    }

    /**
     * Attaches a file or directory URL to the class loader that loaded this utility.
     *
     * <p>The operation invokes the loader's {@code addURL(URL)} implementation through the trusted
     * lookup returned by {@link #getSuperLookup()}.</p>
     *
     * @param file JAR file or directory to add to the loader search path
     * @throws Throwable if trusted lookup, member discovery, URL conversion, or method-handle
     *                   invocation fails
     */
    public static void addFileLibrary(File file) throws Throwable {
        ClassLoader classLoader = ReflectUtil.class.getClassLoader();
        MethodHandle handle = getSuperLookup().unreflect(getMethodWithParent(classLoader.getClass(), "addURL", false, new Class[] { URL.class }));
        handle.invoke(classLoader, file.toURI().toURL());
    }

    /**
     * Finds a declared field by exact name on one class.
     *
     * @param clazz class whose declared fields should be searched
     * @param target exact field name
     * @param handleAccessible whether to force the returned field accessible
     * @return the matching declared field
     * @throws NoSuchFieldException if the class does not declare the requested field
     */
    public static Field getField(Class<?> clazz, String target, boolean handleAccessible) throws NoSuchFieldException {
        try {
            Field field = clazz.getDeclaredField(target);
            if (handleAccessible)
                field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            throw new NoSuchFieldException(target + " field in " + target);
        }
    }

    /**
     * Finds the first field whose declared type exactly matches the requested type, walking the
     * superclass chain when necessary.
     *
     * @param clazz class where the search begins
     * @param target exact field type to locate
     * @param handleAccessible whether to force the returned field accessible
     * @return the first matching field found while walking toward the root superclass
     * @throws NoSuchFieldException if no field with the requested type exists
     */
    public static Field getField(Class<?> clazz, Class<?> target, boolean handleAccessible) throws NoSuchFieldException {
        return getField0(clazz, clazz, target, handleAccessible);
    }

    private static Field getField0(Class<?> source, Class<?> clazz, Class<?> target, boolean handleAccessible) throws NoSuchFieldException {
        Field[] arrayOfField;
        int i;
        byte b;
        for (arrayOfField = clazz.getDeclaredFields(), i = arrayOfField.length, b = 0; b < i; ) {
            Field field = arrayOfField[b];
            if (field.getType() != target) {
                b++;
                continue;
            }
            if (handleAccessible)
                field.setAccessible(true);
            return field;
        }
        clazz = clazz.getSuperclass();
        if (clazz != null)
            return getField(clazz, target, handleAccessible);
        throw new NoSuchFieldException(target.getName() + " type in " + target.getName());
    }

    /**
     * Finds a declared method by case-insensitive name and exact parameter-type sequence.
     *
     * <p>This overload does not search parent classes. Use {@link #getMethodWithParent(Class,
     * String, boolean, Class[])} when inherited declarations should be considered.</p>
     *
     * @param clazz class whose declared methods should be searched
     * @param name method name to match case-insensitively
     * @param handleAccessible whether to force the returned method accessible
     * @param args exact parameter types required by the method
     * @return the matching declared method
     * @throws NoSuchMethodException if no exact signature is found
     */
    public static Method getMethod(Class<?> clazz, String name, boolean handleAccessible, Class<?>... args) throws NoSuchMethodException {
        Method[] arrayOfMethod;
        int i;
        byte b;
        for (arrayOfMethod = clazz.getDeclaredMethods(), i = arrayOfMethod.length, b = 0; b < i; ) {
            Method method = arrayOfMethod[b];
            if (!method.getName().equalsIgnoreCase(name) ||
                    !Arrays.equals((Object[])method.getParameterTypes(), (Object[])args)) {
                b++;
                continue;
            }
            if (handleAccessible)
                method.setAccessible(true);
            return method;
        }
        throw new NoSuchMethodException(name + " method in " + name);
    }

    /**
     * Finds a method by case-insensitive name and exact parameter types while walking parent
     * classes until {@link Object} is reached.
     *
     * @param clazz class where the search begins
     * @param name method name to match case-insensitively
     * @param handleAccessible whether to force the returned method accessible
     * @param args exact parameter types required by the method
     * @return the first matching declaration found in the class hierarchy
     * @throws NoSuchMethodException if no matching declaration is found before {@link Object}
     */
    public static Method getMethodWithParent(Class<?> clazz, String name, boolean handleAccessible, Class<?>... args) throws NoSuchMethodException {
        Method[] arrayOfMethod;
        int i;
        byte b;
        for (arrayOfMethod = clazz.getDeclaredMethods(), i = arrayOfMethod.length, b = 0; b < i; ) {
            Method method = arrayOfMethod[b];
            if (!method.getName().equalsIgnoreCase(name) ||
                    !Arrays.equals((Object[])method.getParameterTypes(), (Object[])args)) {
                b++;
                continue;
            }
            if (handleAccessible)
                method.setAccessible(true);
            return method;
        }
        if (clazz != Object.class)
            return getMethodWithParent(clazz.getSuperclass(), name, handleAccessible, args);
        throw new NoSuchMethodException(name + " method in " + name);
    }

    /**
     * Loads every JAR file and directory immediately inside the process working directory's
     * {@code libs} folder.
     *
     * <p>Discoverable libraries are attached through {@link #addFileLibrary(File)}. Ordinary
     * checked failures are reported to standard error; other throwables are rethrown as runtime
     * exceptions.</p>
     */
    public static void loadLibs(){
        try {
            File libsFolder = new File("libs");
            File[] jarFiles = libsFolder.listFiles((file) -> file.getName().endsWith(".jar") || file.isDirectory());
            if (jarFiles == null || jarFiles.length == 0) {
                System.out.println("No JAR libraries found in " + libsFolder.getAbsolutePath());
                return;
            }
            for (File libraryFile : jarFiles) {
                System.out.println("Loading: " + libraryFile.getName());

                ReflectUtil.addFileLibrary(libraryFile);
            }
            System.out.println("Successfully loaded. ");
        } catch (Exception e) {
            System.err.println("Failed to load external library: " + e.getMessage());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
