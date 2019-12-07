package org.emoflon.tggbenchmark.runner.benchmark.strategies;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.emoflon.ibex.tgg.compiler.defaults.IRegistrationHelper;
import org.emoflon.ibex.tgg.operational.defaults.IbexOptions;
import org.emoflon.ibex.tgg.operational.strategies.OperationalStrategy;
import org.emoflon.tggbenchmark.utils.ReflectionUtils;

public abstract class StrategiesUtils {

    public static void registerUserMetamodels(ResourceSet rs, OperationalStrategy op, ClassLoader classLoader,
            String className, String methodName) throws IOException {
        try {
        	Method registerMetamodelsMethod = ReflectionUtils.getMethodByName(classLoader, className, methodName, false,
                    ResourceSet.class, OperationalStrategy.class);
            registerMetamodelsMethod.invoke(getRegistrationHelperObject(className), new Object[] { rs, op });
        } catch (InvocationTargetException e) {
            throw new IOException(String.format("Failed to register meta models: %s", e.getCause().getMessage()));
        } catch (Exception e) {
            throw new IOException(String.format("Failed to register meta models: %s", e.getMessage()));
        }
    }
    
    public static IbexOptions createIbexOptions(ClassLoader classLoader, String className) throws IOException {
    	try {
            Method createIbexOptionsMethod = ReflectionUtils.getMethodByName(classLoader, className, "createIbexOptions", false);
            System.out.println("method:" + createIbexOptionsMethod);
            IRegistrationHelper regHelper = getRegistrationHelperObject(className);
            System.out.println("helper:" + regHelper);
            return (IbexOptions) regHelper.createIbexOptions();
        } catch (Exception e) {
            throw new IOException(String.format("Failed to create IbexOptions: %s", e.getMessage()));
        }
    }
    
    public static IRegistrationHelper getRegistrationHelperObject(String className) throws IOException {
    	try {
			Class<? extends IRegistrationHelper> regClass = (Class<? extends IRegistrationHelper>) Class.forName(className);
			Constructor<? extends IRegistrationHelper> constr = regClass.getDeclaredConstructor();
			constr.setAccessible(true);
			return constr.newInstance();
		} catch (ClassNotFoundException e) {
            throw new IOException(String.format("Failed to load %s: %s",className, e.getMessage()));
		} catch (NoSuchMethodException e) {
            throw new IOException(String.format("Failed to load find constructor for %s: %s",className, e.getMessage()));
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
            throw new IOException(String.format("Failed to instantiate %s: %s",className, e.getMessage()));
		} catch (IllegalAccessException e) {
            throw new IOException(String.format("Failed to instantiate %s: %s",className, e.getMessage()));
		} catch (IllegalArgumentException e) {
            throw new IOException(String.format("Failed to instantiate due to wrong arguments %s: %s",className, e.getMessage()));
		} catch (InvocationTargetException e) {
            throw new IOException(String.format("Failed to instantiate due to wrong target %s: %s",className, e.getMessage()));
		}
		return null;
    }
}
