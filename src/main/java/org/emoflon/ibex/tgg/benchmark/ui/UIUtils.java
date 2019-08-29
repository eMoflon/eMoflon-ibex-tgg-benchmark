package org.emoflon.ibex.tgg.benchmark.ui;

import java.lang.reflect.Method;

import org.emoflon.ibex.tgg.benchmark.utils.ReflectionUtils;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.WritableObjectValue;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * Various utils used in JavaFX UI classes.
 *
 * @author Andre Lehmann
 */
public abstract class UIUtils {

    /**
     * Bind a {@link ChoiceBox} to a enum property.
     * 
     * @param <E>      Enum type
     * @param chbx     The ChoiceBox
     * @param items    The items of the ChoiceBox
     * @param property The property to bind to
     */
    public static <E extends Enum<?>> void bindEnumChoiceBox(ChoiceBox<E> chbx, ObservableList<E> items,
            ObjectProperty<E> property) {
        chbx.setItems(items);
        chbx.getSelectionModel().select(property.getValue());
        chbx.getSelectionModel().selectedIndexProperty().addListener((observable, old_value, new_value) -> {
            property.setValue(chbx.getItems().get(new_value.intValue()));
        });
    }

    public static <T> void bindChoiceBox(ChoiceBox<T> chbx, ObservableList<T> items, StringProperty property) {
        chbx.setItems(items);
        // select the property if it is contained in the items list
        for (T item : items) {
            if (item.toString().equals(property.get())) {
                chbx.getSelectionModel().select(item);
                break;
            }
        }
        chbx.getSelectionModel().selectedIndexProperty().addListener((observable, old_value, new_value) -> {
            if (new_value != null) {
                property.set(chbx.getItems().get(new_value.intValue()).toString());
            } else {
                property.set(null);
            }
        });
    }

    public static <T> void bindChoiceBox(ChoiceBox<T> chbx, ObservableList<T> items, WritableObjectValue<T> property) {
        chbx.setItems(items);
        // select the property if it is contained in the items list
        chbx.getSelectionModel().select(property.get());
        chbx.getSelectionModel().selectedIndexProperty().addListener((observable, old_value, new_value) -> {
            if (new_value != null) {
                property.set(chbx.getItems().get(new_value.intValue()));
            } else {
                property.set(null);
            }
        });
    }

    public static void bindMethodComboBox(ComboBox<Method> comboBox, ObservableList<Method> items,
            StringProperty property) {
        // give the items a nice appearance
        comboBox.setCellFactory(new Callback<ListView<Method>, ListCell<Method>>() {
            @Override
            public ListCell<Method> call(ListView<Method> param) {
                ListCell<Method> cell = new ListCell<Method>() {
                    @Override
                    protected void updateItem(Method item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            VBox content = new VBox();
                            Label classLabel = new Label(item.getDeclaringClass().getName());
                            Label methodLabel = new Label(item.getName());
                            classLabel.setStyle("-fx-font-size: 70%;");

                            content.getChildren().addAll(methodLabel, classLabel);
                            setGraphic(content);
                        }
                    }
                };
                return cell;
            }
        });
        // display the method in the comboBox
        comboBox.setConverter(new StringConverter<Method>() {
            @Override
            public String toString(Method object) {
                return object.getDeclaringClass().getSimpleName() + "#" + object.getName();
            }

            @Override
            public Method fromString(String string) {
                // not used
                return null;
            }
        });
        // set the items to the combobox
        comboBox.setItems(items);
        // select the item that matches the property value
        for (Method item : items) {
            if (ReflectionUtils.methodToString(item).equals(property.getValue())) {
                comboBox.getSelectionModel().select(item);
                break;
            }
        }
        // update property when an item has been selected
        comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                property.setValue(ReflectionUtils.methodToString(newValue));
            } else {
                property.setValue("");
            }
        });
    }
}