package org.emoflon.ibex.tgg.benchmark.ui.eclipse_view.handlers;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.logging.log4j.Logger;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.emoflon.ibex.tgg.benchmark.model.BenchmarkCase;
import org.emoflon.ibex.tgg.benchmark.ui.benchmark_case_preferences.BenchmarkCaseWindow;
import org.emoflon.ibex.tgg.benchmark.ui.eclipse_view.parts.TGGBenchmarkViewPart.BenchmarkCaseTableView;

public class EditBenchmarkCaseHandler {

    @Inject
    @Named("logger")
    Logger LOG;

    @Execute
    public void execute(@Optional @Named("benchmarkCasesTable") BenchmarkCaseTableView benchmarkCaseTable) {
        if (benchmarkCaseTable != null) {
            BenchmarkCase selectedBenchmarkCase = benchmarkCaseTable.getSelectionModel().getSelectedItem();
            if (selectedBenchmarkCase != null) {
                try {
                    BenchmarkCaseWindow bcw = new BenchmarkCaseWindow(selectedBenchmarkCase);
                    bcw.show();
                } catch (IOException e) {
                    LOG.error("Failed to open benchmark case preferences. Reason: " + e.getMessage());
                }
            }
        }
    }

    @CanExecute
    public boolean canExecute(@Optional @Named("benchmarkCasesTable") BenchmarkCaseTableView benchmarkCaseTable) {
        return true;
    }
}
