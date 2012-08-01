/*
 * Created 30-Jul-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.jung;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

/**
 * Overrides {@link FRLayout} to fix a few bugs in JUNG.
 * 
 * @author Samuel Halliday
 * @deprecated in the hope that JUNG will fix these issues
 */
@Deprecated
public class FRLayoutFixed<V, E> extends FRLayout<V, E> {

    public FRLayoutFixed() {
        // https://sourceforge.net/tracker/?func=detail&aid=3542000&group_id=73840&atid=539119
        super(new UndirectedSparseGraph<V, E>());
    }
//    @Getter @Setter
//    private boolean priority;
//
//    @Override
//    public int hashCode() {
//        // https://sourceforge.net/tracker/?func=detail&aid=3550871&group_id=73840&atid=539119
//        return priority ? 0 : 1;
//    }
//    @Override
//    public void setSize(Dimension size) {
//        super.setSize(size);
//
//        // NOT IMPLEMENTED
//        // https://sourceforge.net/tracker/?func=detail&aid=3553281&group_id=73840&atid=539119
//        // setInitializer(new RandomLocationTransformer<V>(size));
//    }
}
