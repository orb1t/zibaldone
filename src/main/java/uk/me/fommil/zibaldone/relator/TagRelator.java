/*
 * Created 13-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.relator;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.*;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import uk.me.fommil.utils.Convenience;
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
public class TagRelator implements Relator {
    
    private static final Logger log = Logger.getLogger(TagRelator.class.getName());
    
    private final Settings settings = new Settings() {
    };

    // FIXME: tag resolving should be done in a separate object
    // tags that appear in a Synonym are resolved to an arbitrary tag
    private final transient Map<Tag, Tag> resolve = Maps.newHashMap();
    
    private Set<Tag> resolve(Set<Tag> tags) {
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
    public String getName() {
        return "Tags";
    }
    
    @Override
    public Settings getSettings() {
        return settings;
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
            return 1.0;
        }
        int totalTags = Sets.union(aResolved, bResolved).size();
        
        double overlap = ((double) overlapTags) / totalTags;
        return (1 - overlap);
    }
    
    @Override
    public Set<Set<Note>> cluster(Collection<Note> notes) {
        Preconditions.checkNotNull(notes);
        final Set<Set<Note>> clusters = Sets.newHashSet();

        // TODO: use a machine learning clustering algorithm based on the
        // metric and make it an abstract implementation

        Convenience.upperOuter(Lists.newArrayList(notes), new Convenience.Loop<Note>() {
            
            @Override
            public void action(Note first, Note second) {
                Set<Note> active = null;
                for (Set<Note> cluster : clusters) {
                    if (cluster.contains(first)) {
                        active = cluster;
                        break;
                    }
                }
                if (active == null) {
                    active = Sets.newHashSet();
                    clusters.add(active);
                }
                double ds2 = relate(first, second);
                // This only works because exact equality is transitive.
                // For example, it would break for an Epsilon sphere.
                // Indeed, the first loop will create all the valid sets.
                if (ds2 == 0.0) {
                    active.add(second);
                }
            }
        });
        return clusters;
    }
}
