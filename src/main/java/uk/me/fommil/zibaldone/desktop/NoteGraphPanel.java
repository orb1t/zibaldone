/*
 * Created 24-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import javax.swing.JPanel;

/**
 *
 * @author Samuel Halliday
 */
public class NoteGraphPanel extends JPanel {

    /**
     * 
     */
    public NoteGraphPanel() {

        Graph<Integer, String> g = new SparseMultigraph<Integer, String>();
        g.addVertex((Integer) 1);
        g.addVertex((Integer) 2);
        g.addVertex((Integer) 3);
        g.addEdge("Edge-A", 1, 2);
        g.addEdge("Edge-B", 2, 3);

        Layout<Integer, String> layout = new CircleLayout(g);        
        BasicVisualizationServer<Integer, String> vv =
                new BasicVisualizationServer<Integer, String>(layout);

        add(vv);
    }
}
