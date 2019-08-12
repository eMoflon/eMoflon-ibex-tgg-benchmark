package org.emoflon.ibex.tgg.benchmark.utils;

import java.lang.reflect.Method;

/**
 * Java reflections helper methods
 * 
 * @author Andre Lehmann
 */
public class RefelctionUtils {

    /**
     * Gets a method from a class with matching name and signature.
     * 
     * @param classLoader the classloader
     * @param className the containing class
     * @param methodName the method name
     * @param parameters the method signature
     * @return the found method
     * @throws ClassNotFoundException if the class doesn't exist
     * @throws NoSuchMethodException if the method with the given name and signature doesn't exist
     * @throws SecurityException
     */
    public static Method getMethodForName(ClassLoader classLoader, String className, String methodName,
            Class<?>... parameters) throws ClassNotFoundException, NoSuchMethodException, SecurityException {
        Class<? extends Object> _class = classLoader.loadClass(className);
        Method method = _class.getDeclaredMethod(methodName, parameters);

        return method;
    }

    /**
     * Splits a method identifier 'CLASS#METHOD' into a class and method name.
     * 
     * @param methodIdentifier the method identifiert
     * @return class and method name as String array
     * @throws IllegalArgumentException if format is wrong
     */    
    public static String[] splitClassAndMethodName(String methodIdentifier) throws IllegalArgumentException {
        String[] names = methodIdentifier.split("#");
        if (names.length != 2) {
            throw new IllegalArgumentException(String.format("Wrong format for method identifiert. Expected 'CLASS#METHOD' but got '{}'", methodIdentifier));
        }

        names[0] = names[0].trim();
        names[1] = names[1].trim();

        return names;
    }
}