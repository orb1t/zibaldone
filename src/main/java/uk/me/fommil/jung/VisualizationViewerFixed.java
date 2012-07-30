/*
 * Created 30-Jul-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.jung;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Overrides {@link VisualizationViewer} to fix a few bugs in JUNG.
 * 
 * @author Samuel Halliday
 * @deprecated in the hope that JUNG will fix these issues
 */
@Deprecated
public class VisualizationViewerFixed<V, E> extends VisualizationViewer<V, E> {

    public VisualizationViewerFixed(Layout<V, E> layout) {
        super(layout);

        // https://sourceforge.net/tracker/?func=detail&aid=3551453&group_id=73840&atid=539119
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent ce) {
                getGraphLayout().setSize(getSize());
            }
        });
    }
}
