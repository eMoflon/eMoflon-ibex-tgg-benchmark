package org.emoflon.tggbenchmark.gui.controller.benchmark_cases_table;

import java.util.function.Function;

import org.emoflon.tggbenchmark.Core;
import org.emoflon.tggbenchmark.gui.component.CheckBoxTableCell;
import org.emoflon.tggbenchmark.gui.component.SelectAllCheckBox;
import org.emoflon.tggbenchmark.gui.controller.FXMLController;
import org.emoflon.tggbenchmark.gui.handler.EditBenchmarkCaseHandler;
import org.emoflon.tggbenchmark.gui.handler.RunBenchmarkCaseHandler;
import org.emoflon.tggbenchmark.gui.model.BenchmarkCase;
import org.emoflon.tggbenchmark.workspace.EclipseTggProject;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class BenchmarkCasesTableController extends FXMLController {

    private ObservableList<BenchmarkCase> benchmarkCase;
    private BenchmarkCaseTableView table;

    private final Core pluginCore;

    public BenchmarkCasesTableController() {
        // get the plugin core instance
        pluginCore = Core.getInstance();

        // create the content
        table = new BenchmarkCaseTableView();
        content = new AnchorPane(table);
        AnchorPane.setTopAnchor(table, 0.0);
        AnchorPane.setBottomAnchor(table, 0.0);
        AnchorPane.setLeftAnchor(table, 0.0);
        AnchorPane.setRightAnchor(table, 0.0);

        this.benchmarkCase = pluginCore.getBenchmarkCases();
        table.initData(benchmarkCase);
    }

    public class BenchmarkCaseTableView extends TableView<BenchmarkCase> {

        private TableColumn<BenchmarkCase, Boolean> executionColumn;
        private TableColumn<BenchmarkCase, String> nameColumn;
        private TableColumn<BenchmarkCase, String> projectColumn;
        private TableColumn<BenchmarkCase, String> patternMatchingEngineColumn;
        private TableColumn<BenchmarkCase, Boolean> modelgenActiveColumn;
        private TableColumn<BenchmarkCase, Boolean> initialFwdActiveColumn;
        private TableColumn<BenchmarkCase, Boolean> initialBwdActiveColumn;
        private TableColumn<BenchmarkCase, Boolean> fwdActiveColumn;
        private TableColumn<BenchmarkCase, Boolean> bwdActiveColumn;
        private TableColumn<BenchmarkCase, Boolean> fwdOptActiveColumn;
        private TableColumn<BenchmarkCase, Boolean> bwdOptActiveColumn;
        private TableColumn<BenchmarkCase, Boolean> ccActiveColumn;
        private TableColumn<BenchmarkCase, Boolean> coActiveColumn;

        public BenchmarkCaseTableView() {
            // the table view itself
            setEditable(true);
            setRowFactory(tv -> {
                final TableRow<BenchmarkCase> row = new TableRow<>();

                // row menu
                MenuItem runSelectedBenchmarkCases = new MenuItem("Run selected case");
                runSelectedBenchmarkCases.setOnAction(e -> {
                    new RunBenchmarkCaseHandler().execute(getItems());
                });

                final ContextMenu rowMenu = new ContextMenu();
                MenuItem editSelectedBenchmarkCases = new MenuItem("Edit selected case");
                editSelectedBenchmarkCases.setOnAction(e -> {
                    new EditBenchmarkCaseHandler().execute(this);
                });

                MenuItem duplicateSelected = new MenuItem("Duplicate selected case");
                duplicateSelected.setOnAction(e -> {
                    BenchmarkCase selectedBcp = getSelectionModel().getSelectedItem();
                    if (selectedBcp != null) {
                        BenchmarkCase newBcp = new BenchmarkCase(selectedBcp);
                        newBcp.setBenchmarkCaseName(newBcp.getBenchmarkCaseName() + " Copy");
                        newBcp.getEclipseProject().addBenchmarkCase(newBcp);
                    }
                });

                rowMenu.getItems().addAll(runSelectedBenchmarkCases, editSelectedBenchmarkCases, duplicateSelected);
                row.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(row.itemProperty())).then(rowMenu)
                        .otherwise((ContextMenu) null));

                // double click event
                row.setOnMouseClicked(event -> {
                    if (!row.isEmpty()) {
                        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                            new EditBenchmarkCaseHandler().execute(this);
                        }
                    }
                });

                return row;
            });
        }

        /**
         * Initializes the table columns and sets the data. This is the easiest way to
         * do, because the {@link SelectAllCheckBox}s need the item list of the table
         * view and the setItems method cannot be overriden. The item list cannot be
         * changed afterwards, but who cares.
         *
         * @param projects
         */
        public void initData(ObservableList<BenchmarkCase> projects) {
            setItems(projects);

            executionColumn = createCheckboxColumn("Exc", "Mark benchmark case for execution",
                    bc -> (bc.markedForExecutionProperty()));
            getColumns().add(executionColumn);

            nameColumn = createTextColumn("Benchmark Case Name", BenchmarkCase::benchmarkCaseNameProperty);
            getColumns().add(nameColumn);

            projectColumn = createTextColumn("Project", "The asscociated TGG project",
                    bc -> bc.getEclipseProject().nameProperty());
            getColumns().add(projectColumn);

            patternMatchingEngineColumn = createTextColumn("Pattern Matching Engine",
                    "The pattern matching engine to use", bc -> {
                        StringProperty property = new SimpleStringProperty(bc.getPatternMatchingEngine().toString());
                        bc.patternMatchingEngineProperty()
                                .addListener(e -> property.set(bc.getPatternMatchingEngine().toString()));
                        return property;
                    });
            getColumns().add(patternMatchingEngineColumn);

            modelgenActiveColumn = createCheckboxColumn("MG", "Enable the operationalization MODELGEN",
                    bc -> (bc.modelgenIncludeReportProperty()));
            getColumns().add(modelgenActiveColumn);

            initialFwdActiveColumn = createCheckboxColumn("IF", "Enable the operationalization INITIAL_FWD",
                    bc -> (bc.initialFwdActiveProperty()));
            getColumns().add(initialFwdActiveColumn);

            initialBwdActiveColumn = createCheckboxColumn("IB", "Enable the operationalization INITIAL_BWD",
                    bc -> (bc.initialBwdActiveProperty()));
            getColumns().add(initialBwdActiveColumn);

            fwdActiveColumn = createCheckboxColumn("F", "Enable the operationalization FWD",
                    bc -> (bc.fwdActiveProperty()));
            getColumns().add(fwdActiveColumn);

            bwdActiveColumn = createCheckboxColumn("B", "Enable the operationalization BWD",
                    bc -> (bc.bwdActiveProperty()));
            getColumns().add(bwdActiveColumn);

            fwdOptActiveColumn = createCheckboxColumn("FO", "Enable the operationalization FWD_OPT",
                    bc -> (bc.fwdOptActiveProperty()));
            getColumns().add(fwdOptActiveColumn);

            bwdOptActiveColumn = createCheckboxColumn("BO", "Enable the operationalization BWD_OPT",
                    bc -> (bc.bwdOptActiveProperty()));
            getColumns().add(bwdOptActiveColumn);

            ccActiveColumn = createCheckboxColumn("CC", "Enable the operationalization CC",
                    bc -> (bc.ccActiveProperty()));
            getColumns().add(ccActiveColumn);

            coActiveColumn = createCheckboxColumn("CO", "Enable the operationalization CO",
                    bc -> (bc.coActiveProperty()));
            getColumns().add(coActiveColumn);
        }

        private <S, T> TableColumn<S, T> createTextColumn(String title, Function<S, ObservableValue<T>> property) {
            return createTextColumn(title, "", property);
        }

        private <S, T> TableColumn<S, T> createTextColumn(String title, String tooltip,
                Function<S, ObservableValue<T>> property) {
            TableColumn<S, T> col = new TableColumn<>();
            Label columnLabel = new Label(title);
            col.setGraphic(columnLabel);
            if (tooltip != "") {
                Tooltip columnTooltip = new Tooltip(tooltip);
                columnLabel.setTooltip(columnTooltip);
            }
            col.setCellValueFactory(cellData -> property.apply(cellData.getValue()));
            return col;
        }

        private TableColumn<BenchmarkCase, Boolean> createCheckboxColumn(String title, String tooltip,
                Function<BenchmarkCase, BooleanProperty> property) {
            TableColumn<BenchmarkCase, Boolean> column = new TableColumn<>();
            column.setCellValueFactory(cellData -> property.apply(cellData.getValue()));
            column.setCellFactory(
                    new Callback<TableColumn<BenchmarkCase, Boolean>, TableCell<BenchmarkCase, Boolean>>() {
                        @Override
                        public TableCell<BenchmarkCase, Boolean> call(TableColumn<BenchmarkCase, Boolean> column) {
                            CheckBoxTableCell<BenchmarkCase> cell = new CheckBoxTableCell<BenchmarkCase>();

                            cell.getCheckBox().setOnAction(e -> {
                                if (cell.getIndex() >= 0) {
                                    getItems().get(cell.getIndex()).getEclipseProject().delayedSavePreferences();
                                }
                            });
                            return cell;
                        }
                    });
            column.setMinWidth(60.0);

            Label columnLabel = new Label(title);
            columnLabel.setMaxWidth(Double.MAX_VALUE);
            columnLabel.getStyleClass().add("column-header-label");
            SelectAllCheckBox<BenchmarkCase> columnChkBox = new SelectAllCheckBox<BenchmarkCase>(getItems(), property);
            columnChkBox.setOnAction(e -> {
                // save all
                for (EclipseTggProject project : Core.getInstance().getWorkspace().getTggProjects()) {
                    project.delayedSavePreferences();
                }
            });

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
    }

    public BenchmarkCaseTableView getTable() {
        return table;
    }
}
