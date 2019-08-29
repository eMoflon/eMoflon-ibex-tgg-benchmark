package org.emoflon.ibex.tgg.benchmark.ui.components;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

public class IntegerTextFieldListCell extends ListCell<Integer> {

    private IntegerTextField textField;

    /**
     * Creates a TextFieldListCell that provides a {@link TextField} when put into
     * editing mode that allows editing of the cell content. This method will work
     * on any ListView instance, regardless of its generic type. However, to enable
     * this, a {@link StringConverter} must be provided that will convert the given
     * String (from what the user typed in) into an instance of type T. This item
     * will then be passed along to the {@link ListView#onEditCommitProperty()}
     * callback.
     * 
     * @param converter A {@link StringConverter converter} that can convert the
     *                  given String (from what the user typed in) into an instance
     *                  of type T.
     */
    public IntegerTextFieldListCell() {
        this.getStyleClass().add("text-field-list-cell");
        this.setPrefWidth(0);
    }

    /** {@inheritDoc} */
    @Override
    public void startEdit() {
        if (!isEditable() || !getListView().isEditable()) {
            return;
        }
        super.startEdit();

        if (isEditing()) {
            if (textField == null) {
                textField = new IntegerTextField();
                textField.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
                    if (event.getCode() == KeyCode.ENTER) {
                        commitEdit(textField.getValue());
                        event.consume();
                    }
                    if (event.getCode() == KeyCode.ESCAPE) {
                        cancelEdit();
                        event.consume();
                    } else if (event.getCode() == KeyCode.UP) {
                        getListView().getSelectionModel().selectPrevious();
                        event.consume();
                    } else if (event.getCode() == KeyCode.DOWN) {
                        getListView().getSelectionModel().selectNext();
                        event.consume();
                    }
                });
                textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                    if (!isNowFocused) {
                        commitEdit(textField.getValue());
                    }
                });
            }

            textField.setText(getItem() == null ? null : getItem().toString());
            setText(null);
            setGraphic(textField);
            textField.selectAll();
            textField.requestFocus();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText(getItem() == null ? null : getItem().toString());
        setGraphic(null);
    }

    /** {@inheritDoc} */
    @Override
    public void updateItem(Integer item, boolean empty) {
        super.updateItem(item, empty);
        if (isEmpty()) {
            setText(null);
            setGraphic(null);
        } else {
            Integer fieldItem = getItem();
            if (isEditing()) {
                if (textField != null) {
                    textField.setText(fieldItem == null ? null : fieldItem.toString());
                }
                setText(null);
                setGraphic(textField);
            } else {
                setText(fieldItem == null ? null : fieldItem.toString());
                setGraphic(null);
            }
        }
    }
}
