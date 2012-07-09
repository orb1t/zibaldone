/*
 * Created 08-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.Serializable;
import java.util.Set;
import javax.persistence.Embeddable;
import lombok.Data;

/**
 * Provided as a type safe alternative to {@link String}.
 *
 * @author Samuel Halliday
 */
@Embeddable
@Data
public class Tag implements Serializable, Comparable<Tag> {

    private String text;

    /**
     * @param strings
     * @return
     * @see #asTags(Iterable)
     */
    public static Set<Tag> asTags(String... strings) {
        return asTags(Lists.newArrayList(strings));
    }

    /**
     * @param strings
     * @return trimmed, lowercase, sorted with duplicates removed
     */
    public static Set<Tag> asTags(Iterable<String> strings) {
        Set<Tag> tags = Sets.newTreeSet();
        for (String string : strings) {
            Tag tag = new Tag();
            tag.setText(string.trim().toLowerCase());
            tags.add(tag);
        }
        return tags;
    }

    @Override
    public int compareTo(Tag o) {
        return text.compareTo(o.getText());
    }
}
