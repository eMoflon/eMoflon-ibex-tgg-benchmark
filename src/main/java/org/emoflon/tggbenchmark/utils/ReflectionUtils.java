package org.emoflon.tggbenchmark.utils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.emoflon.tggbenchmark.workspace.EclipseJavaProject;

/**
 * Java reflections helper methods
 * 
 * @author Andre Lehmann
 */
public abstract class ReflectionUtils {

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
     * @param methodIdentifier the method identifier in format: CLASS#METHOD
     * @param classLoader      the class loader to resolve the method
     * @param methodParameters
     * @return the method when if the method with the correct name and parameters
     *         exists, otherwise null
     * @throws NoSuchMethodException if anything goes wrong loading the method
     */
    public static Method getStaticMethodByName(ClassLoader classLoader, String methodIdentifier,
            Class<?>... methodParameters) throws NoSuchMethodException {
        String[] names = splitMethodIdentifier(methodIdentifier);
        return getStaticMethodByName(classLoader, names[0].trim(), names[1].trim(), methodParameters);
    }

    /**
     * Gets a method from a class with matching name and signature.
     * 
     * @param classLoader the class loader
     * @param className   the containing class
     * @param methodName  the method name
     * @param parameters  the method parameters
     * @return the found method
     * @throws NoSuchMethodException if anything goes wrong loading the method
     */
    public static Method getStaticMethodByName(ClassLoader classLoader, String className, String methodName,
            Class<?>... parameters) throws NoSuchMethodException {
        try {
            Class<?> clazz = classLoader.loadClass(className);
            // the function clazz.getDeclaredMethod(methodName, parameters); doesn't work
            // when Eclipse is involved so this is a workaround
            nextMethod: for (Method method : clazz.getDeclaredMethods()) {
                Parameter[] methodParameters = method.getParameters();
                if (Modifier.isStatic(method.getModifiers()) && method.getName().equals(methodName)
                        && parameters.length == methodParameters.length) {
                    for (int i = 0; i < parameters.length; i++) {
                        // need to compare the String representations instead of the actual Classes
                        if (!parameters[i].toString().equals(methodParameters[i].getType().toString())) {
                            continue nextMethod;
                        }
                    }
                    return method;
                }
            }
            throw new NoSuchMethodException(String.format(
                    "Class '%s' doesn't contain a static method '%s' or the signature does not match. Check your benchmark preferences.",
                    className, methodName));
        } catch (ClassNotFoundException e) {
            throw new NoSuchMethodException(
                    String.format("Class '%s' not found. Check your benchmark preferences.", className));
        } catch (SecurityException e) {
            throw new NoSuchMethodException(String.format("Static method '%s' from class '%s' couldn't be loaded: %s",
                    methodName, className, e.getMessage()));
        }
    }

    /**
     * Gets the methods matching a given parameter signature.
     * 
     * @param classLoader the class loader used to load the methods
     * @param classPath   the class path to search the methods in
     * @param parameters  the matching parameter signature
     * @return the found methods
     */
    public static Set<Method> getMethodsWithMatchingParameters(ClassLoader classLoader, Path classPath,
            Class<?>... parameters) {
        Set<Method> foundMethods = new HashSet<>();

        try (Stream<Path> stream = Files.find(classPath, 100,
                (path, basicFileAttributes) -> path.toString().endsWith(".class"))) {
            stream.forEach(p -> {
                String className = classPath.relativize(p).toString();
                className = className.substring(0, className.length() - 6).replace("/", ".");
                try {
                    Class<?> clazz = classLoader.loadClass(className);
                    nextMethod: for (Method method : clazz.getDeclaredMethods()) {
                        Parameter[] methodParameters = method.getParameters();
                        if (parameters.length == methodParameters.length) {
                            for (int i = 0; i < parameters.length; i++) {
                                // need to compare the String representations instead of the actual Classes
                                if (!parameters[i].toString().equals(methodParameters[i].getType().toString())) {
                                    continue nextMethod;
                                }
                            }
                            foundMethods.add(method);
                        }
                    }
                } catch (ClassNotFoundException e) {
                    // ignore
                }
            });
        } catch (IOException e) {
            // ignore
        }

        return foundMethods;
    }

    /**
     * Create a {@link URLClassLoader} from a {@link EclipseJavaProject}.
     * 
     * @param javaProject the project
     * @return a class loader
     */
    public static URLClassLoader createClassLoader(EclipseJavaProject javaProject) {
        Set<URL> urls = javaProject.getAllClasspaths();
        return new URLClassLoader(urls.toArray(new URL[urls.size()]));
    }
}