package org.emoflon.ibex.tgg.benchmark.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * EclipseJavaProject is a simple representation of an Eclipse Java project.
 *
 * @author Andre Lehmann
 */
public class EclipseJavaProject {

    protected final StringProperty name;
    protected final ObjectProperty<Path> projectPath;
    protected final ObjectProperty<Path> outputPath;
    protected final ListProperty<EclipseJavaProject> referencedProjects;

    /**
     * Constructor for {@link EclipseJavaProject}.
     */
    public EclipseJavaProject(String name, Path projectPath, Path outputPath, Set<EclipseJavaProject> referencedProjects) {
        this.name = new SimpleStringProperty(name);
        this.projectPath = new SimpleObjectProperty<>(projectPath);
        this.outputPath = new SimpleObjectProperty<>(outputPath);
        this.referencedProjects = new SimpleListProperty<EclipseJavaProject>(FXCollections.observableArrayList(referencedProjects));
    }

    @Override
    public String toString() {
        return this.getName();
    }

    /**
     * @return the name property
     */
    public final StringProperty nameProperty() {
        return this.name;
    }
    
    /**
     * @return the project name
     */
    public final String getName() {
        return this.nameProperty().get();
    }
    
    /**
     * @param name the project name to set
     */
    public final void setName(final String name) {
        this.nameProperty().set(name);
    }

    /**
     * @return the class paths for this project
     */
    public Set<Path> getClassPaths() {
        HashSet<Path> classPaths = new HashSet<>();
        classPaths.add(getOutputPath());
        for (EclipseJavaProject refProject : referencedProjects) {
            classPaths.addAll(refProject.getClassPaths());
        }
        return classPaths;
    }
    
    /**
     * @return the class paths for this project as URLs
     */
    public URL[] getClassPathURLs() {
        Set<Path> classPaths = getClassPaths();
        LinkedList<URL> urls = new LinkedList<URL>();
        for (Path path : classPaths) {
            try {
                urls.add(path.toUri().toURL());
            } catch (MalformedURLException e) {
                // ignore
            }
        }
        return urls.toArray(new URL[urls.size()]);
    }

    public final ObjectProperty<Path> projectPathProperty() {
        return this.projectPath;
    }
    

    public final Path getProjectPath() {
        return this.projectPathProperty().get();
    }
    

    public final void setProjectPath(final Path projectPath) {
        this.projectPathProperty().set(projectPath);
    }
    

    public final ObjectProperty<Path> outputPathProperty() {
        return this.outputPath;
    }
    

    public final Path getOutputPath() {
        return this.outputPathProperty().get();
    }
    

    public final void setOutputPath(final Path outputPath) {
        this.outputPathProperty().set(outputPath);
    }
    

    public final ListProperty<EclipseJavaProject> referencedProjectsProperty() {
        return this.referencedProjects;
    }
    

    public final ObservableList<EclipseJavaProject> getReferencedProjects() {
        return this.referencedProjectsProperty().get();
    }
    

    public final void setReferencedProjects(final ObservableList<EclipseJavaProject> referencedProjects) {
        this.referencedProjectsProperty().set(referencedProjects);
    }
}
