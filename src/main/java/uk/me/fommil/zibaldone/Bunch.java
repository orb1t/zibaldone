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
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import lombok.Data;
import lombok.ToString;
import lombok.extern.java.Log;

/**
 * Collects {@link Note}s together with additional user data.
 * Called Bunch instead of Group/Grouping because of SQL compatibility
 * and to avoid confusion with machine learning "clusters".
 * 
 * @author Samuel Halliday
 */
@Data
@Entity
@Log
@ToString(exclude = "notes")
public class Bunch implements Serializable {

    private static final int CONTENTS_MAX = 8192;

    @Id
    private UUID id = UUID.randomUUID();

    @Column
    private String name;

    // TODO: investigate how to magically delete references
    // from this JoinTable when Notes are removed elsewhere
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Note> notes = Sets.newHashSet();

    @Lob
    @Column(length = CONTENTS_MAX)
    @Basic(fetch = FetchType.EAGER)
    private String contents;

    public void setContents(String contents) {
        if (contents.length() > CONTENTS_MAX) {
            log.warning("Cutting contents of " + toString());
            this.contents = contents.substring(0, CONTENTS_MAX);
        } else {
            this.contents = contents;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Bunch) || id == null) {
            return false;
        }
        Bunch other = (Bunch) obj;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        Preconditions.checkNotNull(id, "id must be set before @Entity.hashCode can be called");
        return id.hashCode();
    }
}
