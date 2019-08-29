package org.emoflon.ibex.tgg.benchmark.ui.generic_preferences;

import java.io.IOException;

import org.controlsfx.validation.ValidationSupport;
import org.emoflon.ibex.tgg.benchmark.ui.components.Part;

/**
 * CategoryPart is the abstract base class for categories GUI parts used in
 * {@link GenericPreferencesPart}. It loads a FXML resource and acts as its
 * controller.
 *
 * @author Andre Lehmann
 */
public abstract class CategoryPart<T> extends Part {

    protected ValidationSupport validation;
    protected T preferencesData;

    /**
     * Constructor for {@link CategoryPart}.
     * 
     * @param resourcePath
     * @throws IOException
     */
    public CategoryPart(String resourcePath) throws IOException {
        super(resourcePath);

        // enable validation support
        this.validation = new ValidationSupport();
    }

    /**
     * Initializes the parts elements by binding them to a data model.
     * 
     * @param preferencesData The data model
     */
    public void initData(T preferencesData) {
        this.preferencesData = preferencesData;
    }

    /**
     * @return the validation
     */
    public ValidationSupport getValidation() {
        return validation;
    }
}
