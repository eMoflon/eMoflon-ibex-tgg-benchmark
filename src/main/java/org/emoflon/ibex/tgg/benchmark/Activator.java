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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;



public class Activator implements BundleActivator {
    
    private static final Logger LOG = LoggerFactory.getLogger(Core.PLUGIN_NAME);
    
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

    private void configureLogbackInBundle(Bundle bundle) throws JoranException, IOException {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator jc = new JoranConfigurator();
        jc.setContext(context);
        context.reset();
        
        // this assumes that the logback.xml file is in the root of the bundle.
        URL logbackConfigFileUrl = FileLocator.find(bundle, new Path("logback.xml"),null);
        jc.doConfigure(logbackConfigFileUrl.openStream());
    }

    public Bundle getBundle() {
        return bundle;
    }
}
