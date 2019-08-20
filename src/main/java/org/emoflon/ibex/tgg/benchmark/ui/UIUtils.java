package org.emoflon.ibex.tgg.benchmark.ui;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;

/**
 * Various utils used in JavaFX UI classes.
 *
 * @author Andre Lehmann
 */
public abstract class UIUtils {

    /**
     * Bind a {@link ChoiceBox} to a enum property.
     * 
     * @param <E> Enum type
     * @param chbx The ChoiceBox
     * @param items The items of the ChoiceBox
     * @param property The property to bind to
     */
    public static <E extends Enum<?>> void bindEnumChoiceBox(ChoiceBox<E> chbx, ObservableList<E> items, ObjectProperty<E> property) {
        chbx.setItems(items);
        chbx.getSelectionModel().select(chbx.getItems().indexOf(property.getValue()));
        chbx.getSelectionModel().selectedIndexProperty().addListener((observable, old_value, new_value) -> {
            property.setValue(chbx.getItems().get(new_value.intValue()));
        });
    }
}