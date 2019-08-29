package org.emoflon.ibex.tgg.benchmark.runner;

import java.io.IOException;

import javax.json.JsonException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.emoflon.ibex.tgg.benchmark.Core;
import org.emoflon.ibex.tgg.benchmark.model.EclipseTggProject;
import org.emoflon.ibex.tgg.benchmark.model.IEclipseWorkspace;
import org.emoflon.ibex.tgg.benchmark.model.PluginPreferences;
import org.emoflon.ibex.tgg.benchmark.runner.report.ReportFileType;
import org.emoflon.ibex.tgg.benchmark.ui.Utils;

public class BenchmarkRunnerTest {

    public static void main(String[] args) throws JsonException, IOException {
        Utils.initConfiguration();

        IEclipseWorkspace eclipseWorkspace = Core.getInstance().getWorkspace();
        PluginPreferences pluginPreferences = Core.getInstance().getPluginPreferences();
        pluginPreferences.setReportFileType(ReportFileType.EXCEL);
        // pluginPreferences.setReportFileType("CSV");
        pluginPreferences.setDefaultTimeout(60);

        EclipseTggProject eclipseProject = eclipseWorkspace.getTggProjects().get(0);
        IProgressMonitor progressMonitor = new EmptyProgressMonitor();

        BenchmarkRunner benchmarkRunner = new BenchmarkRunner(eclipseProject.getBenchmarkCasePreferences(),
                progressMonitor);
        benchmarkRunner.run();
    }
}
