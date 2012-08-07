/*
 * Created 08-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import java.io.Serializable;
import java.util.Set;
import java.util.UUID;
import javax.persistence.*;
import lombok.Data;

/**
 * Synonyms between {@link Tag}s, i.e. tags that are not byte-for-byte the
 * same but should be treated as the same.
 * <p>
 * Synonyms may be user defined but are also automatically created
 * in the {@link Reconciler}. The user may choose to ignore an automatic
 * synonym.
 * 
 * @author Samuel Halliday
 */
@Entity
@Data
public class Synonym implements Serializable {

    @Id
    private UUID id = UUID.randomUUID();

    @Column
    @Enumerated(EnumType.STRING)
    private Context context;

    /** Required by automatically created (and ignored) instances */
    @Column(nullable = true)
    private Tag stem;

    /** Not needed for ignored instances */
    @ElementCollection
    private Set<Tag> tags = Sets.newTreeSet();

    /**
     * Defines the context of {@link Synonym} instances.
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
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Synonym) || id == null) {
            return false;
        }
        Synonym other = (Synonym) obj;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        Preconditions.checkNotNull(id, "id must be set before @Entity.hashCode can be called");
        return id.hashCode();
    }
}
