/*
 * Created 08-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.Serializable;
import java.util.Set;
import javax.persistence.Embeddable;

/**
 * Provided as a type safe alternative to {@link String}.
 *
 * @author Samuel Halliday
 */
@Embeddable
public class Tag implements Serializable {

    /**
     * serial version 1
     */
    public static final long serialVersionUID = 1L;

    private String text;

    /**
     *
     */
    public Tag() {
    }

    /**
     *
     * @param strings
     * @return
     */
    public static Set<Tag> asTags(String ... strings) {
        return asTags(Lists.newArrayList(strings));
    }

    /**
     *
     * @param strings
     * @return
     */
    public static Set<Tag> asTags(Iterable<String> strings) {
        Set<Tag> tags = Sets.newHashSet();
        for (String string : strings) {
            Tag tag = new Tag();
            tag.setText(string.trim());
            tags.add(tag);
        }
        return tags;
    }

    @Override
    public boolean equals(Object obj) {
        // <editor-fold defaultstate="collapsed" desc="boilerplate identity, instanceof and cast">
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Tag)) {
            return false;
        }
        final Tag other = (Tag) obj;// </editor-fold>
        return Objects.equal(text, other.text);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(text);
    }

    @Override
    public String toString() {
        return text;
    }

    // <editor-fold defaultstate="collapsed" desc="BOILERPLATE GETTERS/SETTERS">
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    // </editor-fold>        
}
