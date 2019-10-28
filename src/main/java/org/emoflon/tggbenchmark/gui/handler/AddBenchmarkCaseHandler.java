package org.emoflon.tggbenchmark.gui.handler;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.logging.log4j.Logger;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.emoflon.tggbenchmark.gui.BenchmarkCaseWindow;
import org.emoflon.tggbenchmark.gui.controller.benchmark_cases_table.BenchmarkCasesTableController.BenchmarkCaseTableView;

//core.runtime.IProgressMonitor;

public class AddBenchmarkCaseHandler {

    @Inject
    @Named("logger")
    Logger LOG;

    @Execute
    public void execute() {
        try {
            BenchmarkCaseWindow bcw = new BenchmarkCaseWindow(null);
            bcw.show();
        } catch (IOException e) {
            LOG.error("Failed to open benchmark case preferences. Reason: " + e.getMessage());
        }
    }

    @CanExecute
    public boolean canExecute(@Optional @Named("benchmarkCasesTable") BenchmarkCaseTableView benchmarkCaseTable) {
        return true;
    }
}
