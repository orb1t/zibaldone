/*
 * Created 24-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;

/**
 *
 * @author Samuel Halliday
 */
public class NoteGraphPanel extends VisualizationViewer {

    private final Graph<Integer, String> g;
            
    // AWFUL API requirement
    public NoteGraphPanel(Graph<Integer, String> g) {
        super(new FRLayout(g));
        this.g = g;
        
    }
}
