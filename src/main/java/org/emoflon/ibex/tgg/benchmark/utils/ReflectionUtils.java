package org.emoflon.ibex.tgg.benchmark.utils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.TypeVariable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.emoflon.ibex.tgg.benchmark.model.EclipseJavaProject;
import org.emoflon.ibex.tgg.operational.strategies.OperationalStrategy;
import org.reflections.Reflections;
import org.reflections.scanners.MethodParameterScanner;
import org.reflections.scanners.SubTypesScanner;
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
//        String[] classPaths = "/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.eclipse.fx.ide.css.jfx8_3.5.0.201902220800.jar:/usr/lib/jvm/java-8-openjdk/jre/lib/jfxswt.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Workspaces/TGGBenchmark.workspace/TGG-Benchmark-Includes/apiguardian-api-1.1.0.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Workspaces/TGGBenchmark.workspace/TGG-Benchmark-Includes/commons-codec-1.12.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Workspaces/TGGBenchmark.workspace/TGG-Benchmark-Includes/commons-collections4-4.3.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Workspaces/TGGBenchmark.workspace/TGG-Benchmark-Includes/commons-compress-1.18.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Workspaces/TGGBenchmark.workspace/TGG-Benchmark-Includes/commons-csv-1.7.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Workspaces/TGGBenchmark.workspace/TGG-Benchmark-Includes/commons-math3-3.6.1.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Workspaces/TGGBenchmark.workspace/TGG-Benchmark-Includes/controlsfx-8.40.15.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Workspaces/TGGBenchmark.workspace/TGG-Benchmark-Includes/curvesapi-1.06.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Workspaces/TGGBenchmark.workspace/TGG-Benchmark-Includes/guava-20.0.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Workspaces/TGGBenchmark.workspace/TGG-Benchmark-Includes/ipc-eventbus-1.0.2.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Workspaces/TGGBenchmark.workspace/TGG-Benchmark-Includes/javassist-3.21.0-GA.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Workspaces/TGGBenchmark.workspace/TGG-Benchmark-Includes/javax.json-1.1.4.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Workspaces/TGGBenchmark.workspace/TGG-Benchmark-Includes/javax.json-api-1.1.4.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Workspaces/TGGBenchmark.workspace/TGG-Benchmark-Includes/junit-jupiter-api-5.5.1.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Workspaces/TGGBenchmark.workspace/TGG-Benchmark-Includes/junit-jupiter-engine-5.5.1.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Workspaces/TGGBenchmark.workspace/TGG-Benchmark-Includes/junit-platform-commons-1.5.1.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Workspaces/TGGBenchmark.workspace/TGG-Benchmark-Includes/junit-platform-engine-1.5.1.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Workspaces/TGGBenchmark.workspace/TGG-Benchmark-Includes/log4j-api-2.12.1.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Workspaces/TGGBenchmark.workspace/TGG-Benchmark-Includes/log4j-core-2.12.1.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Workspaces/TGGBenchmark.workspace/TGG-Benchmark-Includes/opentest4j-1.2.0.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Workspaces/TGGBenchmark.workspace/TGG-Benchmark-Includes/poi-4.1.0.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Workspaces/TGGBenchmark.workspace/TGG-Benchmark-Includes/poi-ooxml-4.1.0.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Workspaces/TGGBenchmark.workspace/TGG-Benchmark-Includes/poi-ooxml-schemas-4.1.0.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Workspaces/TGGBenchmark.workspace/TGG-Benchmark-Includes/reflections-0.9.11.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Workspaces/TGGBenchmark.workspace/TGG-Benchmark-Includes/xmlbeans-3.1.0.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.eclipse.osgi_3.14.0.v20190517-1309.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.eclipse.fx.osgi_3.5.0.201902220600.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.eclipse.osgi.compatibility.state_1.1.500.v20190516-1407.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.eclipse.core.resources_3.13.400.v20190505-1655.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.eclipse.core.runtime_3.15.300.v20190508-0543.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/javax.inject_1.0.0.v20091030.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.eclipse.equinox.common_3.10.400.v20190516-1504.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.eclipse.core.jobs_3.10.400.v20190506-1457.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.eclipse.equinox.registry_3.8.400.v20190516-1504.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.eclipse.equinox.preferences_3.7.400.v20190516-1504.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.eclipse.core.contenttype_3.7.300.v20190215-2048.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.eclipse.equinox.app_1.4.200.v20190516-1504.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.eclipse.e4.core.contexts_1.8.100.v20190518-1217.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.eclipse.e4.core.di.annotations_1.6.400.v20190518-1217.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.eclipse.e4.ui.di_1.2.600.v20190510-1100.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.eclipse.e4.ui.model.workbench_2.1.400.v20190513-2118.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.eclipse.e4.ui.services_1.3.500.v20190513-2118.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.eclipse.e4.ui.workbench_1.10.0.v20190529-1505.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.eclipse.emf.ecore_2.18.0.v20190528-0845.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.eclipse.emf.common_2.16.0.v20190528-0845.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.eclipse.emf.ecore.xmi_2.16.0.v20190528-0725.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.eclipse.fx.core_3.5.0.201902220700.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.eclipse.jdt.core_3.18.0.v20190522-0428.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.eclipse.jdt.compiler.apt_1.3.600.v20190402-0634.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.eclipse.jdt.compiler.tool_1.2.600.v20190322-0450.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Workspaces/TGGBenchmark.workspace/.metadata/.plugins/org.eclipse.pde.core/.external_libraries/org.eclipse.jdt.compiler.tool_1.2.600.v20190322-0450/lib/java10api.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.eclipse.swt_3.111.0.v20190605-1801.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.eclipse.swt.gtk.linux.x86_64_3.111.0.v20190605-1801.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.eclipse.ui.workbench_3.115.0.v20190521-1602.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.eclipse.e4.ui.workbench3_0.15.100.v20190513-2118.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.eclipse.xtext.xbase.lib_2.18.0.v20190603-0326.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/com.google.guava_21.0.0.v20170206-1425.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.emoflon.ibex.common_1.0.0.201907191459.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.moflon.core.utilities_3.0.0.201902020839.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.apache.commons.io_2.6.0.v20190123-2029.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.apache.commons.lang3_3.1.0.v201403281430.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.apache.log4j_1.2.15.v201012070815.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.emoflon.ibex.gt_1.0.0.201907191459.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.emoflon.ibex.tgg.core.language_1.0.0.201907191459.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.emoflon.ibex.tgg.core.runtime_1.0.0.201907191459.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Workspaces/TGGBenchmark.workspace/.metadata/.plugins/org.eclipse.pde.core/.external_libraries/org.emoflon.ibex.tgg.core.runtime_1.0.0.201907191459/lib/commons-io-2.5.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Workspaces/TGGBenchmark.workspace/.metadata/.plugins/org.eclipse.pde.core/.external_libraries/org.emoflon.ibex.tgg.core.runtime_1.0.0.201907191459/lib/commons-lang3-3.5.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Workspaces/TGGBenchmark.workspace/.metadata/.plugins/org.eclipse.pde.core/.external_libraries/org.emoflon.ibex.tgg.core.runtime_1.0.0.201907191459/lib/gurobi.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Workspaces/TGGBenchmark.workspace/.metadata/.plugins/org.eclipse.pde.core/.external_libraries/org.emoflon.ibex.tgg.core.runtime_1.0.0.201907191459/lib/javabdd-1.0b2.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Workspaces/TGGBenchmark.workspace/.metadata/.plugins/org.eclipse.pde.core/.external_libraries/org.emoflon.ibex.tgg.core.runtime_1.0.0.201907191459/lib/glpk-java.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Workspaces/TGGBenchmark.workspace/.metadata/.plugins/org.eclipse.pde.core/.external_libraries/org.emoflon.ibex.tgg.core.runtime_1.0.0.201907191459/lib/com.google.ortools.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Workspaces/TGGBenchmark.workspace/.metadata/.plugins/org.eclipse.pde.core/.external_libraries/org.emoflon.ibex.tgg.core.runtime_1.0.0.201907191459/lib/protobuf.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Workspaces/TGGBenchmark.workspace/.metadata/.plugins/org.eclipse.pde.core/.external_libraries/org.emoflon.ibex.tgg.core.runtime_1.0.0.201907191459/lib/mipcl.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.sat4j.core_2.3.5.v201308161310.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.sat4j.pb_2.3.5.v201404071733.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.emoflon.ibex.tgg.runtime.democles_1.0.0.201907191459.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.emoflon.ibex.gt.democles_1.0.0.201907191459.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.gervarro.democles.common_3.0.0.v20170612-2312.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.gervarro.democles.emf_2.1.0.v20170707-2358.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.gervarro.democles.interpreter_1.0.0.v20181003-2250.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.gervarro.democles.interpreter.emf_1.0.0.v20180320-2254.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.gervarro.democles.interpreter.incremental_1.0.0.v20180303-2328.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.gervarro.democles.interpreter.incremental.emf_1.0.0.v20180320-2254.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.gervarro.democles.notification.emf_1.0.0.v20181003-2254.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.gervarro.democles.plan.incremental.leaf_1.0.0.v20181003-2253.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.gervarro.democles.specification.emf_3.0.0.v20170707-2358.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.gervarro.notification_1.0.0.v20170929-1147.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.gervarro.plan.dynprog_1.0.1.v20161103-0122.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.gervarro.util_1.0.0.v20181003-2249.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.gervarro.democles.interpreter.lightning_1.0.0.v20161103-0122.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.gervarro.democles.plan_3.0.0.v20161103-0012.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/org.gervarro.democles.plan.emf_1.0.0.v20161103-0012.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Eclipse/Eclipse eMoflon/plugins/javax.annotation_1.2.0.v201602091430.jar:/home/andre/Studium/Projektseminar_Softwaresysteme/Workspaces/TGGBenchmark.workspace/TGG-Benchmark/target/test-classes:/home/andre/Studium/Projektseminar_Softwaresysteme/Workspaces/TGGBenchmark.workspace/TGG-Benchmark/target/classes:/home/andre/.m2/repository/org/junit/jupiter/junit-jupiter-api/5.5.1/junit-jupiter-api-5.5.1.jar:/home/andre/.m2/repository/org/apiguardian/apiguardian-api/1.1.0/apiguardian-api-1.1.0.jar:/home/andre/.m2/repository/org/opentest4j/opentest4j/1.2.0/opentest4j-1.2.0.jar:/home/andre/.m2/repository/org/junit/platform/junit-platform-commons/1.5.1/junit-platform-commons-1.5.1.jar:/home/andre/.m2/repository/org/junit/jupiter/junit-jupiter-engine/5.5.1/junit-jupiter-engine-5.5.1.jar:/home/andre/.m2/repository/org/junit/platform/junit-platform-engine/1.5.1/junit-platform-engine-1.5.1.jar:/home/andre/.m2/repository/org/controlsfx/controlsfx/8.40.15/controlsfx-8.40.15.jar:/home/andre/.m2/repository/javax/json/javax.json-api/1.1.4/javax.json-api-1.1.4.jar:/home/andre/.m2/repository/org/glassfish/javax.json/1.1.4/javax.json-1.1.4.jar:/home/andre/.m2/repository/org/reflections/reflections/0.9.11/reflections-0.9.11.jar:/home/andre/.m2/repository/com/google/guava/guava/20.0/guava-20.0.jar:/home/andre/.m2/repository/org/javassist/javassist/3.21.0-GA/javassist-3.21.0-GA.jar:/home/andre/.m2/repository/org/apache/logging/log4j/log4j-api/2.12.1/log4j-api-2.12.1.jar:/home/andre/.m2/repository/org/apache/logging/log4j/log4j-core/2.12.1/log4j-core-2.12.1.jar:/home/andre/.m2/repository/org/apache/poi/poi/4.1.0/poi-4.1.0.jar:/home/andre/.m2/repository/commons-codec/commons-codec/1.12/commons-codec-1.12.jar:/home/andre/.m2/repository/org/apache/commons/commons-collections4/4.3/commons-collections4-4.3.jar:/home/andre/.m2/repository/org/apache/commons/commons-math3/3.6.1/commons-math3-3.6.1.jar:/home/andre/.m2/repository/org/apache/poi/poi-ooxml/4.1.0/poi-ooxml-4.1.0.jar:/home/andre/.m2/repository/org/apache/poi/poi-ooxml-schemas/4.1.0/poi-ooxml-schemas-4.1.0.jar:/home/andre/.m2/repository/org/apache/xmlbeans/xmlbeans/3.1.0/xmlbeans-3.1.0.jar:/home/andre/.m2/repository/org/apache/commons/commons-compress/1.18/commons-compress-1.18.jar:/home/andre/.m2/repository/com/github/virtuald/curvesapi/1.06/curvesapi-1.06.jar:/home/andre/.m2/repository/org/apache/commons/commons-csv/1.7/commons-csv-1.7.jar:/home/andre/.m2/repository/org/terracotta/ipc-eventbus/1.0.2/ipc-eventbus-1.0.2.jar".split(":"); 
                
//        System.out.println(System.getProperty("java.class.path"));
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
            Class<? extends Object> clazz = classLoader.loadClass(className);
            for(Class<?> t : parameters)
                System.out.println(t.getName());
            for (Method m : clazz.getDeclaredMethods()) {
                System.out.println(m.getName());
                
                for(Parameter t : m.getParameters()) {
                    System.out.println(t.getType());
                    System.out.println(t.getType().equals(parameters[0]));
                    System.out.println(t.getType().equals(parameters[1]));
                }
            }
            return clazz.getDeclaredMethod(methodName, parameters);
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
    
//    public static Set<Method> getClasses(ClassLoader classLoader, Path classPath) throws MalformedURLException {
//        FilterBuilder classFileFilter = new FilterBuilder().include(".*\\.class");
//        Collection<URL> urls = Arrays.asList(classPath.toUri().toURL());
//
//        Reflections reflections = new Reflections(new ConfigurationBuilder().setUrls(urls).addClassLoader(classLoader)
//                .setScanners(new SubTypesScanner(false)).filterInputsBy(classFileFilter));
//
//        Set<Class<? extends Object>> clazz = reflections.getSubTypesOf(Object.class);
//        for (Class<? extends Object> class1 : clazz) {
//            if (class1.getName().equals("org.emoflon.ibex.tgg.run.companytoit._RegistrationHelper")) {
//                System.out.println("############ Does it matcH?: " + class1.equals(obj));
//            }
//        }
//        
//        return reflections.getMethodsMatchParams(parameters);
//    }

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
        
        for (int i = 0; i < urls.length; i++) {
            System.out.println(urls[i]);
        }

        return new URLClassLoader(urls);
    }
    
    public static URLClassLoader createClassLoader(EclipseJavaProject javaProject) throws MalformedURLException {
        Set<URL> urls = javaProject.getClasspaths();
//        for (URL url : urls) {
//            System.out.println(url.toString());
//        }
        
        URLClassLoader u = new URLClassLoader(new URL[urls.size()]);
        
        System.out.println("here motherfucker");
        
        try {
            Class<? extends Object> xxx = u.loadClass("org.emoflon.ibex.tgg.run.companytoit._RegistrationHelper");
            
            System.out.println(xxx.getName());
            
        } catch (ClassNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
        System.out.println("eclipse sucks");
        
        return u;
//        return new URLClassLoader(new URL[urls.size()]);
    }
}