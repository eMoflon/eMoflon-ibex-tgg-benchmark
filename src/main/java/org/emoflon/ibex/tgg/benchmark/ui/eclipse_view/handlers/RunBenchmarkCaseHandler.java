package org.emoflon.ibex.tgg.benchmark.ui.eclipse_view.handlers;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.logging.log4j.Logger;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.emoflon.ibex.tgg.benchmark.Core;
import org.emoflon.ibex.tgg.benchmark.model.BenchmarkCasePreferences;
import org.emoflon.ibex.tgg.benchmark.ui.eclipse_view.parts.TGGBenchmarkViewPart.BenchmarkCaseTableView;

import javafx.collections.ObservableList;

public class RunBenchmarkCaseHandler {

    @Inject
    @Named("logger")
    Logger LOG;

    @Execute
    public void execute(@Optional @Named("benchmarkCases") ObservableList<BenchmarkCasePreferences> benchmarkCases) {
        if (benchmarkCases != null && benchmarkCases.size() > 0) {
            List<BenchmarkCasePreferences> bcpsToExecute = benchmarkCases.stream()
                    .filter(BenchmarkCasePreferences::isMarkedForExecution).collect(Collectors.toList());
            if (bcpsToExecute.size() > 0) {
                Core.getInstance().runBenchmark(bcpsToExecute);
            }
        }
    }

    @CanExecute
    public boolean canExecute(@Optional @Named("benchmarkCasesTable") BenchmarkCaseTableView benchmarkCaseTable) {
        return true;
    }
}
