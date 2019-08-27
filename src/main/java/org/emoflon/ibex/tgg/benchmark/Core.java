package org.emoflon.ibex.tgg.benchmark;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.emoflon.ibex.tgg.benchmark.model.BenchmarkCasePreferences;
import org.emoflon.ibex.tgg.benchmark.model.EclipseTggProject;
import org.emoflon.ibex.tgg.benchmark.model.IEclipseWorkspace;
import org.emoflon.ibex.tgg.benchmark.model.PluginPreferences;
import org.emoflon.ibex.tgg.benchmark.utils.AggregateObservableList;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

/**
 * Core class for TGG Benchmark plugin.
 */
public class Core {

    public static final String VERSION = "0.1.0";
    public static final String PLUGIN_NAME = "TGG-Benchmark";
    
    private static final Logger LOG = LogManager.getLogger(Core.PLUGIN_NAME);
    private static Core instance;
    
    private PluginPreferences pluginPreferences;
    private IEclipseWorkspace workspace;
    private AggregateObservableList<BenchmarkCasePreferences> benchmarkCasePreferences;
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
    public void runBenchmark(List<BenchmarkCasePreferences> benchmarkCases) {
        if (benchmarkJob != null && benchmarkJob.getState() == Job.RUNNING) {
            Alert alert = new Alert(AlertType.CONFIRMATION, "", ButtonType.NO, ButtonType.YES);
            
            alert.setTitle("Run Benchmark");
            alert.setHeaderText("A benchmark is already in progress.\nDo you want to cancel the running benchmark and start a new one?");
            
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
        
        benchmarkJob = Job.create("My Job", monitor -> {
            SubMonitor subMonitor = SubMonitor.convert(monitor, benchmarkCases.size());
            subMonitor.setTaskName("Running TGG Benchmark");

            for (BenchmarkCasePreferences benchmarkCasePreferences : benchmarkCases) {
                try {
                    System.out.println("Job doing work");
                    TimeUnit.SECONDS.sleep(20);
                    subMonitor.split(1);
                } catch (InterruptedException e) {
                    System.out.println("Interrupt");
                }
            }
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
     * @return the benchmarkCasePreferences
     */
    public ObservableList<BenchmarkCasePreferences> getBenchmarkCases() {
        if (benchmarkCasePreferences == null) {
            benchmarkCasePreferences = new AggregateObservableList<>();
            for (EclipseTggProject project : workspace.getTggProjects()) {
                ObservableList<BenchmarkCasePreferences> bcpl = project.getBenchmarkCasePreferences();
                benchmarkCasePreferences.addList(bcpl);
            }
        }
        
        return benchmarkCasePreferences;
    }
}