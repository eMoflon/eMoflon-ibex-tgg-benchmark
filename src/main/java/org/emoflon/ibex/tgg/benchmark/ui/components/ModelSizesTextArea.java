package org.emoflon.ibex.tgg.benchmark.ui.components;

import java.math.BigInteger;
import java.util.TreeSet;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * This class represents a simple text area to enter model sizes.
 *
 * @author Andre Lehmann
 */
public class ModelSizesTextArea extends TextArea {

    private ObservableList<Integer> property;

    /**
     * Constructor for {@link ModelSizesTextArea}.
     */
    public ModelSizesTextArea() {
        super();

        setWrapText(true);

        UnaryOperator<Change> modelSizesFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("([1-9][0-9]* {0,1})*")) {
                return change;
            }
            return null;
        };

        TextFormatter<Integer> formatter = new TextFormatter<Integer>(modelSizesFilter);
        this.setTextFormatter(formatter);

        this.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                commit();
            }
        });
        this.setOnKeyPressed(e -> {
            // commit on enter
            if (e.getCode().equals(KeyCode.ENTER)) {
                commit();

                // switch on tab
            } else if (e.getCode() == KeyCode.TAB && !e.isShiftDown() && !e.isControlDown()) {
                e.consume();
                Node node = (Node) e.getSource();
                KeyEvent newEvent = new KeyEvent(e.getSource(), e.getTarget(), e.getEventType(), e.getCharacter(),
                        e.getText(), e.getCode(), e.isShiftDown(), true, e.isAltDown(), e.isMetaDown());
                node.fireEvent(newEvent);
            }
        });

        this.setTextFormatter(formatter);
    }

    private void commit() {
        String[] splittedInput = this.getText().split(" ");
        TreeSet<Integer> sortedValues = new TreeSet<>();
        for (String modelSize : splittedInput) {
            try {
                BigInteger bigNum = new BigInteger(modelSize);
                sortedValues.add(bigNum.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) < 0 ? bigNum.intValue()
                        : Integer.MAX_VALUE);
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        if (property != null) {
            property.setAll(sortedValues);
        }
        this.setText(sortedValues.stream().map(String::valueOf).collect(Collectors.joining(" ")));
    }

    public void bindListProperty(ObservableList<Integer> property) {
        this.property = property;
        this.setText(property.stream().map(String::valueOf).collect(Collectors.joining(" ")));
    }
}