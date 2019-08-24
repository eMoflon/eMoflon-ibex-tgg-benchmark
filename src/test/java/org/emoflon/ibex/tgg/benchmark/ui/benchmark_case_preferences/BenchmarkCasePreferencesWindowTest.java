package org.emoflon.ibex.tgg.benchmark.ui.benchmark_case_preferences;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import javax.json.JsonException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.emoflon.ibex.tgg.benchmark.Core;
import org.emoflon.ibex.tgg.benchmark.model.BenchmarkCasePreferences;
import org.emoflon.ibex.tgg.benchmark.model.EclipseJavaProject;
import org.emoflon.ibex.tgg.benchmark.model.EclipseTggProject;
import org.emoflon.ibex.tgg.benchmark.model.EclipseWorkspaceDebug;
import org.emoflon.ibex.tgg.benchmark.model.PluginPreferences;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.stage.Stage;

public class BenchmarkCasePreferencesWindowTest extends Application {

    private static PluginPreferences pluginPreferences;
    private static EclipseWorkspaceDebug eclipseWorkspace;
    private static Core pluginCore;

    @Override
    public void start(Stage primaryStage) throws IOException {
        BenchmarkCasePreferencesWindow bcpw = new BenchmarkCasePreferencesWindow(eclipseWorkspace.getTggProjects().get(0).getBenchmarkCasePreferences());

        try {
            bcpw.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws JsonException, IOException {
        // configure logger
        Configurator.initialize(new DefaultConfiguration());
        Configurator.setRootLevel(Level.ALL);

        eclipseWorkspace = new EclipseWorkspaceDebug();
        pluginPreferences = new PluginPreferences();

        pluginCore = Core.createInstance(eclipseWorkspace);
        pluginCore.setPluginPreferences(pluginPreferences);
        
        EclipseTggProject companyToIt = createEclipseProject("CompanyToIT",
                new String[] { "CompanyLanguage", "ITLanguage", "CompanyToIT", });
        BenchmarkCasePreferences companyToItBcp = new BenchmarkCasePreferences();
        companyToItBcp.setBenchmarkCaseName(companyToIt.getName());
        companyToIt.setBenchmarkCasePreferences(companyToItBcp);

        EclipseTggProject familyToPersonas = createEclipseProject("FamiliesToPersons_V1",
                new String[] { "SimpleFamilies", "SimplePersons", "FamiliesToPersons_V1" });
        eclipseWorkspace.setTggProjects(FXCollections.observableArrayList(companyToIt, familyToPersonas));
        BenchmarkCasePreferences familyToPersonasBcp = new BenchmarkCasePreferences();
        familyToPersonasBcp.setBenchmarkCaseName(companyToIt.getName());
        familyToPersonas.setBenchmarkCasePreferences(familyToPersonasBcp);

        launch(args);
    }

    private static EclipseTggProject createEclipseProject(String projectName, String[] referencedProjectNames)
            throws JsonException, IOException {

        Set<EclipseJavaProject> referencedProjects = new HashSet<EclipseJavaProject>();
        for (String referencedProjectName : referencedProjectNames) {
            Path projectPath = eclipseWorkspace.getLocation().resolve(referencedProjectName);
            Path outputPath = projectPath.resolve("bin");
            referencedProjects
                    .add(new EclipseJavaProject(referencedProjectName, projectPath, outputPath, new HashSet<>()));
        }

        Path projectPath = eclipseWorkspace.getLocation().resolve(projectName);
        Path outputPath = projectPath.resolve("bin");
        return new EclipseTggProject(projectName, projectPath, outputPath, referencedProjects);
    }
}
