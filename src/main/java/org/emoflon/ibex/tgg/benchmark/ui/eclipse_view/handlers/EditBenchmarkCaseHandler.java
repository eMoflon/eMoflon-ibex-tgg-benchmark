package org.emoflon.ibex.tgg.benchmark.ui.eclipse_view.handlers;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.logging.log4j.Logger;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.emoflon.ibex.tgg.benchmark.model.BenchmarkCasePreferences;
import org.emoflon.ibex.tgg.benchmark.ui.benchmark_case_preferences.BenchmarkCasePreferencesWindow;
import org.emoflon.ibex.tgg.benchmark.ui.eclipse_view.parts.TGGBenchmarkViewPart.BenchmarkCaseTableView;

public class EditBenchmarkCaseHandler {
    
    @Inject
    @Named("logger") 
    Logger LOG;

    @Execute
    public void execute(@Optional @Named("benchmarkCasesTable") BenchmarkCaseTableView benchmarkCaseTable) {
        if (benchmarkCaseTable != null) {
            BenchmarkCasePreferences selectedBenchmarkCase = benchmarkCaseTable.getSelectionModel().getSelectedItem();
            if (selectedBenchmarkCase != null) {
                try {
                    new BenchmarkCasePreferencesWindow(selectedBenchmarkCase);
                } catch (IOException e) {
                    LOG.error("Failed to open benchmark case preferences. Reason: " + e.getMessage());
                }
            }
        }
    }
    
    @CanExecute
    public boolean canExecute(@Optional @Named("benchmarkCasesTable") BenchmarkCaseTableView benchmarkCaseTable) {
        return benchmarkCaseTable != null && benchmarkCaseTable.getSelectionModel().getSelectedIndex() != -1;
    }
}
