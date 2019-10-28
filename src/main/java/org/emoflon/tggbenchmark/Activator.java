package org.emoflon.tggbenchmark;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.emoflon.tggbenchmark.gui.model.PluginPreferences;
import org.emoflon.tggbenchmark.workspace.EclipseWorkspace;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin {

    private static Activator plugin;
    private static Core pluginCore;
    private static Logger LOG;

    /**
     * The constructor
     */
    public Activator() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.
     * BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;

        // configure log4j2
        Configurator.initialize(new DefaultConfiguration());
        LOG = LogManager.getLogger(Core.PLUGIN_NAME);

        LOG.debug("Start plugin 'TGG Benchmark'");

        // create instance of Core class
        pluginCore = Core.getInstance();
        EclipseWorkspace eclipseWorkspace = new EclipseWorkspace();
        pluginCore.setWorkspace(eclipseWorkspace);
        PluginPreferences pluginPreferences = new PluginPreferences(eclipseWorkspace.getPluginPreferencesFilePath());
        pluginCore.setPluginPreferences(pluginPreferences);

        // set correct log level
        Core.setLogLevel(Level.getLevel(pluginPreferences.getLogLevel().toString()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }
}
