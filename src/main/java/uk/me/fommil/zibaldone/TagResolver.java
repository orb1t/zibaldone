/*
 * Created 07-Aug-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import uk.me.fommil.utils.Convenience;

/**
 * Resolves {@link Tag}s based on the {@link Synonym} rules.
 *
 * @author Samuel Halliday
 */
public class TagResolver {

    private final transient Map<Tag, Tag> resolve = Maps.newHashMap();

    /**
     * @param tags
     * @return
     */
    public Set<Tag> resolve(Collection<Tag> tags) {
        Set<Tag> resolved = Sets.newHashSet();
        for (Tag tag : tags) {
            resolved.add(resolve(tag));
        }
        return resolved;
    }

    /**
     * @param tag
     * @return
     */
    public Tag resolve(Tag tag) {
        if (resolve.containsKey(tag)) {
            return resolve.get(tag);
        }
        return tag;
    }

    /**
     * @param synonyms
     */
    public void refresh(Collection<Synonym> synonyms) {
        resolve.clear();
        // TODO: implement the AUTOMATIC_IGNORED logic
        Set<Set<Tag>> bags = Sets.newHashSet();
        for (Synonym e : synonyms) {
            Set<Tag> tags = e.getTags();
            bags.add(tags);
        }
        for (Set<Tag> bag : Convenience.disjointify(bags)) {
            Tag resolved = Iterables.get(Sets.newTreeSet(bag), 0);
            for (Tag tag : bag) {
                resolve.put(tag, resolved);
            }
        }
    }
}
