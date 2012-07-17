/*
 * Created 17-Jul-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.control;

import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.EventListener;
import java.util.Set;
import uk.me.fommil.zibaldone.Bunch;
import uk.me.fommil.zibaldone.Note;
import uk.me.fommil.zibaldone.Tag;

/**
 * Custom listeners that are made available by the controller.
 * 
 * @author Samuel Halliday
 */
public final class Listeners {

    public final static class ClusterId {
    }

    public interface BunchesChangedListener extends EventListener {

        // TODO: fire the change, not the object
        @Deprecated
        public void bunchesChanged(Collection<Bunch> bunches);

        // TODO: fire the change, not the object
        @Deprecated
        public void selectedBunchesChanged(Collection<Bunch> bunches);
    }

    /**
     * Clusters are indicators for {@link Note}s to be shown visually close to
     * each other. The clusters are dictated by the interactive
     * user settings and can change frequently. A cluster does
     * not indicate {@link Bunch} membership.
     */
    public interface ClustersChangedListener extends EventListener {

        public void clusterAdded(ClusterId id, Set<Note> newCluster);

        public void clusterRemoved(ClusterId id);

        public void clusterUpdated(ClusterId id, Set<Note> updatedCluster);
    }

    public interface TagsChangedListener extends EventListener {

        // TODO: fire the change, not the object
        @Deprecated
        public void tagSelectionChanged(Multimap<JungMainController.TagChoice, Tag> selection);

        // TODO: fire the change, not the object
        @Deprecated
        public void tagsChanged(Set<Tag> tags);
    }
}
