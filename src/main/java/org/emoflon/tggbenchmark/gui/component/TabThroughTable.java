package org.emoflon.tggbenchmark.gui.component;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

@SuppressWarnings({"rawtypes", "unchecked"})
abstract class TabThroughTable<S> extends TableView<S> {

    public TabThroughTable() {
        super();

        getStylesheets().add(getClass().getResource("../../resources/css/tab-through-table.css").toExternalForm());

        // single cell selection mode
        getSelectionModel().setCellSelectionEnabled(true);
        getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // make editable
        setEditable(true);

        addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (!getItems().isEmpty()) {
                    // get first selected cell
                    ObservableList<TablePosition> selectedCells = getSelectionModel().getSelectedCells();
                    TablePosition firstCell = selectedCells.get(0);
                    for (int i = 1; i < selectedCells.size(); i++) {
                        if (selectedCells.get(i).getRow() <= firstCell.getRow() && selectedCells.get(i).getColumn() < firstCell.getColumn()) {
                            firstCell = selectedCells.get(i);
                        }
                    }
                    
                    if (event.getCode() == KeyCode.ENTER) {
                        if (firstCell.getRow() == -1) {
                            getSelectionModel().select(0);
                        } else if (!event.isShiftDown()) {
                            // downwards
                            if (firstCell.getRow() == getItems().size() - 1) {
                                addRow();
                            }
                            getSelectionModel().clearAndSelect(firstCell.getRow() + 1, firstCell.getTableColumn());
                        } else {
                            // upwards
                            if (firstCell.getRow() > 0) {
                                getSelectionModel().clearAndSelect(firstCell.getRow() - 1, firstCell.getTableColumn());
                            }
                        }
                        event.consume();
                    }
                    
                    if (event.getCode() == KeyCode.TAB) {
                        if (firstCell.getRow() == -1) {
                            getSelectionModel().select(0);
                        } else if (!event.isShiftDown()) {
                            // forward tab
                            if (firstCell.getColumn() < getColumns().size() - 1) {
                                getSelectionModel().clearAndSelect(firstCell.getRow(), getColumns().get(firstCell.getColumn() + 1));
                            } else if (firstCell.getRow() == getItems().size() - 1) {
                                addRow();
                                getSelectionModel().clearAndSelect(firstCell.getRow() + 1, getColumns().get(0));
                            } else {
                                getSelectionModel().clearAndSelect(firstCell.getRow() + 1, getColumns().get(0));
                            }
                        } else {
                            // backward tab
                            if (firstCell.getColumn() > 0) {
                                getSelectionModel().clearAndSelect(firstCell.getRow(), getColumns().get(firstCell.getColumn() - 1));
                            } else if (firstCell.getRow() > 0) {
                                getSelectionModel().clearAndSelect(firstCell.getRow() - 1,
                                        getColumns().get(getColumns().size() - 1));
                            }
                        }
                        event.consume();
                    }
    
                    if (getEditingCell() == null) {
                        // switch to edit mode on keypress, but only if we aren't already in edit mode
                        if (event.getCode().isLetterKey() || event.getCode().isDigitKey()) {
                            edit(firstCell.getRow(), firstCell.getTableColumn());
    
                        } else if (event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.BACK_SPACE) {
                            getSelectionModel().select(firstCell.getRow(), firstCell.getTableColumn());
                            ObservableValue ov = getColumns().get(firstCell.getColumn())
                                    .getCellObservableValue(firstCell.getRow());
                            if (ov instanceof StringProperty) {
                                ((StringProperty) ov).setValue("");
                            } else if (ov instanceof IntegerProperty) {
                                ((IntegerProperty) ov).setValue(0);
                            }
                        }
                    }
                }
            }
        });

        focusedProperty().addListener((observable, wasFocused, isFocused) -> {
            if (isFocused) {
                // add new row if no specific row was selected
                if (getSelectionModel().getSelectedIndex() == -1) {
                    addRow();
                }
            } else {
                // cleanup empty rows on focus loss
                if (getEditingCell() == null) {
                    pruneEmtpyRows();
                }
            }
        });
    }

    /**
     * Insert a new default row to the table, select a cell of it and scroll to it.
     */
    protected void addRow() {
        // create new record and add it to the model
        S data = createDefaultItem();
        getItems().add(data);

        // select newly inserted row
        int row = getItems().isEmpty() ? 0 : getItems().size() - 1;
        getSelectionModel().clearSelection();
        getSelectionModel().select(row, getColumns().get(0));
        scrollTo(data);
    }

    public abstract S createDefaultItem();

    protected void pruneEmtpyRows() {
        getSelectionModel().clearSelection();
        ObservableList<S> items = getItems();
        for (int i = items.size() - 1; i >= 0; i--) {
            if (isRowEmtpty(items.get(i))) {
                items.remove(i);
            }
        }
    }

    protected abstract boolean isRowEmtpty(S rowData);
}