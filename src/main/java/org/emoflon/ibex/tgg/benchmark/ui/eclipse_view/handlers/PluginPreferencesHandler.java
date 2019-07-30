package org.emoflon.ibex.tgg.benchmark.ui.eclipse_view.handlers;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Shell;

public class PluginPreferencesHandler {

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell s) {
        System.out.println("Nothings here yet");
    }
}
