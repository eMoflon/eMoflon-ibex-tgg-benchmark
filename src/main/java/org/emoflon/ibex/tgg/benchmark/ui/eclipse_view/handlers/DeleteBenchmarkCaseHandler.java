package org.emoflon.ibex.tgg.benchmark.ui.eclipse_view.handlers;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.logging.log4j.Logger;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.emoflon.ibex.tgg.benchmark.model.BenchmarkCase;
import org.emoflon.ibex.tgg.benchmark.model.EclipseTggProject;
import org.emoflon.ibex.tgg.benchmark.ui.eclipse_view.parts.TGGBenchmarkViewPart.BenchmarkCaseTableView;

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
