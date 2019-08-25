package org.emoflon.ibex.tgg.benchmark.ui.benchmark_case_preferences;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Set;

import org.eclipse.emf.ecore.EPackage;
import org.emoflon.ibex.tgg.benchmark.Core;
import org.emoflon.ibex.tgg.benchmark.model.BenchmarkCasePreferences;
import org.emoflon.ibex.tgg.benchmark.model.EclipseTggProject;
import org.emoflon.ibex.tgg.benchmark.ui.UIUtils;
import org.emoflon.ibex.tgg.benchmark.ui.components.ModelSizesTextArea;
import org.emoflon.ibex.tgg.benchmark.ui.components.TimeTextField;
import org.emoflon.ibex.tgg.benchmark.ui.generic_preferences.CategoryPart;
import org.emoflon.ibex.tgg.benchmark.utils.ReflectionUtils;

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
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

public class CategoryOperationalizationsPart extends CategoryPart<BenchmarkCasePreferences> {

    private ObservableList<Integer> modelSizes;
    private ObservableList<ModelSizeValueWrapper> maxModelSizeChoiceList;

    // elements from the FXML resource
    @FXML
    private CheckBox modelgenIncludeReport;
    @FXML
    private TimeTextField modelgenTimeout;
    @FXML
    private ModelSizesTextArea modelgenModelSizes;
    @FXML
    private TextField modelgenTggRule;

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

    public CategoryOperationalizationsPart() throws IOException {
        super("../../resources/fxml/benchmark_case_preferences/CategoryOperationalizations.fxml");

        maxModelSizeChoiceList = FXCollections.observableArrayList(new ModelSizeValueWrapper(-1));
    }

    @Override
    public void initData(BenchmarkCasePreferences bcp) {
        super.initData(bcp);

        // tooltips
        timeoutTooltip = new Tooltip() {
            {
                textProperty().bind(Bindings.concat("When set to '0' the timeout will default to '",
                        bcp.defaultTimeoutProperty(), "'.\nThe following formats are allowed: 30, 30s, 5m, 1h"));
            }
        };
        modelSizesTooltip = new Tooltip("Integer values are seperated with a space");
        operationalizationActiveTooltip = new Tooltip("Benchmark operationalization\nand include in report.");
        incrementalEditMethodTooltip = new Tooltip(String.join("\n", "The method for incremental editing.",
                "Only methods with the correct signature will be listed", "Signature: method(EObject o)"));
        timeoutTooltip = new Tooltip(String.join("\n", "Timeout for the operationalization.",
                "The time can be specified as follows: 30, 30s, 5m, 1h",
                "When set to '0' the default value will be used."));

        // MODELGEN
        bindCheckbox(modelgenIncludeReport, bcp.modelgenIncludeReportProperty());

        bindTimeTextField(modelgenTimeout, bcp.modelgenTimeoutProperty());
        modelgenTimeout.setTooltip(timeoutTooltip);

        ChangeListener<ObservableList<Integer>> modelgenModelSizesChangeListener = (observable, oldValue, newValue) -> {
            maxModelSizeChoiceList.clear();
            maxModelSizeChoiceList.add(new ModelSizeValueWrapper(-1));
            bcp.getModelgenModelSizes().forEach((e) -> maxModelSizeChoiceList.add(new ModelSizeValueWrapper(e)));

            initialFwdMaxModelSize.getSelectionModel().selectFirst();
            initialBwdMaxModelSize.getSelectionModel().selectFirst();
            fwdMaxModelSize.getSelectionModel().selectFirst();
            bwdMaxModelSize.getSelectionModel().selectFirst();
            fwdOptMaxModelSize.getSelectionModel().selectFirst();
            bwdOptMaxModelSize.getSelectionModel().selectFirst();
            ccMaxModelSize.getSelectionModel().selectFirst();
            coMaxModelSize.getSelectionModel().selectFirst();
        };
        modelgenModelSizes.bindListProperty(bcp.modelgenModelSizesProperty());
        modelgenModelSizesChangeListener.changed(null, null, null);
        bcp.modelgenModelSizesProperty().addListener(modelgenModelSizesChangeListener);

        // INITIAL FWD
        bindCheckbox(initialFwdActive, bcp.initialFwdActiveProperty());
        bindTimeTextField(initialFwdTimeout, bcp.initialFwdTimeoutProperty());
        initialFwdTimeout.setTooltip(timeoutTooltip);
        bindMaxModelSizeComboBox(initialFwdMaxModelSize, maxModelSizeChoiceList, bcp.initialFwdMaxModelSizeProperty());

        // INITIAL BWD
        bindCheckbox(initialBwdActive, bcp.initialBwdActiveProperty());
        bindTimeTextField(initialBwdTimeout, bcp.initialBwdTimeoutProperty());
        initialBwdTimeout.setTooltip(timeoutTooltip);
        bindMaxModelSizeComboBox(initialBwdMaxModelSize, maxModelSizeChoiceList, bcp.initialBwdMaxModelSizeProperty());

        // FWD
        bindCheckbox(fwdActive, bcp.fwdActiveProperty());
        bindTimeTextField(fwdTimeout, bcp.fwdTimeoutProperty());
        fwdTimeout.setTooltip(timeoutTooltip);
        bindMaxModelSizeComboBox(fwdMaxModelSize, maxModelSizeChoiceList, bcp.fwdMaxModelSizeProperty());
        UIUtils.bindMethodComboBox(fwdIncrementalEditMethod, FXCollections.observableArrayList(),
                preferencesData.metamodelsRegistrationMethodProperty());

        // BWD
        bindCheckbox(bwdActive, bcp.bwdActiveProperty());
        bindTimeTextField(bwdTimeout, bcp.bwdTimeoutProperty());
        bwdTimeout.setTooltip(timeoutTooltip);
        bindMaxModelSizeComboBox(bwdMaxModelSize, maxModelSizeChoiceList, bcp.bwdMaxModelSizeProperty());

        // FWD OPT
        bindCheckbox(fwdOptActive, bcp.fwdOptActiveProperty());
        bindTimeTextField(fwdOptTimeout, bcp.fwdOptTimeoutProperty());
        fwdOptTimeout.setTooltip(timeoutTooltip);
        bindMaxModelSizeComboBox(fwdOptMaxModelSize, maxModelSizeChoiceList, bcp.fwdOptMaxModelSizeProperty());

        // BWD OPT
        bindCheckbox(bwdOptActive, bcp.bwdOptActiveProperty());
        bindTimeTextField(bwdOptTimeout, bcp.bwdOptTimeoutProperty());
        bwdOptTimeout.setTooltip(timeoutTooltip);
        bindMaxModelSizeComboBox(bwdOptMaxModelSize, maxModelSizeChoiceList, bcp.bwdOptMaxModelSizeProperty());

        // CC
        bindCheckbox(ccActive, bcp.ccActiveProperty());
        bindTimeTextField(ccTimeout, bcp.ccTimeoutProperty());
        ccTimeout.setTooltip(timeoutTooltip);
        bindMaxModelSizeComboBox(ccMaxModelSize, maxModelSizeChoiceList, bcp.ccMaxModelSizeProperty());

        // CO
        bindCheckbox(coActive, bcp.coActiveProperty());
        bindTimeTextField(coTimeout, bcp.coTimeoutProperty());
        coTimeout.setTooltip(timeoutTooltip);
        bindMaxModelSizeComboBox(coMaxModelSize, maxModelSizeChoiceList, bcp.coMaxModelSizeProperty());

        updateIncrementalEditMethodComboBoxes();

        // trigger when changing the associated project
        bcp.eclipseProjectProperty().addListener(e -> {
            updateIncrementalEditMethodComboBoxes();
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

    private void updateIncrementalEditMethodComboBoxes() {
        if (preferencesData.getEclipseProject() == null) {
            return;
        }

        ObservableList<Method> fwdItems = fwdIncrementalEditMethod.getItems();
        ObservableList<Method> bwdItems = bwdIncrementalEditMethod.getItems();
        fwdItems.clear();
        bwdItems.clear();

        Path classPath = preferencesData.getEclipseProject().getOutputPath();
        try (URLClassLoader classLoader = ReflectionUtils.createClassLoader(classPath)) {
            Set<Method> methods = ReflectionUtils.getMethodsWithMatchingParameters(classLoader, classPath, EPackage.class);
            fwdItems.setAll(methods);
            bwdItems.setAll(methods);
        } catch (Exception e) {
            LOG.debug("Failed to fetch meta model registration helper methods from '{}'. Reason: {}", classPath.toString(), e.getMessage());
        }
    }
}
