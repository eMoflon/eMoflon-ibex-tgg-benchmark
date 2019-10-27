package org.emoflon.ibex.tgg.benchmark.ui;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import javax.json.JsonException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.emoflon.ibex.tgg.benchmark.Core;
import org.emoflon.ibex.tgg.benchmark.model.BenchmarkCase;
import org.emoflon.ibex.tgg.benchmark.model.EclipseJavaProject;
import org.emoflon.ibex.tgg.benchmark.model.EclipseTggProject;
import org.emoflon.ibex.tgg.benchmark.model.EclipseWorkspaceDebug;
import org.emoflon.ibex.tgg.benchmark.model.PluginPreferences;

import javafx.collections.FXCollections;
import javafx.util.Pair;

/**
 * Utils
 */
public abstract class Utils {
    
    static Set<URL> CLASS_PATHS;
    
    static {
        String[] classPaths = System.getProperty("java.class.path").split(":");
        CLASS_PATHS = new HashSet<>();
        for (String path : classPaths) {
            try {
                CLASS_PATHS.add(new URL("file://" + path));
            } catch (MalformedURLException e) {
            }
        }
    }

    public static void initConfiguration() throws JsonException, IOException {
        // configure logger
        Configurator.initialize(new DefaultConfiguration());
        Configurator.setRootLevel(Level.ALL);

        Core pluginCore = Core.getInstance();
        EclipseWorkspaceDebug eclipseWorkspace = new EclipseWorkspaceDebug();
        pluginCore.setWorkspace(eclipseWorkspace);
        PluginPreferences pluginPreferences = new PluginPreferences();
        pluginPreferences.setDefaultBwdOptActive(false);
        pluginPreferences.setDefaultFwdOptActive(false);
        pluginCore.setPluginPreferences(pluginPreferences);

        EclipseTggProject companyToIt = createEclipseProject("CompanyToIT",
                new String[] { "CompanyLanguage", "ITLanguage", "CompanyToIT", });
        BenchmarkCase companyToItBcp = new BenchmarkCase();
        companyToItBcp.setBenchmarkCaseName(companyToIt.getName());
        companyToItBcp.setEclipseProject(companyToIt);
        companyToItBcp.setMetamodelsRegistrationMethod(
                "org.emoflon.ibex.tgg.run.companytoit._RegistrationHelper#registerMetamodels");
        companyToItBcp.setModelgenRuleCount(FXCollections.observableArrayList(new Pair<String, Integer>("CompanyToITRule", 1)));
        companyToItBcp.setFwdIncrementalEditMethod("org.emoflon.ibex.tgg.run.companytoit.IncrementalEdit#fwd");
        companyToItBcp.setBwdIncrementalEditMethod("org.emoflon.ibex.tgg.run.companytoit.IncrementalEdit#bwd");
        companyToIt.setBenchmarkCasePreferences(FXCollections.observableArrayList(companyToItBcp));

        EclipseTggProject familyToPersonas = createEclipseProject("FamiliesToPersons_V1",
                new String[] { "SimpleFamilies", "SimplePersons", "FamiliesToPersons_V1" });
        eclipseWorkspace.setTggProjects(FXCollections.observableArrayList(companyToIt, familyToPersonas));
        BenchmarkCase familyToPersonasBcp = new BenchmarkCase();
        familyToPersonasBcp.setEclipseProject(familyToPersonas);
        familyToPersonasBcp.setBenchmarkCaseName(familyToPersonas.getName());
        familyToPersonasBcp.setMetamodelsRegistrationMethod(
                "org.emoflon.ibex.tgg.run.familiestopersons_v1._RegistrationHelper#registerMetamodels");
        familyToPersonasBcp.setModelgenRuleCount(FXCollections.observableArrayList(new Pair<String, Integer>("HandleRegisters", 1)));
        familyToPersonas.setBenchmarkCasePreferences(FXCollections.observableArrayList(familyToPersonasBcp));
    }

    private static EclipseTggProject createEclipseProject(String projectName, String[] referencedProjectNames)
            throws JsonException, IOException {

        Set<EclipseJavaProject> referencedProjects = new HashSet<EclipseJavaProject>();
        for (String referencedProjectName : referencedProjectNames) {
            Path projectPath = Core.getInstance().getWorkspace().getLocation().resolve(referencedProjectName);
            Path outputPath = projectPath.resolve("bin");
            Set<URL> classPaths = new HashSet<URL>();
            classPaths.add(outputPath.toUri().toURL());
            referencedProjects
                    .add(new EclipseJavaProject(referencedProjectName, projectPath, outputPath, classPaths, new HashSet<>()));
        }

        Path projectPath = Core.getInstance().getWorkspace().getLocation().resolve(projectName);
        Path outputPath = projectPath.resolve("bin");
        Set<URL> classPaths = new HashSet<URL>();
        classPaths.addAll(CLASS_PATHS);
        classPaths.add(outputPath.toUri().toURL());
        return new EclipseTggProject(projectName, projectPath, outputPath, classPaths, referencedProjects);
    }

}