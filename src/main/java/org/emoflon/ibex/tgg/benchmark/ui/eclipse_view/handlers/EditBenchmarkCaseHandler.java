package org.emoflon.ibex.tgg.benchmark.ui.eclipse_view.handlers;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Shell;
import org.emoflon.ibex.tgg.benchmark.ui.eclipse_view.parts.TGGBenchmarkView.BenchmarkCaseTableView;

public class EditBenchmarkCaseHandler {

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell s, BenchmarkCaseTableView table) {
		System.out.println(table);
		
		System.out.println("Nothings here yet");
	}
	
	@CanExecute
	public boolean canExecute(@Named(IServiceConstants.ACTIVE_SELECTION) @Optional Object selection) {
		if (selection != null) {
			System.out.println("Slected: " + selection.getClass().getName());
		}
		return true;
	}
}
