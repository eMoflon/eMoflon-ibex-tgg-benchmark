package org.emoflon.ibex.tgg.benchmark.runner.operationalizations;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.emoflon.ibex.tgg.operational.strategies.OperationalStrategy;

public abstract class OperationalizationUtils {

    public static void registerUserMetamodels(ResourceSet rs, OperationalStrategy op, ClassLoader classLoader,
            String className, String methodName) throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, ClassNotFoundException, NoSuchMethodException, SecurityException {

        Method registerMetamodelsMethod = org.emoflon.ibex.tgg.benchmark.utils.RefelctionUtils.getMethodForName(classLoader,
                className, methodName, ResourceSet.class, OperationalStrategy.class);
        registerMetamodelsMethod.invoke(null, new Object[] { rs, op });
    }
}
