package org.emoflon.ibex.tgg.benchmark.ui.components;

import java.math.BigInteger;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.property.IntegerProperty;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.util.StringConverter;

/**
 * <p>
 * Text input component that allows a user to enter a time value. Time is hereby
 * represented as seconds.
 * </p>
 *
 * <p>
 * Format examples:
 * </p>
 * <ul>
 * <li>30</li>
 * <li>30s</li>
 * <li>5m</li>
 * <li>1h</li>
 * </ul>
 *
 * @author Andre Lehmann
 */
public class TimeTextField extends TextField {

    private final TextFormatter<Integer> formatter;

    /**
     * Constructor for {@link TimeTextField}.
     */
    public TimeTextField() {
        super();

        UnaryOperator<Change> timeFormatFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("(\\d+(s|m|h{0,1}))|\\d*")) {
                return change;
            }
            return null;
        };

        formatter = new TextFormatter<Integer>(new TimeStringConverter(), 0, timeFormatFilter);
        this.setTextFormatter(formatter);
    }

    /**
     * Bind a IntegerProperty to the value of this text field.
     * 
     * @param property to bind to the textfield
     */
    public final void bindIntegerProperty(IntegerProperty property) {
        formatter.valueProperty().bindBidirectional(property.asObject());
    }

    /**
     * Returns the seconds entered in this field.
     * 
     * @return the seconds
     */
    public Integer getValue() {
        return formatter.getValue();
    }

    /**
     * <p>
     * Converter for time values. Time hereby is represented in seconds.
     * </p>
     * 
     * <p>
     * Format examples:
     * </p>
     * <ul>
     * <li>30</li>
     * <li>30s</li>
     * <li>5m</li>
     * <li>1h</li>
     * </ul>
     */
    private class TimeStringConverter extends StringConverter<Integer> {

        /** {@inheritDoc} */
        @Override
        public Integer fromString(String value) {
            if (value.isEmpty())
                return 0;

            Pattern pattern = Pattern.compile("(\\d+)(s|m|h{0,1})");
            Matcher matcher = pattern.matcher(value);

            if (matcher.matches()) {
                BigInteger num = new BigInteger(matcher.group(1));
                switch (matcher.group(2)) {
                case "s":
                    // no change
                    break;
                case "m":
                    num = num.multiply(BigInteger.valueOf(60));
                    break;
                case "h":
                    num = num.multiply(BigInteger.valueOf(1800));
                    break;
                default:
                    break;
                }
                // prevent integer overflow
                return num.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) < 0 ? num.intValue() : Integer.MAX_VALUE;
            }

            return 0;
        }

        /** {@inheritDoc} */
        @Override
        public String toString(Integer value) {
            if (value == 0)
                return "0";

            if (value % 1800 == 0) {
                return (value / 1800) + "h";
            } else if (value % 60 == 0) {
                return (value / 60) + "m";
            }

            return value + "s";
        }
    }
}