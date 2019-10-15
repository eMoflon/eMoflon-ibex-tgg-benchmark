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
import org.emoflon.ibex.tgg.benchmark.model.BenchmarkCase;
import org.emoflon.ibex.tgg.benchmark.ui.eclipse_view.parts.TGGBenchmarkViewPart.BenchmarkCaseTableView;

import javafx.collections.ObservableList;

public class RunBenchmarkCaseHandler {

    @Inject
    @Named("logger")
    Logger LOG;

    @Execute
    public void execute(@Optional @Named("benchmarkCases") ObservableList<BenchmarkCase> benchmarkCases) {
        if (benchmarkCases != null && benchmarkCases.size() > 0) {
            List<BenchmarkCase> bcsToExecute = benchmarkCases.stream()
                    .filter(BenchmarkCase::isMarkedForExecution).collect(Collectors.toList());
            if (bcsToExecute.size() > 0) {
                Core.getInstance().runBenchmark(bcsToExecute);
            }
        }
    }

    @CanExecute
    public boolean canExecute(@Optional @Named("benchmarkCasesTable") BenchmarkCaseTableView benchmarkCaseTable) {
        return true;
    }
}
