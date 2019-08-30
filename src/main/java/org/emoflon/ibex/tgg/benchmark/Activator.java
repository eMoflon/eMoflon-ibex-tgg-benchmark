package org.emoflon.ibex.tgg.benchmark;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.emoflon.ibex.tgg.benchmark.model.EclipseWorkspace;
import org.emoflon.ibex.tgg.benchmark.model.PluginPreferences;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

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

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        // configure log4j2
        Configurator.initialize(new DefaultConfiguration());

        LOG = LogManager.getLogger(Core.PLUGIN_NAME);

        LOG.debug("Start plugin 'TGG Benchmark'");

        plugin = this;
        bundle = bundleContext.getBundle();
        serviceContext = EclipseContextFactory.getServiceContext(bundleContext);
        appContext = serviceContext.createChild();

        // create instance of Core class
        pluginCore = Core.getInstance();
        EclipseWorkspace eclipseWorkspace = new EclipseWorkspace();
        pluginCore.setWorkspace(eclipseWorkspace);
        PluginPreferences pluginPreferences = new PluginPreferences(eclipseWorkspace.getPluginPreferencesFilePath());
        pluginCore.setPluginPreferences(pluginPreferences);

        // set correct log level
        Core.setLogLevel(Level.getLevel(pluginPreferences.getLogLevel().toString()));
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        appContext.dispose();
    }

    /**
     * @return the appContext
     */
    public IEclipseContext getAppContext() {
        return appContext;
    }

    /**
     * @return the bundle
     */
    public Bundle getBundle() {
        return bundle;
    }
}
