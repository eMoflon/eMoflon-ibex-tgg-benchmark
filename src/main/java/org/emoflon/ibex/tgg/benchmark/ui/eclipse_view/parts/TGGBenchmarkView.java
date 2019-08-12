package org.emoflon.ibex.tgg.benchmark.ui.eclipse_view.parts;

import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import javafx.embed.swt.FXCanvas;
import javafx.scene.Scene;

public class TGGBenchmarkView {
    private FXCanvas fxCanvas;
    
    private TGGBenchmarkViewPart tggBenchmarkViewPart;

    @PostConstruct
    public void createPartControl(Composite parent) {
        System.out.println("TGG createPartControl");
        
        // create canvas and initialize FX toolkit
        fxCanvas = new FXCanvas(parent, SWT.NONE);

        // create GUI part
        tggBenchmarkViewPart = new TGGBenchmarkViewPart();
//        tggBenchmarkViewPart.initData(benchmarkCasePreferencesList);
    
        // set scene
        Scene view_scene = new Scene(tggBenchmarkViewPart.getContent());
        fxCanvas.setScene(view_scene);
    }
}
