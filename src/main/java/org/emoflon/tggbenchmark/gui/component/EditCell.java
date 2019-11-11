package org.emoflon.tggbenchmark.gui.component;

import java.util.ArrayList;
import java.util.List;

import javafx.event.Event;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

class EditCell<S, T> extends TableCell<S, T> {
    private final TextField textField;

    // Converter for converting the text in the text field to the user type, and
    // vice-versa
    private final StringConverter<T> converter;

    private Runnable onViewFocusLostEventHandler;

    public void onTableViewFocusLost(Runnable eventHandler) {
        onViewFocusLostEventHandler = eventHandler;
    }

    /**
     * Creates and initializes an edit cell object.
     * 
     * @param converter the converter to convert from and to strings
     */
    public EditCell(TextField textField, StringConverter<T> converter) {
        this.textField = textField;
        this.converter = converter;

        itemProperty().addListener((obx, oldItem, newItem) -> {
            setText(newItem != null ? this.converter.toString(newItem) : null);
        });

        setGraphic(this.textField);
        setContentDisplay(ContentDisplay.TEXT_ONLY);

        this.textField.setOnAction(evt -> {
            commitEdit(this.converter.fromString(this.textField.getText()));
        });
        this.textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                commitEdit(this.converter.fromString(this.textField.getText()));
                if (!getTableView().isFocused()) {
                    if (onViewFocusLostEventHandler != null) {
                        onViewFocusLostEventHandler.run();
                    }
                }
            }
        });
        this.textField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                this.textField.setText(this.converter.toString(getItem()));
                cancelEdit();
                event.consume();
            } else if (event.getCode() == KeyCode.TAB) {
                commitEdit(this.converter.fromString(this.textField.getText()));
                TableColumn<S, ?> nextColumn = getNextColumn(!event.isShiftDown());
                if (nextColumn != null) {
                    getTableView().getSelectionModel().clearAndSelect(getTableRow().getIndex(), nextColumn);
                    getTableView().edit(getTableRow().getIndex(), nextColumn);
                }
            }
        });
    }

    /**
     * Convenience converter that does nothing (converts Strings to themselves and
     * vice-versa...).
     */
    public static final StringConverter<String> IDENTITY_CONVERTER = new StringConverter<String>() {

        @Override
        public String toString(String object) {
            return object;
        }

        @Override
        public String fromString(String string) {
            return string;
        }

    };

    /**
     * Convenience method for creating an EditCell for a String value.
     * 
     * @return the edit cell
     */
    public static <S> EditCell<S, String> createStringEditCell() {
        return new EditCell<S, String>(new TextField(), IDENTITY_CONVERTER);
    }

    // set the text of the text field and display the graphic
    @Override
    public void startEdit() {
        super.startEdit();
        this.textField.setText(this.converter.toString(getItem()));
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        this.textField.requestFocus();
    }

    // revert to text display
    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    // commits the edit. Update property if possible and revert to text display
    @Override
    public void commitEdit(T item) {
        // This block is necessary to support commit on losing focus, because the
        // baked-in mechanism
        // sets our editing state to false before we can intercept the loss of focus.
        // The default commitEdit(...) method simply fails if we are not editing...
        if (!isEditing() && !item.equals(getItem())) {
            TableView<S> table = getTableView();
            if (table != null) {
                TableColumn<S, T> column = getTableColumn();
                CellEditEvent<S, T> event = new CellEditEvent<>(table,
                        new TablePosition<S, T>(table, getIndex(), column), TableColumn.editCommitEvent(), item);
                Event.fireEvent(column, event);
            }
        }

        super.commitEdit(item);

        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    /**
     * Finds and returns the next editable column.
     * 
     * @param forward indicates whether to search forward or backward from the
     *                current column
     * @return the next editable column or {@code null} if there is no next column
     *         available
     */
    private TableColumn<S, ?> getNextColumn(boolean forward) {
        List<TableColumn<S, ?>> columns = new ArrayList<>();
        for (TableColumn<S, ?> column : getTableView().getColumns()) {
            columns.addAll(getEditableColumns(column));
        }
        // There is no other column that supports editing.
        if (columns.size() < 2) {
            return null;
        }
        int currentIndex = columns.indexOf(getTableColumn());
        int nextIndex = currentIndex;
        if (forward) {
            nextIndex++;
            if (nextIndex > columns.size() - 1) {
                nextIndex = 0;
            }
        } else {
            nextIndex--;
            if (nextIndex < 0) {
                nextIndex = columns.size() - 1;
            }
        }
        return columns.get(nextIndex);
    }

    /**
     * Returns all editable columns of a table column (supports nested columns).
     * 
     * @param root the table column to check for editable columns
     * @return a list of table columns which are editable
     */
    private List<TableColumn<S, ?>> getEditableColumns(TableColumn<S, ?> root) {
        List<TableColumn<S, ?>> columns = new ArrayList<>();
        if (root.getColumns().isEmpty()) {
            // We only want the leaves that are editable.
            if (root.isEditable()) {
                columns.add(root);
            }
            return columns;
        } else {
            for (TableColumn<S, ?> column : root.getColumns()) {
                columns.addAll(getEditableColumns(column));
            }
            return columns;
        }
    }
}