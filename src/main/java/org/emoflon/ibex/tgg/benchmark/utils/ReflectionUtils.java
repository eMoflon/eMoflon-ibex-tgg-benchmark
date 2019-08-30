package org.emoflon.ibex.tgg.benchmark.utils;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.MethodParameterScanner;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

/**
 * Java reflections helper methods
 * 
 * @author Andre Lehmann
 */
public abstract class ReflectionUtils {

    public static final List<URL> CLASS_PATHS;

    static {
        String[] classPaths = System.getProperty("java.class.path").split(":");

        CLASS_PATHS = new LinkedList<>();
        for (String path : classPaths) {
            try {
                CLASS_PATHS.add(new URL("file://" + path));
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * Turns a method into a String with format: CLASS#METHOD
     * 
     * @param method the method
     * @return the method identifier
     */
    public static String methodToString(Method method) {
        return method.getDeclaringClass().getName() + "#" + method.getName();
    }

    /**
     * Splits a method identifier into a class and method name.
     * 
     * @param methodIdentifier the method identifier in the format: CLASS#METHOD
     * @return the class and method name
     */
    public static String[] splitMethodIdentifier(String methodIdentifier) {
        if (methodIdentifier.length() == 0) {
            throw new IllegalArgumentException("Method identifier must not be empty.");
        }
        String[] names = methodIdentifier.split("#");
        if (names.length != 2) {
            throw new IllegalArgumentException(String.format(
                    "Wrong format for method identifier. Expected 'CLASS#METHOD' but got '%s'", methodIdentifier));
        }
        return names;
    }

    /**
     * Turns a method name into an actual method object.
     * 
     * @param methodIdentifier the method identigier in format: CLASS#METHOD
     * @param classLoader      the class loader to resolve the method
     * @param methodParameters
     * @return the method when if the method with the correct name and parameters
     *         exists, otherwise null
     * @throws NoSuchMethodException if anything goes wrong loading the method
     */
    public static Method getMethodByName(ClassLoader classLoader, String methodIdentifier, Class<?>... methodParameters)
            throws NoSuchMethodException {
        String[] names = splitMethodIdentifier(methodIdentifier);
        return getMethodByName(classLoader, names[0].trim(), names[1].trim(), methodParameters);
    }

    /**
     * Gets a method from a class with matching name and signature.
     * 
     * @param classLoader the classloader
     * @param className   the containing class
     * @param methodName  the method name
     * @param parameters  the method parameters
     * @return the found method
     * @throws NoSuchMethodException if anything goes wrong loading the method
     */
    public static Method getMethodByName(ClassLoader classLoader, String className, String methodName,
            Class<?>... parameters) throws NoSuchMethodException {
        try {
            Class<? extends Object> _class = classLoader.loadClass(className);
            return _class.getDeclaredMethod(methodName, parameters);
        } catch (ClassNotFoundException e) {
            throw new NoSuchMethodException(
                    String.format("Class '%s' not found. Check your benchmark preferences.", className));
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodException(String.format(
                    "Class '%s' doesn't contain a method '%s' or its signature is wrong. Check your benchmark preferences.",
                    className, methodName));
        } catch (SecurityException e) {
            throw new NoSuchMethodException(String.format("Method '%s' from class '%s' couldn't be loaded: %s",
                    methodName, className, e.getMessage()));
        }
    }

    /**
     * Gets the methods matching a given parameter signature.
     * 
     * @param classPath  the class path to search the methods in
     * @param parameters the matching parameter signature
     * @return the found methods
     * @throws MalformedURLException if the classPath is invalid
     */
    public static Set<Method> getMethodsWithMatchingParameters(ClassLoader classLoader, Path classPath,
            Class<?>... parameters) throws MalformedURLException {
        FilterBuilder classFileFilter = new FilterBuilder().include(".*\\.class");
        Collection<URL> urls = Arrays.asList(classPath.toUri().toURL());

        Reflections reflections = new Reflections(new ConfigurationBuilder().setUrls(urls).addClassLoader(classLoader)
                .setScanners(new MethodParameterScanner()).filterInputsBy(classFileFilter));

        return reflections.getMethodsMatchParams(parameters);
    }

    /**
     * Create a {@link URLClassLoader} from a given class path.
     * 
     * @param classPath the class path
     * @return a {@link URLClassLoader} for the class path
     * @throws MalformedURLException if the path is invalid
     */
    public static URLClassLoader createClassLoader(Path classPath) throws MalformedURLException {
        URL[] urls = CLASS_PATHS.toArray(new URL[CLASS_PATHS.size() + 1]);
        urls[urls.length - 1] = classPath.toUri().toURL();

        return new URLClassLoader(urls);
    }
}