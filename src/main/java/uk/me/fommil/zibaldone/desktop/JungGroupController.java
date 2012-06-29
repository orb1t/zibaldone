/*
 * Created 29-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import edu.uci.ics.jung.graph.ObservableGraph;
import java.util.List;
import uk.me.fommil.zibaldone.Group;
import uk.me.fommil.zibaldone.Note;

/**
 * A specialist MVC Controller for dealing with {@link Group}s.
 * 
 * @author Samuel Halliday
 */
public class JungGroupController {

    private final ObservableGraph<Note, Double> graph;

    /**
     * @param graph
     */
    public JungGroupController(ObservableGraph<Note, Double> graph) {
        this.graph = graph;
    }

    /**
     * @return
     */
    public List<String> getGroupNames() {
        // TODO: implement method
        throw new UnsupportedOperationException("not implemented yet");
    }
    
    // TODO: perhaps expose the Group object
    
    /**
     * @param name
     */
    public void addGroup(String name) {
        // TODO: implement method
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * @param name
     */
    public void removeGroup(String name) {
        // TODO: implement method
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * @param name of group
     * @param note
     */
    public void addToGroup(String name, Note note) {
        // TODO: implement method
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * @param name of cluster
     * @param note
     */
    public void removeFromGroup(String name, Note note) {
        // TODO: implement method
        throw new UnsupportedOperationException("not implemented yet");
    }
}
