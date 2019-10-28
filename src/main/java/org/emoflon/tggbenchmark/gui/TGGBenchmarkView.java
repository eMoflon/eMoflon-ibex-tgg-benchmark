package org.emoflon.tggbenchmark.gui;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.emoflon.tggbenchmark.Core;
import org.emoflon.tggbenchmark.gui.controller.benchmark_cases_table.BenchmarkCasesTableController;

import javafx.embed.swt.FXCanvas;
import javafx.scene.Scene;

public class TGGBenchmarkView {

    private static final Logger LOG = LogManager.getLogger(Core.PLUGIN_NAME);

    private FXCanvas fxCanvas;
    private BenchmarkCasesTableController mainPart;

    @PostConstruct
    public void createPartControl(Composite parent, IEclipseContext context) {
        // create canvas and initialize FX toolkit
        fxCanvas = new FXCanvas(parent, SWT.NONE);

        // create GUI part
        mainPart = new BenchmarkCasesTableController();

        // add objects to the eclipse context (used for dependency injection)
        context.set("logger", LOG);
        context.set("pluginCore", Core.getInstance());
        context.set("benchmarkCases", Core.getInstance().getBenchmarkCases());
        context.set("benchmarkCasesTable", mainPart.getTable());

        // set scene
        Scene view_scene = new Scene(mainPart.getContent());
        fxCanvas.setScene(view_scene);
    }
}
