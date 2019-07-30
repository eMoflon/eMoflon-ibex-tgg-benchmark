package org.emoflon.ibex.tgg.benchmark.ui.eclipse_view.parts;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.emoflon.ibex.tgg.benchmark.Core;
import org.emoflon.ibex.tgg.benchmark.model.BenchmarkCasePreferences;
import org.emoflon.ibex.tgg.benchmark.ui.benchmark_case_preferences.BenchmarkCasePreferencesWindow;
import org.emoflon.ibex.tgg.benchmark.ui.components.Part;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

public class TGGBenchmarkViewPart extends Part {

    private ObservableList<BenchmarkCasePreferences> benchmarkCasePreferencesList;
    private BenchmarkCaseTableView table;

    private final Core pluginCore;

    public TGGBenchmarkViewPart() {
        // get the plugin core instance
        pluginCore = Core.getInstance();

        // create the content
        table = new BenchmarkCaseTableView();
        content = new AnchorPane(table);
        AnchorPane.setTopAnchor(table, 0.0);
        AnchorPane.setBottomAnchor(table, 0.0);
        AnchorPane.setLeftAnchor(table, 0.0);
        AnchorPane.setRightAnchor(table, 0.0);
    }

    public void initData(ObservableList<BenchmarkCasePreferences> bcpl) {
        this.benchmarkCasePreferencesList = bcpl;
        table.initData(benchmarkCasePreferencesList);
    }

    public class BenchmarkCaseTableView extends TableView<BenchmarkCasePreferences> {

        private TableColumn<BenchmarkCasePreferences, Boolean> executionColumn;
        private TableColumn<BenchmarkCasePreferences, String> nameColumn;
        private TableColumn<BenchmarkCasePreferences, Boolean> modelgenActiveColumn;
        private TableColumn<BenchmarkCasePreferences, Boolean> initialFwdActiveColumn;
        private TableColumn<BenchmarkCasePreferences, Boolean> initialBwdActiveColumn;
        private TableColumn<BenchmarkCasePreferences, Boolean> fwdOptActiveColumn;
        private TableColumn<BenchmarkCasePreferences, Boolean> bwdOptActiveColumn;
        private TableColumn<BenchmarkCasePreferences, Boolean> syncActiveColumn;
        private TableColumn<BenchmarkCasePreferences, Boolean> ccActiveColumn;
        private TableColumn<BenchmarkCasePreferences, Boolean> coActiveColumn;

        class SelectAllCheckBox<S> extends CheckBox {
            private Runnable selectAllStateChangeProcessor;
            private Function<S, BooleanProperty> property;
            private ObservableList<S> observableList;

            public SelectAllCheckBox(ObservableList<S> observableList, Function<S, BooleanProperty> property) {
                super();
                
                this.property = property;
                this.observableList = observableList;

                for (S item : observableList) {
                    property.apply(item).addListener((observable, wasSelected, isSelected) -> {
                        updateSelectedState();
                    });
                }
                observableList.addListener((Change<? extends S> c) -> {
                    while (c.next()) {
                        if (c.wasAdded()) {
                            for (S item : c.getAddedSubList()) {
                                property.apply(item).addListener((observable, wasSelected, isSelected) -> {
                                    updateSelectedState();
                                });
                            }
                        }
                    }
                });

                this.selectedProperty()
                        .addListener((observable, wasSelected, isSelected) -> scheduleSelectAllStateChangeProcessing());

                updateSelectedState();
            }

            private List<BooleanProperty> getAllProperties() {
                return observableList.stream().map((item) -> property.apply(item)).collect(Collectors.toList());
            }

            private void updateSelectedState() {
                if (selectAllStateChangeProcessor == null) {
                    List<BooleanProperty> allProperties = getAllProperties();
                    boolean allSelected = allProperties.stream().map(BooleanProperty::get).reduce(true,
                            (a, b) -> a && b);
                    boolean anySelected = allProperties.stream().map(BooleanProperty::get).reduce(false,
                            (a, b) -> a || b);

                    if (allSelected) {
                        this.setSelected(true);
                        this.setIndeterminate(false);
                    }

                    if (!anySelected) {
                        this.setSelected(false);
                        this.setIndeterminate(false);
                    }

                    if (anySelected && !allSelected) {
                        this.setSelected(false);
                        this.setIndeterminate(true);
                    }
                }
            }

            private void scheduleSelectAllStateChangeProcessing() {
                if (selectAllStateChangeProcessor == null) {
                    selectAllStateChangeProcessor = this::processSelectAllStateChange;
                    Platform.runLater(selectAllStateChangeProcessor);
                }
            }

            private void processSelectAllStateChange() {
                if (!this.isIndeterminate()) {
                    getAllProperties().forEach(bp -> bp.set(this.isSelected()));
                }
                selectAllStateChangeProcessor = null;
            }
        }

        @SuppressWarnings("unchecked")
        public BenchmarkCaseTableView() {
            // the table view itself
            setEditable(true);
            setRowFactory(tv -> {
                final TableRow<BenchmarkCasePreferences> row = new TableRow<>();

                // row menu
                final ContextMenu rowMenu = new ContextMenu();
                MenuItem editSelectedBenchmarkCases = new MenuItem("Edit Selected");
                editSelectedBenchmarkCases.setOnAction(e -> {
                    pluginCore.editBenchmarkCase(getSelectionModel().getSelectedItem());
                });
                MenuItem markSelectedForExecution = new MenuItem("Mark for execution");
                markSelectedForExecution.setOnAction(e -> {
                    getSelectionModel().getSelectedItems().forEach(bcp -> bcp.setMarkedForExecution(true));
                });
                MenuItem addBenchmarkCase = new MenuItem("Add Benchmark Case");
                addBenchmarkCase.setOnAction(e -> {
                    createBenchmarkCase();
                });
                MenuItem deleteBenchmarkCase = new MenuItem("Delete Benchmark Case");
                deleteBenchmarkCase.setOnAction(e -> {
                    pluginCore.deleteBenchmarkCase(getSelectionModel().getSelectedItem());
                });
                rowMenu.getItems().addAll(editSelectedBenchmarkCases, markSelectedForExecution, new SeparatorMenuItem(),
                        addBenchmarkCase, deleteBenchmarkCase);
                row.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(row.itemProperty())).then(rowMenu)
                        .otherwise((ContextMenu) null));

                // double click event
                row.setOnMouseClicked(event -> {
                    if (!row.isEmpty()) {
                        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {

                            BenchmarkCasePreferences clickedRow = row.getItem();
                            System.out.println(clickedRow.getBenchmarkCaseName());
                        }
                    }

                });

                return row;
            });
        }

        /**
         * Initializes the table columns and sets the data. This is the easiest
         * way to do, because the {@link SelectAllCheckBox}s need the item list
         * of the table view and the setItems method cannot be overriden. The
         * item list cannot be changed afterwards, but who cares.
         *
         * @param bcpl
         */
        public void initData(ObservableList<BenchmarkCasePreferences> bcpl) {
            setItems(bcpl);

            executionColumn = createCheckboxColumn("Exc", "Mark benchmark case for execution", BenchmarkCasePreferences::markedForExecutionProperty);
            nameColumn = createTextColumn("Benchmark Case Name", BenchmarkCasePreferences::benchmarkCaseNameProperty);
            modelgenActiveColumn = createCheckboxColumn("MG", "Enable the operationalization MODELGEN", BenchmarkCasePreferences::modelgenCreateReportProperty);
            initialFwdActiveColumn = createCheckboxColumn("IF", "Enable the operationalization INITIAL_FWD", BenchmarkCasePreferences::initialFwdActiveProperty);
            initialBwdActiveColumn = createCheckboxColumn("IB", "Enable the operationalization INITIAL_BWD", BenchmarkCasePreferences::initialBwdActiveProperty);
            fwdOptActiveColumn = createCheckboxColumn("FO", "Enable the operationalization FWD_OPT", BenchmarkCasePreferences::fwdOptActiveProperty);
            bwdOptActiveColumn = createCheckboxColumn("BO", "Enable the operationalization BWD_OPT", BenchmarkCasePreferences::bwdOptActiveProperty);
            syncActiveColumn = createCheckboxColumn("SY", "Enable the operationalization SYNC", BenchmarkCasePreferences::syncActiveProperty);
            ccActiveColumn = createCheckboxColumn("CC", "Enable the operationalization CC", BenchmarkCasePreferences::ccActiveProperty);
            coActiveColumn = createCheckboxColumn("CO", "Enable the operationalization CO", BenchmarkCasePreferences::coActiveProperty);

            getColumns().setAll(executionColumn, nameColumn, modelgenActiveColumn, initialFwdActiveColumn,
                    initialBwdActiveColumn, fwdOptActiveColumn, bwdOptActiveColumn, syncActiveColumn, ccActiveColumn,
                    coActiveColumn);
        }

        private <S, T> TableColumn<S, T> createTextColumn(String title, Function<S, ObservableValue<T>> property) {
            TableColumn<S, T> col = new TableColumn<>(title);
            col.setCellValueFactory(cellData -> property.apply(cellData.getValue()));
            return col;
        }

        private TableColumn<BenchmarkCasePreferences, Boolean> createCheckboxColumn(String title, String tooltip,
                Function<BenchmarkCasePreferences, BooleanProperty> property) {
            TableColumn<BenchmarkCasePreferences, Boolean> column = new TableColumn<>();
            column.setCellValueFactory(cellData -> property.apply(cellData.getValue()));
            column.setCellFactory(CheckBoxTableCell.forTableColumn(column));
            column.setMinWidth(60.0);

            Label columnLabel = new Label(title);
            columnLabel.setMaxWidth(Double.MAX_VALUE);
            columnLabel.getStyleClass().add("column-header-label");
            SelectAllCheckBox<BenchmarkCasePreferences> columnChkBox = new SelectAllCheckBox<BenchmarkCasePreferences>(
                    getItems(), property);
            HBox columnLayout = new HBox(columnChkBox, columnLabel);
            columnLayout.setSpacing(2);
            columnLayout.setAlignment(Pos.CENTER);
            if (tooltip != "") {
                Tooltip columnTooltip = new Tooltip(tooltip);
                columnLabel.setTooltip(columnTooltip);
                columnChkBox.setTooltip(columnTooltip);
            }
            column.setGraphic(columnLayout);

            return column;
        }

        private void createBenchmarkCase() {
            BenchmarkCasePreferences newBenchmarkCase = new BenchmarkCasePreferences();

            try {
                BenchmarkCasePreferencesWindow bcpWindow = new BenchmarkCasePreferencesWindow(newBenchmarkCase);
                bcpWindow.show();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // if (save) {
            //
            // }
        }

        private void editBenchmarkCase(BenchmarkCasePreferences benchmarkCase) {
            BenchmarkCasePreferences tmpBenchmarkCaseCopy = new BenchmarkCasePreferences(benchmarkCase);

            // if (save) {
            //
            // }
        }
    }
}
