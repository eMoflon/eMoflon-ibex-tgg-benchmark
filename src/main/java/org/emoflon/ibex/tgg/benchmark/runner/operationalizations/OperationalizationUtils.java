package org.emoflon.ibex.tgg.benchmark.runner.operationalizations;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.emoflon.ibex.tgg.benchmark.utils.ReflectionUtils;
import org.emoflon.ibex.tgg.operational.strategies.OperationalStrategy;

public abstract class OperationalizationUtils {

    public static void registerUserMetamodels(ResourceSet rs, OperationalStrategy op, ClassLoader classLoader,
            String className, String methodName) throws IOException {
        try {
            Method registerMetamodelsMethod = ReflectionUtils
                    .getMethodByName(classLoader, className, methodName, ResourceSet.class, OperationalStrategy.class);
            registerMetamodelsMethod.invoke(null, new Object[] { rs, op });
        } catch (InvocationTargetException e) {
            throw new IOException(String.format("Failed to register meta models: %s", e.getCause().getMessage()));
        } catch (Exception e) {
            throw new IOException(String.format("Failed to register meta models: %s", e.getMessage()));
        }
    }
}
