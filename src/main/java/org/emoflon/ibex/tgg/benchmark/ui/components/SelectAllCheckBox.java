package org.emoflon.ibex.tgg.benchmark.ui.components;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;

public class SelectAllCheckBox<S> extends CheckBox {
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