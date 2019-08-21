package org.emoflon.ibex.tgg.benchmark.ui.eclipse_view.parts;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.emoflon.ibex.tgg.benchmark.Core;
import org.emoflon.ibex.tgg.benchmark.model.BenchmarkCasePreferences;
import org.emoflon.ibex.tgg.benchmark.model.EclipseProject;
import org.emoflon.ibex.tgg.benchmark.ui.benchmark_case_preferences.BenchmarkCasePreferencesWindow;
import org.emoflon.ibex.tgg.benchmark.ui.components.Part;
import org.emoflon.ibex.tgg.benchmark.ui.components.SelectAllCheckBox;

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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import org.emoflon.ibex.tgg.benchmark.ui.components.CheckBoxTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class TGGBenchmarkViewPart extends Part {

    private ObservableList<EclipseProject> tggProjects;
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

    public void initData(ObservableList<EclipseProject> tggProjects) {
        this.tggProjects = tggProjects;
        table.initData(tggProjects);
    }

    public class BenchmarkCaseTableView extends TableView<EclipseProject> {

        private TableColumn<EclipseProject, Boolean> executionColumn;
        private TableColumn<EclipseProject, String> nameColumn;
        private TableColumn<EclipseProject, Boolean> modelgenActiveColumn;
        private TableColumn<EclipseProject, Boolean> initialFwdActiveColumn;
        private TableColumn<EclipseProject, Boolean> initialBwdActiveColumn;
        private TableColumn<EclipseProject, Boolean> fwdOptActiveColumn;
        private TableColumn<EclipseProject, Boolean> bwdOptActiveColumn;
        private TableColumn<EclipseProject, Boolean> syncActiveColumn;
        private TableColumn<EclipseProject, Boolean> ccActiveColumn;
        private TableColumn<EclipseProject, Boolean> coActiveColumn;

        public BenchmarkCaseTableView() {
            // the table view itself
            setEditable(true);
            setRowFactory(tv -> {
                final TableRow<EclipseProject> row = new TableRow<>();

                // row menu
                final ContextMenu rowMenu = new ContextMenu();
                MenuItem editSelectedBenchmarkCases = new MenuItem("Edit Selected");
                editSelectedBenchmarkCases.setOnAction(e -> {
                    pluginCore.editBenchmarkCase(getSelectionModel().getSelectedItem());
                });
                MenuItem markSelectedForExecution = new MenuItem("Mark for execution");
                markSelectedForExecution.setOnAction(e -> {
                    getSelectionModel().getSelectedItems()
                            .forEach(project -> project.getBenchmarkCasePreferences().setMarkedForExecution(true));
                });
                MenuItem runSelectedBenchmarkCases = new MenuItem("Run Selected");
                runSelectedBenchmarkCases.setOnAction(e -> {
                    // TODO: implement
                });
                // MenuItem addBenchmarkCase = new MenuItem("Add Benchmark Case");
                // addBenchmarkCase.setOnAction(e -> {
                // createBenchmarkCase();
                // });
                // MenuItem deleteBenchmarkCase = new MenuItem("Delete Benchmark Case");
                // deleteBenchmarkCase.setOnAction(e -> {
                // pluginCore.deleteBenchmarkCase(getSelectionModel().getSelectedItem());
                // });
                rowMenu.getItems().addAll(editSelectedBenchmarkCases, new SeparatorMenuItem(),
                        markSelectedForExecution);
                row.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(row.itemProperty())).then(rowMenu)
                        .otherwise((ContextMenu) null));

                // double click event
                row.setOnMouseClicked(event -> {
                    if (!row.isEmpty()) {
                        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                            pluginCore.editBenchmarkCase(row.getItem());
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
        public void initData(ObservableList<EclipseProject> projects) {
            setItems(projects);

            executionColumn = createCheckboxColumn("Exc", "Mark benchmark case for execution",
                    ep -> (ep.getBenchmarkCasePreferences().markedForExecutionProperty()));
            nameColumn = createTextColumn("Benchmark Case Name", EclipseProject::nameProperty);
            modelgenActiveColumn = createCheckboxColumn("MG", "Enable the operationalization MODELGEN",
                    ep -> (ep.getBenchmarkCasePreferences().modelgenIncludeReportProperty()));
            initialFwdActiveColumn = createCheckboxColumn("IF", "Enable the operationalization INITIAL_FWD",
                    ep -> (ep.getBenchmarkCasePreferences().initialFwdActiveProperty()));
            initialBwdActiveColumn = createCheckboxColumn("IB", "Enable the operationalization INITIAL_BWD",
                    ep -> (ep.getBenchmarkCasePreferences().initialBwdActiveProperty()));
            fwdOptActiveColumn = createCheckboxColumn("FO", "Enable the operationalization FWD_OPT",
                    ep -> (ep.getBenchmarkCasePreferences().fwdOptActiveProperty()));
            bwdOptActiveColumn = createCheckboxColumn("BO", "Enable the operationalization BWD_OPT",
                    ep -> (ep.getBenchmarkCasePreferences().bwdOptActiveProperty()));
            syncActiveColumn = createCheckboxColumn("SY", "Enable the operationalization SYNC",
                    ep -> (ep.getBenchmarkCasePreferences().syncActiveProperty()));
            ccActiveColumn = createCheckboxColumn("CC", "Enable the operationalization CC",
                    ep -> (ep.getBenchmarkCasePreferences().ccActiveProperty()));
            coActiveColumn = createCheckboxColumn("CO", "Enable the operationalization CO",
                    ep -> (ep.getBenchmarkCasePreferences().coActiveProperty()));

            getColumns().setAll(executionColumn, nameColumn, modelgenActiveColumn, initialFwdActiveColumn,
                    initialBwdActiveColumn, fwdOptActiveColumn, bwdOptActiveColumn, syncActiveColumn, ccActiveColumn,
                    coActiveColumn);
        }

        private <S, T> TableColumn<S, T> createTextColumn(String title, Function<S, ObservableValue<T>> property) {
            TableColumn<S, T> col = new TableColumn<>(title);
            col.setCellValueFactory(cellData -> property.apply(cellData.getValue()));
            return col;
        }

        private TableColumn<EclipseProject, Boolean> createCheckboxColumn(String title, String tooltip,
                Function<EclipseProject, BooleanProperty> property) {
            TableColumn<EclipseProject, Boolean> column = new TableColumn<>();
            column.setCellValueFactory(cellData -> property.apply(cellData.getValue()));
            // column.setCellValueFactory(cellData -> {
            // BooleanProperty bp = property.apply(cellData.getValue());
            // bp.addListener((o, old, value) -> {
            // System.out.println("dddd");
            // });
            // System.out.println(cellData.getValue().getName());
            // return bp;
            // });
            // column.setCellFactory(CheckBoxTableCell.forTableColumn(column));

            // column.setCellFactory(CheckBoxTableCell.forTableColumn(new Callback<Integer,
            // ObservableValue<Boolean>>() {
            // @Override
            // public ObservableValue<Boolean> call(Integer param) {
            // getItems()
            // System.out.println("Cours "+items.get(param).getCours()+" changed value to "
            // +items.get(param).isChecked());
            // return items.get(param).checkedProperty();
            // }
            // }));

            column.setCellFactory(
                    new Callback<TableColumn<EclipseProject, Boolean>, TableCell<EclipseProject, Boolean>>() {
                        @Override
                        public TableCell<EclipseProject, Boolean> call(TableColumn<EclipseProject, Boolean> column) {
                            CheckBoxTableCell<EclipseProject> cell = new CheckBoxTableCell<EclipseProject>();
                            
                            cell.getCheckBox().setOnAction(e -> {
                                System.out.println("action");
                            });
                            return cell;
                        }
                    });

            // column.setCellFactory(
            // new Callback<TableColumn<EclipseProject, Boolean>, TableCell<EclipseProject,
            // Boolean>>() {
            // @Override
            // public TableCell<EclipseProject, Boolean> call(TableColumn<EclipseProject,
            // Boolean> column) {
            // CheckBoxTableCell<EclipseProject, Boolean> cell = new
            // CheckBoxTableCell<EclipseProject, Boolean>();
            // cell.getItem().
            //// .addListener((o, old, value) -> {
            //// System.out.println("dddd");
            //// System.out.println(cell.getTableRow().getItem());
            //// });
            // return cell;
            // }
            // });

            column.setMinWidth(60.0);

            Label columnLabel = new Label(title);
            columnLabel.setMaxWidth(Double.MAX_VALUE);
            columnLabel.getStyleClass().add("column-header-label");
            SelectAllCheckBox<EclipseProject> columnChkBox = new SelectAllCheckBox<EclipseProject>(getItems(),
                    property);
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

        // private void createBenchmarkCase() {
        // BenchmarkCasePreferences newBenchmarkCase = new BenchmarkCasePreferences();

        // try {
        // BenchmarkCasePreferencesWindow bcpWindow = new
        // BenchmarkCasePreferencesWindow(newBenchmarkCase);
        // bcpWindow.show();
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }

        // // if (save) {
        // //
        // // }
        // }

        private void editBenchmarkCase(BenchmarkCasePreferences benchmarkCase) {
            BenchmarkCasePreferences tmpBenchmarkCaseCopy = new BenchmarkCasePreferences(benchmarkCase);

            // if (save) {
            //
            // }
        }
    }
}
