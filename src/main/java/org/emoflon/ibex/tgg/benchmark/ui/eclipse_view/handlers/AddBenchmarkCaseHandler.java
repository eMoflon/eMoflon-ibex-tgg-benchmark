package org.emoflon.ibex.tgg.benchmark.ui.eclipse_view.handlers;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.swt.widgets.Shell;

//core.runtime.IProgressMonitor;

public class AddBenchmarkCaseHandler {

    @Execute
    public void execute(IWorkbench workbench, @Named(IServiceConstants.ACTIVE_SHELL) Shell s) {
//        workbench.close();
        
    }
}
