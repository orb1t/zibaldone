/*
 * Created 13-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.relator;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import uk.me.fommil.zibaldone.Equivalence;
import uk.me.fommil.zibaldone.Note;
import uk.me.fommil.zibaldone.Relator;
import uk.me.fommil.zibaldone.Tag;

/**
 * Defines the relation between {@link Note} instances based purely on tags,
 * whilst respecting the {@link Equivalence} rules.
 * <p>
 * Assumes that the provided {@link Equivalence} instances do not change
 * after construction.
 * 
 * @author Samuel Halliday
 */
public class TagRelator implements Relator {

    // tags that appear in an Equivalence are resolved to an arbitrary tag
    private final Map<Tag, Tag> resolve = Maps.newHashMap();

    public TagRelator(List<Equivalence> equivalences) {

        // imagine situation where user-defined equivalences extend
        // each other and automatic ones

        Set<Set<Tag>> bags = Sets.newHashSet();
        for (Equivalence e : equivalences) {
            Set<Tag> tags = e.getTags();
            boolean added = false;
            for (Set<Tag> bag : bags) {
                if (Sets.union(bag, tags).size() > 0) {
                    bag.addAll(tags);
                    added = true;
                }
            }
            if (!added) {
                bags.add(tags);
            }
        }
        
        // gather bags with non-zero unions
        Set<Set<Tag>> distinctBags = Sets.newHashSet();
        for (Set<Tag> bag : bags) {
            for (Set<Tag> otherBag : bags) {
                if (bag == otherBag) {
                    continue;
                }
                
                
            }
        }
        
        
        // TODO: create the resolver map
    }

    @Override
    public double relate(Note a, Note b) {
        Preconditions.checkNotNull(a);
        Preconditions.checkNotNull(b);
        if (a.equals(b)) {
            return 0;
        }




        throw new UnsupportedOperationException("Not supported yet.");
    }

    private Tag resolve(Tag tag) {
        if (resolve.containsKey(tag)) {
            return resolve.get(tag);
        }
        return tag;
    }
}
