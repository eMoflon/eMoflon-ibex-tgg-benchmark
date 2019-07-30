package org.emoflon.ibex.tgg.benchmark.ui.components;

import javafx.event.Event;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
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
    @Override public void startEdit() {
        if (! isEditable() || ! getListView().isEditable()) {
            return;
        }
        super.startEdit();
        System.out.println("start edit");

        if (isEditing()) {
            if (textField == null) {
                textField = new IntegerTextField();
                textField.setOnKeyReleased(event -> {
                    if (event.getCode() == KeyCode.ENTER) {
                        commitEdit(textField.getIntValue());
                    } else if (event.getCode() == KeyCode.ESCAPE) {
                        cancelEdit();
                    } else if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) {
                        textField.fireEvent(event);
                        Event.fireEvent(textField, null);
                    }
                });
            }

            textField.setText(getItem().toString());
            setText(null);
            setGraphic(textField);
            textField.selectAll();
            textField.requestFocus();
        }
    }

    /** {@inheritDoc} */
    @Override public void cancelEdit() {
        super.cancelEdit();
        System.out.println("cancel edit" + getItem());
        setText(getItem().toString());
        setGraphic(null);
    }
    
    /** {@inheritDoc} */
    @Override public void updateItem(Integer item, boolean empty) {
        super.updateItem(item, empty);
        System.out.println("update item" + item + " " + getText() + " " + empty);
        if (isEmpty()) {
            setText(null);
            setGraphic(null);
        } else {
            Integer
            if (isEditing()) {
                if (textField != null) {
                    textField.setText(getItem().toString());
                }
                setText(null);
                setGraphic(textField);
            } else {
                setText(getItem().toString());
                setGraphic(null);
            }
        }
    }
}
