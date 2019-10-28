package org.emoflon.tggbenchmark.gui.controller.generic_preferences;

import java.io.IOException;

import org.controlsfx.validation.ValidationSupport;
import org.emoflon.tggbenchmark.gui.controller.FXMLController;

/**
 * CategoryPart is the abstract base class for categories GUI parts used in
 * {@link GenericPreferencesController}. It loads a FXML resource and acts as
 * its controller.
 *
 * @author Andre Lehmann
 */
public abstract class CategoryController<T> extends FXMLController {

    protected ValidationSupport validation;
    protected T preferencesData;

    /**
     * Constructor for {@link CategoryController}.
     * 
     * @param resourcePath
     * @throws IOException
     */
    public CategoryController(String resourcePath) throws IOException {
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
