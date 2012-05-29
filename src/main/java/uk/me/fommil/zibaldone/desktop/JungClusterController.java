/*
 * Created 29-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import edu.uci.ics.jung.graph.ObservableGraph;
import java.util.List;
import uk.me.fommil.zibaldone.Cluster;
import uk.me.fommil.zibaldone.Note;

/**
 * A specialist MVC Controller for dealing with {@link Cluster}s.
 * 
 * @author Samuel Halliday
 */
public class JungClusterController {

    private final ObservableGraph<Note, Double> graph;

    /**
     * @param graph
     */
    public JungClusterController(ObservableGraph<Note, Double> graph) {
        this.graph = graph;
    }

    /**
     * @return
     */
    public List<String> getClusterNames() {
        // TODO: implement method
        throw new UnsupportedOperationException("not implemented yet");
    }
    
    /**
     * @param name
     */
    public void addCluster(String name) {
        // TODO: implement method
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * @param name
     */
    public void removeCluster(String name) {
        // TODO: implement method
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * @param name of cluster
     * @param note
     */
    public void addToCluster(String name, Note note) {
        // TODO: implement method
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * @param name of cluster
     * @param note
     */
    public void removeFromCluster(String name, Note note) {
        // TODO: implement method
        throw new UnsupportedOperationException("not implemented yet");
    }
}
