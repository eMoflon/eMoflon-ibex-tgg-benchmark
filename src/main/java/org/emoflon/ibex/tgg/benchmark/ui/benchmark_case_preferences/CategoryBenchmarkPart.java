package org.emoflon.ibex.tgg.benchmark.ui.benchmark_case_preferences;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import org.controlsfx.validation.Validator;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.emoflon.ibex.tgg.benchmark.Core;
import org.emoflon.ibex.tgg.benchmark.model.BenchmarkCasePreferences;
import org.emoflon.ibex.tgg.benchmark.model.EclipseTggProject;
import org.emoflon.ibex.tgg.benchmark.runner.PatternMatchingEngine;
import org.emoflon.ibex.tgg.benchmark.ui.UIUtils;
import org.emoflon.ibex.tgg.benchmark.ui.components.TimeTextField;
import org.emoflon.ibex.tgg.benchmark.ui.generic_preferences.CategoryPart;
import org.emoflon.ibex.tgg.benchmark.utils.ReflectionUtils;
import org.emoflon.ibex.tgg.operational.strategies.OperationalStrategy;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

public class CategoryBenchmarkPart extends CategoryPart<BenchmarkCasePreferences> {

    // elements from the FXML resource
    @FXML
    private ChoiceBox<EclipseTggProject> eclipseProject;
    @FXML
    private TextField benchmarkCaseName;
    @FXML
    private ComboBox<Method> metamodelsRegistrationMethod;
    @FXML
    private ChoiceBox<PatternMatchingEngine> patternMatchingEngine;
    @FXML
    private TimeTextField defaultTimeout;

    private Tooltip eclipseProjectTooltip;
    private Tooltip benchmarkCaseNameTooltip;
    private Tooltip metamodelsRegistrationMethodTooltip;
    private Tooltip defaultTimeoutTooltip;

    public CategoryBenchmarkPart() throws IOException {
        super("../../resources/fxml/benchmark_case_preferences/CategoryBenchmark.fxml");
    }

    @Override
    public void initData(BenchmarkCasePreferences preferencesData) {
        super.initData(preferencesData);

        // tooltips
        eclipseProjectTooltip = new Tooltip("The TGG Project this case is associated with");
        benchmarkCaseNameTooltip = new Tooltip("An unique name for the benchmark case");
        metamodelsRegistrationMethodTooltip = new Tooltip(
                String.join("\n", "The helper method for registering meta models.",
                        "Only methods with the correct signature will be listed",
                        "Signature: method(ResourceSet rs, OperationalStrategy os)"));
        defaultTimeoutTooltip = new Tooltip(String.format(
                String.join("\n", "Default timeout for the operationalizations.",
                        "The time can be specified as follows: 30, 30s, 5m, 1h",
                        "When set to '0' the default value of '%s' will be used."),
                Core.getInstance().getPluginPreferences().getDefaultTimeout()));

        // bindings
        UIUtils.bindChoiceBox(eclipseProject, Core.getInstance().getWorkspace().getTggProjects(),
                preferencesData.eclipseProjectProperty());
        eclipseProject.setTooltip(eclipseProjectTooltip);
        eclipseProject.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            String oldCaseName = preferencesData.getBenchmarkCaseName();
            if (oldCaseName.isEmpty()) {
                preferencesData.setBenchmarkCaseName(newValue.getName());
            }
            if (oldValue != null) {
                preferencesData.setBenchmarkCaseName(oldCaseName.replaceFirst(oldValue.getName(), newValue.getName()));
            }

            if (newValue != null) {
                ObservableList<Method> items = metamodelsRegistrationMethod.getItems();
                items.setAll(getRegistrationMethods(newValue.getOutputPath()));
                if (!metamodelsRegistrationMethod.getItems().isEmpty()) {
                    metamodelsRegistrationMethod.getSelectionModel().selectFirst();
                }
            }
        });

        benchmarkCaseName.textProperty().bindBidirectional(preferencesData.benchmarkCaseNameProperty());
        benchmarkCaseName.setTooltip(benchmarkCaseNameTooltip);
        validation.registerValidator(benchmarkCaseName, true, Validator.createEmptyValidator("A name for benchmark case must be specified"));

        Set<Method> registrationMethods = new HashSet<>();
        if (preferencesData.getEclipseProject() != null) {
            registrationMethods = getRegistrationMethods(preferencesData.getEclipseProject().getOutputPath());
        }
        UIUtils.bindMethodComboBox(metamodelsRegistrationMethod, FXCollections.observableArrayList(registrationMethods),
                preferencesData.metamodelsRegistrationMethodProperty());
        if (metamodelsRegistrationMethod.getSelectionModel().getSelectedIndex() == -1
                && !metamodelsRegistrationMethod.getItems().isEmpty()) {
            metamodelsRegistrationMethod.getSelectionModel().selectFirst();
        }
        metamodelsRegistrationMethod.setTooltip(metamodelsRegistrationMethodTooltip);
        validation.registerValidator(metamodelsRegistrationMethod, true, Validator.createEmptyValidator("A metamodel registration method must be selected"));

        UIUtils.bindEnumChoiceBox(patternMatchingEngine,
                FXCollections.observableArrayList(PatternMatchingEngine.values()),
                preferencesData.patternMatchingEngineProperty());

        defaultTimeout.bindIntegerProperty(preferencesData.defaultTimeoutProperty());
        defaultTimeout.setTooltip(defaultTimeoutTooltip);
    }

    private Set<Method> getRegistrationMethods(Path classPath) {
        try (URLClassLoader classLoader = ReflectionUtils.createClassLoader(classPath)) {
            return ReflectionUtils.getMethodsWithMatchingParameters(classLoader, classPath, ResourceSet.class,
                    OperationalStrategy.class);
        } catch (Exception e) {
            LOG.error("Failed to fetch meta model registration helper methods from '{}'. Reason: {}",
                    classPath.toString(), e.getMessage());
        }
        return new HashSet<Method>();
    }
}
