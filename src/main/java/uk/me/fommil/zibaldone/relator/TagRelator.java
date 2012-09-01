/*
 * Created 13-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.relator;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.*;
import javax.persistence.EntityManagerFactory;
import lombok.Getter;
import lombok.extern.java.Log;
import uk.me.fommil.utils.Convenience;
import uk.me.fommil.utils.Convenience.Loop;
import uk.me.fommil.zibaldone.Note;
import uk.me.fommil.zibaldone.Relator;

/**
 * Defines the relation between {@link Note} instances based purely on tags.
 * 
 * @author Samuel Halliday
 */
@Log
public class TagRelator implements Relator {

    @Getter
    private final String name = "Tags";

    @Override
    public void refresh(EntityManagerFactory emf) {
    }

    @Override
    public double relate(Note a, Note b) {
        Preconditions.checkNotNull(a);
        Preconditions.checkNotNull(b);
        if (a.getTags().isEmpty() || b.getTags().isEmpty()) {
            return 1;
        }
        if (a.getTags().equals(b.getTags())) {
            return 0;
        }

        int overlapTags = Sets.intersection(a.getTags(), b.getTags()).size();
        if (overlapTags == 0) {
            return 1;
        }
        int totalTags = Sets.union(a.getTags(), b.getTags()).size();

        double overlap = ((double) overlapTags) / totalTags;
        return (1 - overlap);
    }

    @Override
    public Set<Set<Note>> cluster(Collection<Note> notes) {
        Preconditions.checkNotNull(notes);
        final List<Set<Note>> matches = Lists.newArrayList();

        Convenience.upperOuter(notes, new Loop<Note>() {
            @Override
            public void action(Note first, Note second) {
                if (relate(first, second) == 0) {
                    matches.add(Sets.newHashSet(first, second));
                }
            }
        });
        return Convenience.disjointify(matches);
    }
}
