package org.emoflon.ibex.tgg.benchmark.ui.eclipse_view.parts;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.emoflon.ibex.tgg.benchmark.Core;

import javafx.embed.swt.FXCanvas;
import javafx.scene.Scene;

public class TGGBenchmarkView {

    private static final Logger LOG = LogManager.getLogger(Core.PLUGIN_NAME);
    
    private FXCanvas fxCanvas;
    private TGGBenchmarkViewPart tggBenchmarkViewPart;

    @PostConstruct
    public void createPartControl(Composite parent, IEclipseContext context) {
        // create canvas and initialize FX toolkit
        fxCanvas = new FXCanvas(parent, SWT.NONE);

        // create GUI part
        tggBenchmarkViewPart = new TGGBenchmarkViewPart();
        
        // add objects to the eclipse context
        context.set("logger", LOG);
        context.set("pluginCore", Core.getInstance());
        context.set("benchmarkCases", Core.getInstance().getBenchmarkCases());
        context.set("benchmarkCasesTable", tggBenchmarkViewPart.getTable());
    
        // set scene
        Scene view_scene = new Scene(tggBenchmarkViewPart.getContent());
        fxCanvas.setScene(view_scene);
    }
}
