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
 * @version 1.0
 * @since 2019-07-17
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

        formatter = new TextFormatter<Integer>(new IntegerStringConverter2(), 99, integerFormatFilter);
        this.setTextFormatter(formatter);

        setText(text);
    }

    public Integer getValue() {
        return formatter.getValue();
    }

    public int getIntValue() {
        return formatter.getValue().intValue();        
    }

    /**
     * Bind a IntegerProperty to the value of this text field.
     * @param property to bind to the textfield
     */
    public final void bindIntegerProperty(IntegerProperty property) {
        formatter.valueProperty().bindBidirectional(property.asObject());
    }

    private class IntegerStringConverter2 extends IntegerStringConverter {

        /** {@inheritDoc} */
        @Override
        public Integer fromString(String value) {
            System.out.println("from string " + value);
            if (value == null) {
                return null;
            }

            if (value.isEmpty()) {
                return null;
            }

            // if (value.isEmpty())
            //     return 0;

            if (value.matches("\\d+")) {
                // prevent integer overflow
                BigInteger num = new BigInteger(value);
                return num.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) < 0 ? num.intValue()
                        : Integer.MAX_VALUE;
            }

            return 0;
        }
    }
}