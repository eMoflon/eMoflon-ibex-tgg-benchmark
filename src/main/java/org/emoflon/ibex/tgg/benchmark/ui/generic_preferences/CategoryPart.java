package org.emoflon.ibex.tgg.benchmark.ui.generic_preferences;

import java.io.IOException;

import org.controlsfx.validation.ValidationSupport;

/**
 * CategoryPart is the abstract base class for categories GUI parts used in
 * GenericPreferencesPart. It loads a FXML resource and acts as its controller.
 *
 * @author Andre Lehmann
 * @version 1.0
 * @since 2019-07-09
 */
public abstract class CategoryPart<T> extends Part {

	protected ValidationSupport validationSupport;
	protected T preferencesData;

	/**
	 * Constructor for CategoryPart
	 * 
	 * @param resourcePath
	 * @throws IOException
	 */
	public CategoryPart(String resourcePath) throws IOException {
		super(resourcePath);

		// enable validation support
		this.validationSupport = new ValidationSupport();
	}

	/**
	 * Initializes the parts elements by binding them to a data model.
	 * 
	 * @param preferencesData The data model
	 */
	public void initData(T preferencesData) {
		this.preferencesData = preferencesData;
	}
}
