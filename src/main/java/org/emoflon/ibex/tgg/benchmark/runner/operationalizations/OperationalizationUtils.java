package org.emoflon.ibex.tgg.benchmark.runner.operationalizations;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.emoflon.ibex.tgg.operational.strategies.OperationalStrategy;

public abstract class OperationalizationUtils {

    public static void registerUserMetamodels(ResourceSet rs, OperationalStrategy op, ClassLoader classLoader,
            String className, String methodName) throws IOException {
        try {
            Method registerMetamodelsMethod = org.emoflon.ibex.tgg.benchmark.utils.RefelctionUtils
                    .getMethodForName(classLoader, className, methodName, ResourceSet.class, OperationalStrategy.class);
            registerMetamodelsMethod.invoke(null, new Object[] { rs, op });
        } catch (ClassNotFoundException e) {
            throw new IOException(String.format(
                    "Class '%s' containing the helper for registering the meta models not found. Check your benchmark preferences.",
                    className));
        } catch (NoSuchMethodException e) {
            throw new IOException(String.format(
                    "Helper class '%s' doesn't contain a method '%s' or its signature is wrong. Check your benchmark preferences.",
                    className, methodName));
        } catch (InvocationTargetException e) {
            throw new IOException(String.format("Failed to register meta models: %s", e.getCause().getMessage()));
        } catch (Exception e) {
            throw new IOException(String.format("Failed to register meta models: %s", e.getMessage()));
        }
        System.out.println("here");
    }
}
