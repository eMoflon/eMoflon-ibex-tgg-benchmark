package org.emoflon.tggbenchmark.gui.controller.benchmark_case_preferences;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

import org.controlsfx.validation.Validator;
import org.eclipse.emf.ecore.EObject;
import org.emoflon.tggbenchmark.gui.component.ModelSizesTextArea;
import org.emoflon.tggbenchmark.gui.component.TimeTextField;
import org.emoflon.tggbenchmark.gui.controller.generic_preferences.CategoryController;
import org.emoflon.tggbenchmark.gui.model.BenchmarkCase;
import org.emoflon.tggbenchmark.utils.ReflectionUtils;
import org.emoflon.tggbenchmark.utils.UIUtils;
import org.emoflon.tggbenchmark.workspace.EclipseJavaProject;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;

public class CategoryOperationalizationsController extends CategoryController<BenchmarkCase> {

    private ObservableList<ModelSizeValueWrapper> maxModelSizeChoiceList;

    // elements from the FXML resource
    @FXML
    private CheckBox modelgenIncludeReport;
    @FXML
    private TimeTextField modelgenTimeout;
    @FXML
    private ModelSizesTextArea modelgenModelSizes;

    @FXML
    private CheckBox initialFwdActive;
    @FXML
    private TimeTextField initialFwdTimeout;
    @FXML
    private ComboBox<ModelSizeValueWrapper> initialFwdMaxModelSize;

    @FXML
    private CheckBox initialBwdActive;
    @FXML
    private TimeTextField initialBwdTimeout;
    @FXML
    private ComboBox<ModelSizeValueWrapper> initialBwdMaxModelSize;

    @FXML
    private CheckBox fwdActive;
    @FXML
    private TimeTextField fwdTimeout;
    @FXML
    private ComboBox<ModelSizeValueWrapper> fwdMaxModelSize;
    @FXML
    private ComboBox<Method> fwdIncrementalEditMethod;

    @FXML
    private CheckBox bwdActive;
    @FXML
    private TimeTextField bwdTimeout;
    @FXML
    private ComboBox<ModelSizeValueWrapper> bwdMaxModelSize;
    @FXML
    private ComboBox<Method> bwdIncrementalEditMethod;

    @FXML
    private CheckBox fwdOptActive;
    @FXML
    private TimeTextField fwdOptTimeout;
    @FXML
    private ComboBox<ModelSizeValueWrapper> fwdOptMaxModelSize;

    @FXML
    private CheckBox bwdOptActive;
    @FXML
    private TimeTextField bwdOptTimeout;
    @FXML
    private ComboBox<ModelSizeValueWrapper> bwdOptMaxModelSize;

    @FXML
    private CheckBox ccActive;
    @FXML
    private TimeTextField ccTimeout;
    @FXML
    private ComboBox<ModelSizeValueWrapper> ccMaxModelSize;

    @FXML
    private CheckBox coActive;
    @FXML
    private TimeTextField coTimeout;
    @FXML
    private ComboBox<ModelSizeValueWrapper> coMaxModelSize;

    private Tooltip timeoutTooltip;
    private Tooltip modelSizesTooltip;
    private Tooltip operationalizationActiveTooltip;
    private Tooltip incrementalEditMethodTooltip;
    private Tooltip maxModelSizeTooltip;

    /**
     * InnerCategoryOperationalizationsPart
     */
    static class ModelSizeValueWrapper {

        private final Integer value;

        public ModelSizeValueWrapper(Integer value) {
            this.value = value;
        }

        @Override
        public String toString() {
            if (value >= 0) {
                return value.toString();
            }
            return "no limit";
        }

        /**
         * @return the value
         */
        public Integer getValue() {
            return value;
        }
    }

    public CategoryOperationalizationsController() throws IOException {
        super("../../../resources/view/benchmark_case_preferences/CategoryOperationalizations.fxml");

        maxModelSizeChoiceList = FXCollections.observableArrayList(new ModelSizeValueWrapper(-1));
    }

    @Override
    public void initData(BenchmarkCase bc) {
        super.initData(bc);

        // tooltips
        timeoutTooltip = new Tooltip() {
            {
                textProperty().bind(Bindings.concat("When set to '0' the timeout will default to '",
                        bc.defaultTimeoutProperty(), "'.\nThe following formats are allowed: 30, 30s, 5m, 1h"));
            }
        };
        modelSizesTooltip = new Tooltip("Integer values are seperated with a space");
        operationalizationActiveTooltip = new Tooltip("Benchmark operationalization\nand include in report.");
        incrementalEditMethodTooltip = new Tooltip(String.join("\n", "The method for incremental editing.",
                "Only static methods with the correct signature will be listed",
                "Signature: static method(EObject o)"));
        timeoutTooltip = new Tooltip(String.join("\n", "Timeout for the operationalization.",
                "The time can be specified as follows: 30, 30s, 5m, 1h",
                "When set to '0' the default value will be used."));

        // MODELGEN
        bindCheckbox(modelgenIncludeReport, bc.modelgenIncludeReportProperty());
        modelgenIncludeReport.setTooltip(operationalizationActiveTooltip);

        bindTimeTextField(modelgenTimeout, bc.modelgenTimeoutProperty());
        modelgenTimeout.setTooltip(timeoutTooltip);

        ChangeListener<ObservableList<Integer>> modelgenModelSizesChangeListener = (observable, oldValue, newValue) -> {
            maxModelSizeChoiceList.clear();
            maxModelSizeChoiceList.add(new ModelSizeValueWrapper(-1));
            bc.getModelgenModelSizes().forEach((e) -> maxModelSizeChoiceList.add(new ModelSizeValueWrapper(e)));

            initialFwdMaxModelSize.getSelectionModel().selectFirst();
            initialBwdMaxModelSize.getSelectionModel().selectFirst();
            fwdMaxModelSize.getSelectionModel().selectFirst();
            bwdMaxModelSize.getSelectionModel().selectFirst();
            fwdOptMaxModelSize.getSelectionModel().selectFirst();
            bwdOptMaxModelSize.getSelectionModel().selectFirst();
            ccMaxModelSize.getSelectionModel().selectFirst();
            coMaxModelSize.getSelectionModel().selectFirst();
        };
        modelgenModelSizes.bindListProperty(bc.modelgenModelSizesProperty());
        modelgenModelSizes.setTooltip(modelSizesTooltip);
        modelgenModelSizesChangeListener.changed(null, null, null);
        bc.modelgenModelSizesProperty().addListener(modelgenModelSizesChangeListener);
        validation.registerValidator(modelgenModelSizes,
                Validator.createEmptyValidator("At least one model size need to be specified"));
        // TODO: change to RuleCount
        // modelgenTggRule.textProperty().bindBidirectional(bc.modelgenTggRuleProperty());
        // validation.registerValidator(modelgenTggRule,
        // Validator.createEmptyValidator("A TGG rule for model generation must be
        // specified"));

        // INITIAL FWD
        bindCheckbox(initialFwdActive, bc.initialFwdActiveProperty());
        initialFwdActive.setTooltip(operationalizationActiveTooltip);
        bindTimeTextField(initialFwdTimeout, bc.initialFwdTimeoutProperty());
        initialFwdTimeout.setTooltip(timeoutTooltip);
        bindMaxModelSizeComboBox(initialFwdMaxModelSize, maxModelSizeChoiceList, bc.initialFwdMaxModelSizeProperty());

        // INITIAL BWD
        bindCheckbox(initialBwdActive, bc.initialBwdActiveProperty());
        initialBwdActive.setTooltip(operationalizationActiveTooltip);
        bindTimeTextField(initialBwdTimeout, bc.initialBwdTimeoutProperty());
        initialBwdTimeout.setTooltip(timeoutTooltip);
        bindMaxModelSizeComboBox(initialBwdMaxModelSize, maxModelSizeChoiceList, bc.initialBwdMaxModelSizeProperty());

        // FWD
        bindCheckbox(fwdActive, bc.fwdActiveProperty());
        fwdActive.setTooltip(operationalizationActiveTooltip);
        bindTimeTextField(fwdTimeout, bc.fwdTimeoutProperty());
        fwdTimeout.setTooltip(timeoutTooltip);
        bindMaxModelSizeComboBox(fwdMaxModelSize, maxModelSizeChoiceList, bc.fwdMaxModelSizeProperty());
        Set<Method> incrementalEditMethods = new HashSet<>();
        if (preferencesData.getEclipseProject() != null) {
            incrementalEditMethods = getIncrementalEditMethods(preferencesData.getEclipseProject());
        }
        UIUtils.bindMethodComboBox(fwdIncrementalEditMethod, FXCollections.observableArrayList(incrementalEditMethods),
                preferencesData.fwdIncrementalEditMethodProperty());
        fwdIncrementalEditMethod.setTooltip(incrementalEditMethodTooltip);

        // BWD
        bindCheckbox(bwdActive, bc.bwdActiveProperty());
        bwdActive.setTooltip(operationalizationActiveTooltip);
        bindTimeTextField(bwdTimeout, bc.bwdTimeoutProperty());
        bwdTimeout.setTooltip(timeoutTooltip);
        bindMaxModelSizeComboBox(bwdMaxModelSize, maxModelSizeChoiceList, bc.bwdMaxModelSizeProperty());
        UIUtils.bindMethodComboBox(bwdIncrementalEditMethod, FXCollections.observableArrayList(incrementalEditMethods),
                preferencesData.bwdIncrementalEditMethodProperty());
        bwdIncrementalEditMethod.setTooltip(incrementalEditMethodTooltip);

        // FWD OPT
        bindCheckbox(fwdOptActive, bc.fwdOptActiveProperty());
        fwdOptActive.setTooltip(operationalizationActiveTooltip);
        bindTimeTextField(fwdOptTimeout, bc.fwdOptTimeoutProperty());
        fwdOptTimeout.setTooltip(timeoutTooltip);
        bindMaxModelSizeComboBox(fwdOptMaxModelSize, maxModelSizeChoiceList, bc.fwdOptMaxModelSizeProperty());

        // BWD OPT
        bindCheckbox(bwdOptActive, bc.bwdOptActiveProperty());
        bwdOptActive.setTooltip(operationalizationActiveTooltip);
        bindTimeTextField(bwdOptTimeout, bc.bwdOptTimeoutProperty());
        bwdOptTimeout.setTooltip(timeoutTooltip);
        bindMaxModelSizeComboBox(bwdOptMaxModelSize, maxModelSizeChoiceList, bc.bwdOptMaxModelSizeProperty());

        // CC
        bindCheckbox(ccActive, bc.ccActiveProperty());
        ccActive.setTooltip(operationalizationActiveTooltip);
        bindTimeTextField(ccTimeout, bc.ccTimeoutProperty());
        ccTimeout.setTooltip(timeoutTooltip);
        bindMaxModelSizeComboBox(ccMaxModelSize, maxModelSizeChoiceList, bc.ccMaxModelSizeProperty());

        // CO
        bindCheckbox(coActive, bc.coActiveProperty());
        coActive.setTooltip(operationalizationActiveTooltip);
        bindTimeTextField(coTimeout, bc.coTimeoutProperty());
        coTimeout.setTooltip(timeoutTooltip);
        bindMaxModelSizeComboBox(coMaxModelSize, maxModelSizeChoiceList, bc.coMaxModelSizeProperty());

        // trigger when changing the associated project
        bc.eclipseProjectProperty().addListener(e -> {
            if (preferencesData.getEclipseProject() != null) {
                Set<Method> items = getIncrementalEditMethods(preferencesData.getEclipseProject());

                ObservableList<Method> fwdItems = fwdIncrementalEditMethod.getItems();
                ObservableList<Method> bwdItems = bwdIncrementalEditMethod.getItems();
                fwdItems.setAll(items);
                bwdItems.setAll(items);
            }
        });
    }

    private void bindMaxModelSizeComboBox(ComboBox<ModelSizeValueWrapper> comboBox,
            ObservableList<ModelSizeValueWrapper> items, IntegerProperty property) {
        comboBox.setItems(items);
        // set the items to the chbx
        comboBox.setItems(items);
        // select the item that matches the property value
        for (ModelSizeValueWrapper item : items) {
            if (item.getValue().equals(property.getValue())) {
                comboBox.getSelectionModel().select(item);
                break;
            }
        }
        if (comboBox.getSelectionModel().getSelectedIndex() == -1) {
            comboBox.getSelectionModel().selectFirst();
            property.setValue(-1);
        }
        // update property when an item has been selected
        comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                property.setValue(newValue.getValue());
            } else {
                property.setValue(-1);
            }
        });
    }

    private void bindChoiceBox(ChoiceBox<String> chbx, ObservableList<String> items, IntegerProperty ip) {
        int selectIndex = ip.get() + 1;

        chbx.setItems(items);
        chbx.getSelectionModel().selectedIndexProperty().addListener((observable, old_value, new_value) -> {
            // offset index so that 'no limit' has '-1'
            ip.set(new_value.intValue() - 1);
        });
        if (selectIndex < 0) {
            chbx.getSelectionModel().selectFirst();
        } else if (selectIndex < items.size()) {
            chbx.getSelectionModel().select(selectIndex);
        } else {
            chbx.getSelectionModel().selectLast();
        }
    }

    private void bindCheckbox(CheckBox chkbx, BooleanProperty bp) {
        chkbx.selectedProperty().bindBidirectional(bp);
    }

    private void bindTimeTextField(TimeTextField field, IntegerProperty property) {
        field.bindIntegerProperty(property);
    }

    private Set<Method> getIncrementalEditMethods(EclipseJavaProject javaProject) {
        try (URLClassLoader classLoader = ReflectionUtils.createClassLoader(javaProject)) {
            return ReflectionUtils.getMethodsWithMatchingParameters(classLoader, javaProject.getOutputPath(),
                    EObject.class);
        } catch (Exception e) {
            LOG.error("Failed to fetch incremental edit methods from '{}'. Reason: {}", javaProject.toString(),
                    e.getMessage());
        }
        return new HashSet<Method>();
    }
}
