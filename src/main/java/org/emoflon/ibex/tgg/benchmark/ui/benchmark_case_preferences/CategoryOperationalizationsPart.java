package org.emoflon.ibex.tgg.benchmark.ui.benchmark_case_preferences;

import java.io.IOException;

import org.emoflon.ibex.tgg.benchmark.model.BenchmarkCasePreferences;
import org.emoflon.ibex.tgg.benchmark.ui.components.IntegerTextFieldListCell;
import org.emoflon.ibex.tgg.benchmark.ui.components.TimeTextField;
import org.emoflon.ibex.tgg.benchmark.ui.generic_preferences.CategoryPart;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.converter.IntegerStringConverter;

public class CategoryOperationalizationsPart extends CategoryPart<BenchmarkCasePreferences> {

    private ObservableList<Integer> modelSizes;
    private ObservableList<String> maxModelSizeChoiceList;

    // elements from the FXML resource
    @FXML
    private CheckBox modelgenCreateReport;
    @FXML
    private TimeTextField modelgenTimeout;
    @FXML
    private ListView<Integer> modelgenModelSizes;
    @FXML
    private TextField modelgenTggRule;
    @FXML
    private CheckBox initialFwdActive;
    @FXML
    private TimeTextField initialFwdTimeout;
    @FXML
    private ChoiceBox<String> initialFwdMaxModelSize;
    @FXML
    private CheckBox initialBwdActive;
    @FXML
    private TimeTextField initialBwdTimeout;
    @FXML
    private ChoiceBox<String> initialBwdMaxModelSize;
    @FXML
    private CheckBox fwdOptActive;
    @FXML
    private TimeTextField fwdOptTimeout;
    @FXML
    private ChoiceBox<String> fwdOptMaxModelSize;
    @FXML
    private CheckBox bwdOptActive;
    @FXML
    private TimeTextField bwdOptTimeout;
    @FXML
    private ChoiceBox<String> bwdOptMaxModelSize;
    @FXML
    private CheckBox syncActive;
    @FXML
    private TimeTextField syncTimeout;
    @FXML
    private ChoiceBox<String> syncMaxModelSize;
    @FXML
    private ChoiceBox<?> syncDirection;
    @FXML
    private TextField syncConsumer;
    @FXML
    private CheckBox ccActive;
    @FXML
    private TimeTextField ccTimeout;
    @FXML
    private ChoiceBox<String> ccMaxModelSize;
    @FXML
    private CheckBox coActive;
    @FXML
    private TimeTextField coTimeout;
    @FXML
    private ChoiceBox<String> coMaxModelSize;

    private Tooltip timeoutTooltip;

    public CategoryOperationalizationsPart() throws IOException {
        super("../../resources/fxml/benchmark_case_preferences/CategoryOperationalizations.fxml");

        modelSizes = FXCollections.observableArrayList(1000, 2000, 4000, 8000);
        maxModelSizeChoiceList = FXCollections.observableArrayList("no limit");
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

        // MODELGEN
        bindCheckbox(modelgenCreateReport, bcp.modelgenCreateReportProperty());
        bindTimeTextField(modelgenTimeout, bcp.modelgenTimeoutProperty());
        modelgenTimeout.setTooltip(timeoutTooltip);

        modelgenModelSizes.setItems(bcp.getModelgenModelSizes());

        bcp.getModelgenModelSizes().forEach((e) -> maxModelSizeChoiceList.add(e.toString()));

        // ObservableList<String> items = FXCollections.observableArrayList("test1",
        // "test2", "test3", "test4");
        // ObservableList<String> items = FXCollections.observableArrayList("test1",
        // "test2");
        // modelgenModelSizes.setItems(items);
        // modelgenModelSizes.setEditable(true);
        // modelgenModelSizes.setCellFactory(TextFieldListCell.forListView());
        // modelgenModelSizes.setCellFactory(lv -> new TextFieldListCell<Integer>() {
        // {
        // setPrefWidth(0.0);
        // setConverter(new IntegerStringConverter());
        // }
        // });
        // modelgenModelSizes.setCellFactory(lv -> {
        // TextFieldListCell<Integer> cell = new TextFieldListCell<Integer>();
        // cell.setConverter(new IntegerStringConverter());
        // cell.setPrefWidth(0);
        // return cell;
        // });

        // listViewIntegers.setItems(dataModel);

        modelgenModelSizes.setEditable(true);

        Runnable editCurrentSelectedCell = new Runnable() {
            @Override
            public void run() {
                modelgenModelSizes.edit(modelgenModelSizes.getSelectionModel().getSelectedIndex());
            }
        };

        modelgenModelSizes.setCellFactory(lv -> new IntegerTextFieldListCell());

        modelgenModelSizes.setOnEditCommit(event -> {
            if (event.getNewValue() == null || event.getNewValue() <= 0) {
                modelgenModelSizes.getItems().remove(event.getIndex());
            } else {
                modelgenModelSizes.getItems().set(event.getIndex(), event.getNewValue());
                FXCollections.sort(modelgenModelSizes.getItems());
            }
            maxModelSizeChoiceList.clear();
            maxModelSizeChoiceList.add("no limit");
            modelgenModelSizes.getItems().forEach((e) -> maxModelSizeChoiceList.add(e.toString()));
        });
        modelgenModelSizes.setOnEditCancel(event -> {
            ObservableList<Integer> items = modelgenModelSizes.getItems();
            if (event.getIndex() >= 0 && event.getIndex() < items.size()) {
                Integer cellValue = modelgenModelSizes.getItems().get(event.getIndex());
                if (cellValue == null || cellValue <= 0) {
                    modelgenModelSizes.getItems().remove(event.getIndex());
                }
            }
        });
        // modelgenModelSizes.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
        // if (event.getCode() == KeyCode.ENTER) {
        // int new_index = modelgenModelSizes.getSelectionModel().getSelectedIndex() +
        // 1;
        // if (new_index >= modelgenModelSizes.getItems().size()){
        // modelgenModelSizes.getItems().add(null);
        // modelgenModelSizes.scrollTo(new_index);
        // }
        // modelgenModelSizes.getSelectionModel().select(new_index);
        // event.consume();
        // }
        // });

        modelgenModelSizes.getSelectionModel().selectedIndexProperty()
                .addListener((observable, old_value, new_value) -> {
                    // make sure the cell really switches into edit mode
                    modelgenModelSizes.layout();
                    Platform.runLater(editCurrentSelectedCell);
                });

        // new TextFieldListCell(new DefaultStringConverter());
        // new TextFieldListCell(new NumberStringConverter());
        //
        // Callback<ListView<String>, ListCell<String>> d =
        // TextFieldListCell.forListView();
        // TextFieldListCell f = new TextFieldListCell();
        // f.
        // select all
        // modelgenModelSizes.setOnEditStart(null);

        // modelgenModelSizes.setOnMouseClicked(e -> {
        // // if (e.getTarget() instanceof TextFieldListCell<?>) {
        // // if (((TextFieldListCell<?>) e.getTarget()).getText() == null) {
        // // ObservableList<Integer> dataModel = modelgenModelSizes.getItems();
        // // Integer n = 1;
        // // dataModel.add(n);
        // // modelgenModelSizes.layout();
        // // modelgenModelSizes.scrollTo(n);
        // // modelgenModelSizes.edit(dataModel.size() - 1);
        // // }
        // // } else {
        // // ObservableList<Integer> dataModel = modelgenModelSizes.getItems();
        // // Integer n = 1;
        // // dataModel.add(n);
        // // modelgenModelSizes.layout();
        // // modelgenModelSizes.scrollTo(n);
        // // modelgenModelSizes.edit(dataModel.size() - 1);
        // // }
        // System.out.println(e.getTarget());
        // System.out.println(e);
        // });

        // modelgenModelSizes.setOnKeyPressed(null);
        // modelgenModelSizes.setOnEditCommit(e -> {
        // System.out.println(e);
        // });

        maxModelSizeChoiceList.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable arg0) {
                changeSelection(initialFwdMaxModelSize);
                changeSelection(initialBwdMaxModelSize);
                changeSelection(fwdOptMaxModelSize);
                changeSelection(bwdOptMaxModelSize);
                changeSelection(syncMaxModelSize);
                changeSelection(ccMaxModelSize);
                changeSelection(coMaxModelSize);
            }

            private void changeSelection(ChoiceBox<String> cbx) {
                // cbx.getSelectionModel().selectFirst();
                // int selectionIndex = cbx.getSelectionModel().getSelectedIndex();
                // if (selectionIndex >= 0) {
                // System.out.println(cbx.getChildrenUnmodifiable().get(selectionIndex));
                // }
            }
        });

        // INITIAL FWD
        bindCheckbox(initialFwdActive, bcp.initialFwdActiveProperty());
        bindTimeTextField(initialFwdTimeout, bcp.initialFwdTimeoutProperty());
        initialFwdTimeout.setTooltip(timeoutTooltip);
        initialFwdMaxModelSize.setItems(maxModelSizeChoiceList);
        bindChoiceBox(initialFwdMaxModelSize, maxModelSizeChoiceList, bcp.initialFwdMaxModelSizeProperty());

        // INITIAL BWD
        bindCheckbox(initialBwdActive, bcp.initialBwdActiveProperty());
        bindTimeTextField(initialBwdTimeout, bcp.initialBwdTimeoutProperty());
        initialBwdTimeout.setTooltip(timeoutTooltip);
        bindChoiceBox(initialBwdMaxModelSize, maxModelSizeChoiceList, bcp.initialBwdMaxModelSizeProperty());

        // FWD OPT
        bindCheckbox(fwdOptActive, bcp.fwdOptActiveProperty());
        bindTimeTextField(fwdOptTimeout, bcp.fwdOptTimeoutProperty());
        fwdOptTimeout.setTooltip(timeoutTooltip);
        bindChoiceBox(fwdOptMaxModelSize, maxModelSizeChoiceList, bcp.fwdOptMaxModelSizeProperty());

        // BWD OPT
        bindCheckbox(bwdOptActive, bcp.bwdOptActiveProperty());
        bindTimeTextField(bwdOptTimeout, bcp.bwdOptTimeoutProperty());
        bwdOptTimeout.setTooltip(timeoutTooltip);
        bindChoiceBox(bwdOptMaxModelSize, maxModelSizeChoiceList, bcp.bwdOptMaxModelSizeProperty());

        // SYNC
        bindCheckbox(syncActive, bcp.syncActiveProperty());
        // TODO: syncdirection and consumer must be bound
        // syncDirection.setItems(bcp.syncDirectionProperty());
        // syncConsumer
        bindTimeTextField(syncTimeout, bcp.syncTimeoutProperty());
        syncTimeout.setTooltip(timeoutTooltip);
        bindChoiceBox(syncMaxModelSize, maxModelSizeChoiceList, bcp.syncMaxModelSizeProperty());

        // CC
        bindCheckbox(ccActive, bcp.ccActiveProperty());
        bindTimeTextField(ccTimeout, bcp.ccTimeoutProperty());
        ccTimeout.setTooltip(timeoutTooltip);
        bindChoiceBox(ccMaxModelSize, maxModelSizeChoiceList, bcp.ccMaxModelSizeProperty());

        // CO
        bindCheckbox(coActive, bcp.coActiveProperty());
        bindTimeTextField(coTimeout, bcp.coTimeoutProperty());
        coTimeout.setTooltip(timeoutTooltip);
        bindChoiceBox(coMaxModelSize, maxModelSizeChoiceList, bcp.coMaxModelSizeProperty());
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
}
