package org.emoflon.tggbenchmark;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.eclipse.core.runtime.jobs.Job;
import org.emoflon.tggbenchmark.gui.component.AggregateObservableList;
import org.emoflon.tggbenchmark.gui.model.BenchmarkCase;
import org.emoflon.tggbenchmark.gui.model.PluginPreferences;
import org.emoflon.tggbenchmark.runner.BenchmarkRunner;
import org.emoflon.tggbenchmark.workspace.EclipseTggProject;
import org.emoflon.tggbenchmark.workspace.IEclipseWorkspace;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;

/**
 * Core class for TGG Benchmark plugin.
 */
public class Core {

    public static final String VERSION = "0.1.0";
    public static final String PLUGIN_NAME = "TGG-Benchmark";

    private static Core instance;

    private PluginPreferences pluginPreferences;
    private IEclipseWorkspace workspace;

    /**
     * Contains the benchmark cases that are displayed in the plugin view. The list
     * contains a list of benchmark cases for every project in the workspace. It
     * aggregates the lists and offers direct access to their entries.
     */
    private AggregateObservableList<BenchmarkCase> benchmarkCases;
    private Job benchmarkJob;

    private Core() {
        super();
    }

    /**
     * @return get a singleton of Core
     */
    public static synchronized Core getInstance() {
        if (Core.instance == null) {
            Core.instance = new Core();
        }
        return Core.instance;
    }

    /**
     * Runs the given benchmark cases.
     * 
     * @param benchmarkCases the benchmark cases that shall be executed
     */
    public void runBenchmark(List<BenchmarkCase> benchmarkCases) {
        if (benchmarkJob != null && benchmarkJob.getState() == Job.RUNNING) {
            Alert alert = new Alert(AlertType.CONFIRMATION, "", ButtonType.NO, ButtonType.YES);

            alert.setTitle("Run Benchmark");
            alert.setHeaderText(
                    "A benchmark is already in progress.\nDo you want to cancel the running benchmark and start a new one?");

            Optional<ButtonType> option = alert.showAndWait();

            if (option.get() == null || option.get() == ButtonType.NO) {
                return;
            } else if (option.get() == ButtonType.YES) {
                benchmarkJob.cancel();
                benchmarkJob.getThread().interrupt();
                try {
                    benchmarkJob.join();
                } catch (InterruptedException e) {
                    return;
                }
            }
        }

        benchmarkJob = Job.create("TGG Benchmark", monitor -> {
            monitor.setTaskName("Preparing Benchmark");

            BenchmarkRunner benchmarkRunner = new BenchmarkRunner(benchmarkCases, monitor);
            List<String> foundErrors = benchmarkRunner.checkForErrors();
            if (!foundErrors.isEmpty()) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Run Benchmark");
                    alert.setHeaderText(
                            "The following errors need to be fixed before the benchmark cases can be executed:");

                    TextArea textArea = new TextArea(String.join("\n\n", foundErrors));
                    textArea.setEditable(false);
                    textArea.setWrapText(true);
                    textArea.setMaxWidth(Double.MAX_VALUE);
                    textArea.setMaxHeight(Double.MAX_VALUE);

                    alert.getDialogPane().setExpandableContent(textArea);
                    alert.getDialogPane().setExpanded(true);
                    alert.getDialogPane().setPrefSize(600, 600);
                    alert.showAndWait();
                });
                return;
            }

            benchmarkRunner.run();
        });

        benchmarkJob.schedule();
    }

    /**
     * @return the workspace
     */
    public IEclipseWorkspace getWorkspace() {
        return workspace;
    }

    /**
     * @param workspace the workspace to set
     */
    public void setWorkspace(IEclipseWorkspace workspace) {
        this.workspace = workspace;
    }

    /**
     * @return the pluginPreferences
     */
    public PluginPreferences getPluginPreferences() {
        return pluginPreferences;
    }

    /**
     * @param pluginPreferences the pluginPreferences to set
     */
    public void setPluginPreferences(PluginPreferences pluginPreferences) {
        this.pluginPreferences = pluginPreferences;
    }

    /**
     * @return the benchmarkCase
     */
    public ObservableList<BenchmarkCase> getBenchmarkCases() {
        if (benchmarkCases == null) {
            benchmarkCases = new AggregateObservableList<>();
            for (EclipseTggProject project : workspace.getTggProjects()) {
                ObservableList<BenchmarkCase> bcl = project.getBenchmarkCase();
                benchmarkCases.addList(bcl);
            }
        }

        return benchmarkCases;
    }

    /**
     * Set the Log4J logging level and update loggers.
     * 
     * @param logLevel the logging level
     */
    public static void setLogLevel(Level logLevel) {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig(Core.PLUGIN_NAME);
        loggerConfig.setLevel(logLevel);
        ctx.updateLoggers();
    }
}