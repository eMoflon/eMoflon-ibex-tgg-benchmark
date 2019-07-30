package org.emoflon.ibex.tgg.benchmark;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.emoflon.ibex.tgg.benchmark.model.BenchmarkCasePreferences;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {
    
    // http://blog.vogella.com/2010/07/06/reading-resources-from-plugin/
//    @Inject @Named(E4Workbench.INSTANCE_LOCATION) private String instanceLocation;
    
    private static Activator plugin = null;
    private IEclipseContext appContext;
    private IEclipseContext serviceContext;    

    public static Activator getInstance() {
        return plugin;
    }

    public void start(BundleContext bundleContext) throws Exception {
        plugin = this;
        serviceContext = EclipseContextFactory.getServiceContext(bundleContext);
        appContext = serviceContext.createChild();
        
        System.out.println("TGG Activator");
        
//        appContext.set(BenchmarkCasePreferences.class.getName(), bcp);
    }

    public void stop(BundleContext bundleContext) throws Exception {
        appContext.dispose();
    }

    /**
     * @return the appContext
     */
    public IEclipseContext getAppContext() {
        return appContext;
    }


}
