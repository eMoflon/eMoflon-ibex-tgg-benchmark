package org.emoflon.tggbenchmark.runner.benchmark.strategies;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.emoflon.ibex.tgg.operational.strategies.OperationalStrategy;
import org.emoflon.tggbenchmark.utils.ReflectionUtils;

abstract class StrategiesUtils {

    public static void registerUserMetamodels(ResourceSet rs, OperationalStrategy op, ClassLoader classLoader,
            String className, String methodName) throws IOException {
        try {
            Method registerMetamodelsMethod = ReflectionUtils.getStaticMethodByName(classLoader, className, methodName,
                    ResourceSet.class, OperationalStrategy.class);
            registerMetamodelsMethod.invoke(null, new Object[] { rs, op });
        } catch (InvocationTargetException e) {
            throw new IOException(String.format("Failed to register meta models: %s", e.getCause().getMessage()));
        } catch (Exception e) {
            throw new IOException(String.format("Failed to register meta models: %s", e.getMessage()));
        }
    }
}
