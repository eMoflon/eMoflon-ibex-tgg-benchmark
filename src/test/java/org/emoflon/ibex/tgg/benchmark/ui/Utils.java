package org.emoflon.ibex.tgg.benchmark.ui;

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

import javafx.collections.FXCollections;

/**
 * Utils
 */
public abstract class Utils {

    public static void initConfiguration() throws JsonException, IOException {
        // configure logger
        Configurator.initialize(new DefaultConfiguration());
        Configurator.setRootLevel(Level.ALL);

        Core pluginCore = Core.getInstance();
        EclipseWorkspaceDebug eclipseWorkspace = new EclipseWorkspaceDebug();
        pluginCore.setWorkspace(eclipseWorkspace);
        PluginPreferences pluginPreferences = new PluginPreferences();
        pluginCore.setPluginPreferences(pluginPreferences);

        EclipseTggProject companyToIt = createEclipseProject("CompanyToIT",
                new String[] { "CompanyLanguage", "ITLanguage", "CompanyToIT", });
        BenchmarkCasePreferences companyToItBcp = new BenchmarkCasePreferences();
        companyToItBcp.setBenchmarkCaseName(companyToIt.getName());
        companyToItBcp.setEclipseProject(companyToIt);
        companyToItBcp.setMetamodelsRegistrationMethod(
                "org.emoflon.ibex.tgg.run.companytoit._RegistrationHelper#registerMetamodels");
        companyToItBcp.setModelgenTggRule("CompanyToITRule");
        companyToIt.setBenchmarkCasePreferences(FXCollections.observableArrayList(companyToItBcp));

        EclipseTggProject familyToPersonas = createEclipseProject("FamiliesToPersons_V1",
                new String[] { "SimpleFamilies", "SimplePersons", "FamiliesToPersons_V1" });
        eclipseWorkspace.setTggProjects(FXCollections.observableArrayList(companyToIt, familyToPersonas));
        BenchmarkCasePreferences familyToPersonasBcp = new BenchmarkCasePreferences();
        familyToPersonasBcp.setEclipseProject(familyToPersonas);
        familyToPersonasBcp.setBenchmarkCaseName(familyToPersonas.getName());
        familyToPersonasBcp.setMetamodelsRegistrationMethod(
                "org.emoflon.ibex.tgg.run.familiestopersons_v1._RegistrationHelper#registerMetamodels");
        familyToPersonasBcp.setModelgenTggRule("HandleRegisters");
        familyToPersonas.setBenchmarkCasePreferences(FXCollections.observableArrayList(familyToPersonasBcp));
    }

    private static EclipseTggProject createEclipseProject(String projectName, String[] referencedProjectNames)
            throws JsonException, IOException {

        Set<EclipseJavaProject> referencedProjects = new HashSet<EclipseJavaProject>();
        for (String referencedProjectName : referencedProjectNames) {
            Path projectPath = Core.getInstance().getWorkspace().getLocation().resolve(referencedProjectName);
            Path outputPath = projectPath.resolve("bin");
            referencedProjects
                    .add(new EclipseJavaProject(referencedProjectName, projectPath, outputPath, new HashSet<>()));
        }

        Path projectPath = Core.getInstance().getWorkspace().getLocation().resolve(projectName);
        Path outputPath = projectPath.resolve("bin");
        return new EclipseTggProject(projectName, projectPath, outputPath, referencedProjects);
    }

}