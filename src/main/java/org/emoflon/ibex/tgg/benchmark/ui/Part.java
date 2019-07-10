package org.emoflon.ibex.tgg.benchmark.ui;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * Part is the abstract base class for GUI FXML parts. It loads a FXML resource
 * and acts as its controller.
 *
 * @author Andre Lehmann
 * @version 1.0
 * @since 2019-07-09
 */
public abstract class Part {

	protected Parent content;
	protected URL resourcePath;

	/**
	 * Constructor for {@link Part}.
	 */
	public Part() {	}

	/**
	 * Constructor for {@link Part}.
	 * 
	 * @param resourcePath The path of the FXML resource
	 * @throws IOException if the FXML resource couldn't be found
	 */
	public Part(String resourcePath) throws IOException {
		// resolve the resource path
		this.resourcePath = getClass().getResource(resourcePath);
		if (this.resourcePath == null)
			throw new IOException(String.format("Resource not found: '%s'", resourcePath));

		// load the FXML resource
		FXMLLoader loader = new FXMLLoader(this.resourcePath);
		loader.setController(this);
		this.content = loader.load();
	}

	/**
	 * @return the content
	 */
	public Parent getContent() {
		return content;
	}

	/**
	 * @return the resourcePath
	 */
	public URL getResourcePath() {
		return resourcePath;
	}
}
