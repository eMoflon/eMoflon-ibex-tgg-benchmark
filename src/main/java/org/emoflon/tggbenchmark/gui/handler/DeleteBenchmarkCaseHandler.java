package org.emoflon.tggbenchmark.gui.handler;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.logging.log4j.Logger;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.emoflon.tggbenchmark.gui.controller.benchmark_cases_table.BenchmarkCasesTableController.BenchmarkCaseTableView;
import org.emoflon.tggbenchmark.gui.model.BenchmarkCase;
import org.emoflon.tggbenchmark.workspace.EclipseTggProject;

public class DeleteBenchmarkCaseHandler {

    @Inject
    @Named("logger")
    Logger LOG;

    @Execute
    public void execute(@Optional @Named("benchmarkCasesTable") BenchmarkCaseTableView benchmarkCaseTable) {
        if (benchmarkCaseTable != null) {
            BenchmarkCase bc = benchmarkCaseTable.getSelectionModel().getSelectedItem();
            EclipseTggProject eclipseProject = bc.getEclipseProject();
            eclipseProject.removeBenchmarkCase(bc);
        }
    }

    @CanExecute
    public boolean canExecute(@Optional @Named("benchmarkCasesTable") BenchmarkCaseTableView benchmarkCaseTable) {
        return true;
    }
}
