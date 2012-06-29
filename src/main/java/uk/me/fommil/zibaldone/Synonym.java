/*
 * Created 08-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone;

import com.google.common.collect.Sets;
import java.io.Serializable;
import java.util.Set;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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
@ToString(includeFieldNames = true)
@EqualsAndHashCode(of = "id")
public class Synonym implements Serializable {

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
}
