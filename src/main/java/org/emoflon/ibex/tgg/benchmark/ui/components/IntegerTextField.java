package org.emoflon.ibex.tgg.benchmark.ui.components;

import java.math.BigInteger;
import java.util.function.UnaryOperator;

import javafx.beans.property.IntegerProperty;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.util.converter.IntegerStringConverter;

/**
 * Text input component that allows a user to enter an integer value
 *
 * @author Andre Lehmann
 */
public class IntegerTextField extends TextField {

    private final TextFormatter<Integer> formatter;

    /**
     * Constructor for {@link IntegerTextField}.
     */
    public IntegerTextField() {
        this("");
    }

    /**
     * Constructor for {@link IntegerTextField}.
     * 
     * @param text A string for text content.
     */
    public IntegerTextField(String text) {
        super();

        UnaryOperator<Change> integerFormatFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        };

        formatter = new TextFormatter<Integer>(new IntegerStringConverter2(), 0, integerFormatFilter);
        this.setTextFormatter(formatter);

        setText(text);
    }

    public Integer getValue() {
        return formatter.getValue();
    }

    public int getIntValue() {
        Integer integerValue = formatter.getValue();
        System.out.println("intvalue: " + (integerValue == null ? 0 : integerValue.intValue()));
        return integerValue == null ? 0 : integerValue.intValue();
    }

    /**
     * Bind a IntegerProperty to the value of this text field.
     * 
     * @param property to bind to the textfield
     */
    public final void bindIntegerProperty(IntegerProperty property) {
        formatter.valueProperty().bindBidirectional(property.asObject());
    }

    private class IntegerStringConverter2 extends IntegerStringConverter {

        /** {@inheritDoc} */
        @Override
        public Integer fromString(String value) {
            if (value == null || value.isEmpty()) {
                return null;
            }

            if (value.matches("\\d+")) {
                // prevent integer overflow
                BigInteger num = new BigInteger(value);
                return num.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) < 0 ? num.intValue() : Integer.MAX_VALUE;
            }

            return 0;
        }
    }
}