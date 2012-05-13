/*
 * Created 08-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import javax.persistence.*;

/**
 * Equivalences between {@link Tag}s, i.e. tags that are not byte-for-byte the
 * same but should be treated as the same.
 * <p>
 * Equivalences may be user defined but are also automatically created
 * in the {@link Reconciler}. The user may choose to ignore an automatic
 * equivalence.
 * 
 * @author Samuel Halliday
 */
@Entity
public class Equivalence implements Serializable {

    /** serial version 1 */
    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column
    @Enumerated(EnumType.STRING)
    private Context context;

    /** Required by automatically created (and ignored) instances */
    @Column(nullable = true)
    private Tag stem;

    /** Not needed for ignored instances */
    @ElementCollection
    private Set<Tag> tags = Sets.newHashSet();

    /**
     * Defines the context of {@link Equivalence} instances.
     */
    public static enum Context {

        /** The user has defined the instance */
        USER_DEFINED,
        /** The instance was automatically created, e.g. by token stemming */
        AUTOMATIC,
        /** The instance was automatically created but the user has chosen to ignore it */
        AUTOMATIC_IGNORED

    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Equivalence) || id == null) {
            return false;
        }
        final Equivalence other = (Equivalence) obj;
        return Objects.equal(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return context + (stem == null ? "" : ":" + stem.getText()) + ":" + tags;
    }

    // <editor-fold defaultstate="collapsed" desc="BOILERPLATE GETTERS/SETTERS">
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public Tag getStem() {
        return stem;
    }

    public void setStem(Tag stem) {
        this.stem = stem;
    }
    // </editor-fold>
}
