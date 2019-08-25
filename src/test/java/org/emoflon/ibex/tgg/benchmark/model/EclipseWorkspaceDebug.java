package org.emoflon.ibex.tgg.benchmark.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.emoflon.ibex.tgg.benchmark.Core;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;

/**
 * EclipseWorkspaceDebug represents an Eclipse workspace for debugging and
 * testing purposes. It allows easy testing of the plugin without the need to
 * use the Eclipse APIs.
 *
 * @author Andre Lehmann
 */
public class EclipseWorkspaceDebug implements IEclipseWorkspace {

    private static final Logger LOG = LogManager.getLogger(Core.PLUGIN_NAME);
    
    private final ObjectProperty<Path> location;
    private final ListProperty<EclipseTggProject> tggProjects;

    /**
     * Constructor for {@link EclipseWorkspaceDebug}.
     */
    public EclipseWorkspaceDebug() {
        this(Paths.get("../").toAbsolutePath().normalize());
    }

    /**
     * Constructor for {@link EclipseWorkspaceDebug}.
     * 
     * @param workspacePath the path of the workspace to use
     */
    public EclipseWorkspaceDebug(Path workspacePath) {
        LOG.debug("Use custom workspace. Path: " + workspacePath.toString());
        this.location = new SimpleObjectProperty<>(workspacePath);
        this.tggProjects = new SimpleListProperty<EclipseTggProject>();
    }

    /** {@inheritDoc} */
    @Override
    public Path getPluginStateLocation() {
        return getLocation().resolve(".metadata/.plugins/org.emoflon.ibex.tgg.benchmark");
    }

    /** {@inheritDoc} */
    @Override
    public Path getPluginPreferencesFilePath() {
        return getPluginStateLocation().resolve("tgg-benchmark.json");
    }

    public final ObjectProperty<Path> locationProperty() {
        return this.location;
    }
    
    /** {@inheritDoc} */
    @Override
    public final Path getLocation() {
        return this.locationProperty().get();
    }
    

    public final void setLocation(final Path location) {
        this.locationProperty().set(location);
    }

    public final ListProperty<EclipseTggProject> tggProjectsProperty() {
        return this.tggProjects;
    }
    

    @Override
    public final ObservableList<EclipseTggProject> getTggProjects() {
        return this.tggProjectsProperty().get();
    }
    

    public final void setTggProjects(final ObservableList<EclipseTggProject> tggProjects) {
        this.tggProjectsProperty().set(tggProjects);
    }
    
    
}
