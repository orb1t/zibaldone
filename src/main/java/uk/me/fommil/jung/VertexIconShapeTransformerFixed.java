/*
 * Created 30-Jul-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.jung;

import edu.uci.ics.jung.visualization.decorators.VertexIconShapeTransformer;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import javax.swing.Icon;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.apache.commons.collections15.Transformer;

/**
 * Replaces {@link VertexIconShapeTransformer} to work closely with the
 * icon transformer.
 * 
 * @param <V>
 * @author Samuel Halliday
 * @deprecated in the hope that JUNG fix the bug
 */
@RequiredArgsConstructor
@Deprecated
public class VertexIconShapeTransformerFixed<V> implements Transformer<V, Shape> {

    private final Transformer<V, Icon> vertexIconTransformer;

    // https://sourceforge.net/tracker/?func=detail&aid=3552131&group_id=73840&atid=539119
    @Override
    public Shape transform(final V v) {
        Icon icon = vertexIconTransformer.transform(v);
        int width = icon.getIconWidth();
        int height = icon.getIconHeight();
        Rectangle shape = new Rectangle(width, height);
        // this transformation is a unexpected
        AffineTransform transform = AffineTransform.getTranslateInstance(-width / 2, -height / 2);
        return transform.createTransformedShape(shape);
    }
}
