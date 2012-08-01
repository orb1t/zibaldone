/*
 * Created 29-Jul-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.jung;

import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.Map;

/**
 * Overrides {@link AggregateLayout} to fix a few bugs in JUNG.
 * 
 * @author Samuel Halliday
 * @deprecated in the hope that JUNG will fix these issues
 */
@Deprecated
public class AggregateLayoutFixed<V, E> extends AggregateLayout<V, E> {

    public AggregateLayoutFixed() {
        super(new FRLayoutFixed<V, E>());
    }

    @Override
    public void setSize(Dimension size) {
        // NOT IMPLEMENTED
        // https://sourceforge.net/tracker/?func=detail&aid=3553171&group_id=73840&atid=539122

        // NOT IMPLEMENTED
        // https://sourceforge.net/tracker/?func=detail&aid=3553281&group_id=73840&atid=539119

        // https://sourceforge.net/tracker/?func=detail&aid=3551453&group_id=73840&atid=539119
        Dimension oldSize = getSize();
        super.setSize(size);
        Dimension newSize = getSize();
        if (newSize == null || oldSize == null) {
            return;
        }
        int xOffset = (newSize.width - oldSize.width) / 2;
        int yOffset = (newSize.height - oldSize.height) / 2;
        for (Map.Entry<Layout<V, E>, Point2D> entry : getLayouts().entrySet()) {
            double x = entry.getValue().getX() + xOffset;
            double y = entry.getValue().getY() + yOffset;
            entry.setValue(new Point2D.Double(x, y));
        }
    }
}
