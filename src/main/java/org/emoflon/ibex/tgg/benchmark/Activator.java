package org.emoflon.ibex.tgg.benchmark;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.emoflon.ibex.tgg.benchmark.model.BenchmarkCasePreferences;
import org.emoflon.ibex.tgg.benchmark.model.EclipseWorkspace;
import org.emoflon.ibex.tgg.benchmark.utils.AsyncActions;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.apache.logging.log4j.spi.StandardLevel;



public class Activator implements BundleActivator {
    
    private static Logger LOG;
    
    private static Activator plugin = null;
    private static Core pluginCore = null;
    private IEclipseContext appContext;
    private IEclipseContext serviceContext;
    
    private Bundle bundle;
    
    public static Activator getInstance() {
        return plugin;
    }

    public void start(BundleContext bundleContext) throws Exception {
        LOG.debug("Start plugin 'TGG Benchmark'");

        plugin = this;
        bundle = bundleContext.getBundle();
        serviceContext = EclipseContextFactory.getServiceContext(bundleContext);
        appContext = serviceContext.createChild();
        
        // create instance of Core class
        EclipseWorkspace workspace = new EclipseWorkspace();
        pluginCore = Core.createInstance(workspace);
        pluginCore.loadPluginPreferences();
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        appContext.dispose();
        AsyncActions.stopAll();
    }

    /**
     * @return the appContext
     */
    public IEclipseContext getAppContext() {
        return appContext;
    }

    public Bundle getBundle() {
        return bundle;
    }
}
