package org.emoflon.tggbenchmark.runner;

import java.io.IOException;

import javax.json.JsonException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.emoflon.tggbenchmark.Core;
import org.emoflon.tggbenchmark.gui.Utils;
import org.emoflon.tggbenchmark.gui.model.BenchmarkCase;
import org.emoflon.tggbenchmark.gui.model.PluginPreferences;
import org.emoflon.tggbenchmark.runner.BenchmarkRunner;
import org.emoflon.tggbenchmark.runner.report.ReportFileType;
import org.emoflon.tggbenchmark.workspace.EclipseTggProject;
import org.emoflon.tggbenchmark.workspace.IEclipseWorkspace;

import javafx.collections.FXCollections;

public class BenchmarkRunnerTest {

    public static void main(String[] args) throws JsonException, IOException {
        Utils.initConfiguration();

        IEclipseWorkspace eclipseWorkspace = Core.getInstance().getWorkspace();
        PluginPreferences pluginPreferences = Core.getInstance().getPluginPreferences();
        pluginPreferences.setReportFileType(ReportFileType.EXCEL);
        pluginPreferences.setDefaultTimeout(600);

        EclipseTggProject eclipseProject = eclipseWorkspace.getTggProjects().get(0);
        BenchmarkCase bc = eclipseProject.getBenchmarkCase().get(0);
        // pluginPreferences.setRepetitions(1);
        // bc.setModelgenModelSizes(FXCollections.observableArrayList(100));
        // bc.setInitialBwdActive(false);
        // bc.setInitialFwdActive(false);
        // bc.setFwdActive(false);
        // bc.setBwdActive(false);
        // bc.setCcActive(true);
        // bc.setCoActive(false);

        IProgressMonitor progressMonitor = new EmptyProgressMonitor();

        BenchmarkRunner benchmarkRunner = new BenchmarkRunner(eclipseProject.getBenchmarkCase(), progressMonitor);
        benchmarkRunner.run();
    }
}
