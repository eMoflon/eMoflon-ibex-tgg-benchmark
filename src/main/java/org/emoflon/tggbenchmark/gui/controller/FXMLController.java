package org.emoflon.tggbenchmark.gui.controller;

import java.io.IOException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.emoflon.tggbenchmark.Core;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * Part is the abstract base class for GUI FXML parts. It loads a FXML resource
 * and acts as its controller.
 *
 * @author Andre Lehmann
 */
public abstract class FXMLController {

    protected static final Logger LOG = LogManager.getLogger(Core.PLUGIN_NAME);

    protected Parent content;
    protected URL resourcePath;

    /**
     * Constructor for {@link FXMLController}.
     */
    public FXMLController() {
    }

    /**
     * Constructor for {@link FXMLController}.
     * 
     * @param resourcePath The path of the FXML resource
     * @throws IOException if the FXML resource couldn't be found
     */
    public FXMLController(String resourcePath) throws IOException {
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
