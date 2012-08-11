/*
 * Created 17-Jul-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.control;

import java.util.EventListener;
import java.util.Set;
import uk.me.fommil.zibaldone.Bunch;
import uk.me.fommil.zibaldone.Note;
import uk.me.fommil.zibaldone.Tag;
import uk.me.fommil.zibaldone.control.TagController.TagChoice;

/**
 * Custom listeners that are made available by the controller.
 * 
 * @author Samuel Halliday
 */
public final class Listeners {

    public final static class ClusterId {
    }

    public interface BunchListener extends EventListener {

        public void bunchAdded(Bunch bunch);

        public void bunchRemoved(Bunch bunch);

        public void bunchUpdated(Bunch bunch);

        public void bunchSelectionChanged(Bunch bunch, TagChoice choice);
    }

    /**
     * Clusters are indicators for {@link Note}s to be shown visually close to
     * each other. The clusters are dictated by the interactive
     * user settings and can change frequently. A cluster does
     * not indicate {@link Bunch} membership.
     */
    public interface ClusterListener extends EventListener {

        public void clusterAdded(ClusterId id, Set<Note> newCluster);

        public void clusterRemoved(ClusterId id);

        public void clusterUpdated(ClusterId id, Set<Note> updatedCluster);
    }

    /**
     * Tags tend to be in bulk.
     */
    public interface TagListener extends EventListener {

        public void tagsAdded(Set<Tag> tags);

        public void tagsRemoved(Set<Tag> tags);

        public void tagSelection(Tag tag, TagChoice choice);
    }

    /**
     * Notes tend to be updated in bulk.
     */
    public interface NoteListener extends EventListener {

        public void notesChanged(Set<Note> note);
    }

    /**
     * The search input bar.
     */
    public interface SearchListener {

        public void searchChanged(String term);
    }
}
