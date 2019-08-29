/*
 * Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package org.emoflon.ibex.tgg.benchmark.ui.components;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * This is a reimplemented version of the
 * {@link javafx.scene.control.cell.CheckBoxTableCell}. The only difference is,
 * that you can bind to the selectedProperty of the ChceckBox.
 */
public class CheckBoxTableCell<S> extends TableCell<S, Boolean> {

    // this custom class is just because of this little piece of shit
    private final CheckBox checkBox;

    private boolean showLabel;

    private ObservableValue<Boolean> booleanProperty;

    /**
     * Creates a default CheckBoxTableCell.
     */
    public CheckBoxTableCell() {
        this.getStyleClass().add("check-box-table-cell");

        this.checkBox = new CheckBox();

        // by default the graphic is null until the cell stops being empty
        setGraphic(null);
        setSelectedStateCallback(null);
    }

    // --- converter
    private ObjectProperty<StringConverter<Boolean>> converter = new SimpleObjectProperty<StringConverter<Boolean>>(
            this, "converter") {
        @Override
        protected void invalidated() {
            updateShowLabel();
        }
    };

    /**
     * The {@link StringConverter} property.
     */
    public final ObjectProperty<StringConverter<Boolean>> converterProperty() {
        return converter;
    }

    /**
     * Sets the {@link StringConverter} to be used in this cell.
     */
    public final void setConverter(StringConverter<Boolean> value) {
        converterProperty().set(value);
    }

    /**
     * Returns the {@link StringConverter} used in this cell.
     */
    public final StringConverter<Boolean> getConverter() {
        return converterProperty().get();
    }

    // --- selected state callback property
    private ObjectProperty<Callback<Integer, ObservableValue<Boolean>>> selectedStateCallback = new SimpleObjectProperty<Callback<Integer, ObservableValue<Boolean>>>(
            this, "selectedStateCallback");

    /**
     * Property representing the {@link Callback} that is bound to by the CheckBox
     * shown on screen.
     */
    public final ObjectProperty<Callback<Integer, ObservableValue<Boolean>>> selectedStateCallbackProperty() {
        return selectedStateCallback;
    }

    /**
     * Sets the {@link Callback} that is bound to by the CheckBox shown on screen.
     */
    public final void setSelectedStateCallback(Callback<Integer, ObservableValue<Boolean>> value) {
        selectedStateCallbackProperty().set(value);
    }

    /**
     * Returns the {@link Callback} that is bound to by the CheckBox shown on
     * screen.
     */
    public final Callback<Integer, ObservableValue<Boolean>> getSelectedStateCallback() {
        return selectedStateCallbackProperty().get();
    }

    /**
     * @return the checkBox
     */
    public CheckBox getCheckBox() {
        return checkBox;
    }

    /** {@inheritDoc} */
    @Override
    public void updateItem(Boolean item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            StringConverter<Boolean> c = getConverter();

            if (showLabel) {
                setText(c.toString(item));
            }
            setGraphic(checkBox);

            if (booleanProperty instanceof BooleanProperty) {
                checkBox.selectedProperty().unbindBidirectional((BooleanProperty) booleanProperty);
            }
            ObservableValue<Boolean> obsValue = getSelectedProperty();
            if (obsValue instanceof BooleanProperty) {
                booleanProperty = obsValue;
                checkBox.selectedProperty().bindBidirectional((BooleanProperty) booleanProperty);
            }

            checkBox.disableProperty().bind(Bindings.not(getTableView().editableProperty()
                    .and(getTableColumn().editableProperty()).and(editableProperty())));
        }
    }

    private void updateShowLabel() {
        this.showLabel = converter != null;
        this.checkBox.setAlignment(showLabel ? Pos.CENTER_LEFT : Pos.CENTER);
    }

    private ObservableValue<Boolean> getSelectedProperty() {
        return getSelectedStateCallback() != null ? getSelectedStateCallback().call(getIndex())
                : getTableColumn().getCellObservableValue(getIndex());
    }
}
