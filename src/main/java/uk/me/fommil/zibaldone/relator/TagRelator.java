/*
 * Created 13-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.relator;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.*;
import javax.persistence.EntityManagerFactory;
import lombok.Getter;
import lombok.extern.java.Log;
import uk.me.fommil.utils.Convenience;
import uk.me.fommil.utils.Convenience.Loop;
import uk.me.fommil.zibaldone.Synonym;
import uk.me.fommil.zibaldone.Note;
import uk.me.fommil.zibaldone.Relator;
import uk.me.fommil.zibaldone.Tag;
import uk.me.fommil.zibaldone.persistence.SynonymDao;

/**
 * Defines the relation between {@link Note} instances based purely on tags.
 * 
 * @author Samuel Halliday
 */
@Log
public class TagRelator implements Relator {

    @Getter
    private final Settings settings = new Settings() {
    };

    @Getter
    private final String name = "Tags";

    // FIXME: tag resolving should be done in a separate object
    // tags that appear in a Synonym are resolved to an arbitrary tag
    private final transient Map<Tag, Tag> resolve = Maps.newHashMap();

    private Set<Tag> resolve(Collection<Tag> tags) {
        Set<Tag> resolved = Sets.newHashSet();
        for (Tag tag : tags) {
            resolved.add(resolve(tag));
        }
        return resolved;
    }

    private Tag resolve(Tag tag) {
        if (resolve.containsKey(tag)) {
            return resolve.get(tag);
        }
        return tag;
    }

    @Override
    public void refresh(EntityManagerFactory emf) {
        Preconditions.checkNotNull(emf);
        SynonymDao dao = new SynonymDao(emf);
        Collection<Synonym> synonyms = dao.readActive().values();
        refresh(synonyms);
    }

    @VisibleForTesting
    void refresh(Collection<Synonym> synonyms) {
        resolve.clear();
        Set<Set<Tag>> bags = Sets.newHashSet();
        for (Synonym e : synonyms) {
            Set<Tag> tags = e.getTags();
            bags.add(tags);
        }
        for (Set<Tag> bag : Convenience.disjointify(bags)) {
            Tag resolved = Iterables.get(bag, 0);
            for (Tag tag : bag) {
                resolve.put(tag, resolved);
            }
        }
    }

    @Override
    public double relate(Note a, Note b) {
        Preconditions.checkNotNull(a);
        Preconditions.checkNotNull(b);
        Set<Tag> aResolved = resolve(a.getTags());
        Set<Tag> bResolved = resolve(b.getTags());
        if (aResolved.isEmpty() || bResolved.isEmpty()) {
            return 1;
        }
        if (aResolved.equals(bResolved)) {
            return 0;
        }

        int overlapTags = Sets.intersection(aResolved, bResolved).size();
        if (overlapTags == 0) {
            return 1;
        }
        int totalTags = Sets.union(aResolved, bResolved).size();

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
