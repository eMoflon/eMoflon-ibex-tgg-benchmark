package org.emoflon.ibex.tgg.benchmark.ui.eclipse_view.handlers;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.logging.log4j.Logger;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.emoflon.ibex.tgg.benchmark.ui.plugin_preferences.PluginPreferencesWindow;

public class PluginPreferencesHandler {

    @Inject
    @Named("logger")
    Logger LOG;

    @Execute
    public void execute() {
        try {
            PluginPreferencesWindow ppw = new PluginPreferencesWindow();
            ppw.show();
        } catch (IOException e) {
            LOG.error("Failed to open plugin preferences. Reason: " + e.getMessage());
        }
    }

    @CanExecute
    public boolean canExecute() {
        return true;
    }
}
