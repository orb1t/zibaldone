/*
 * Copyright Samuel Halliday 2011
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import javax.persistence.*;
import lombok.*;
import lombok.extern.java.Log;

/**
 * The atomic element for users: holds a title, rich text and tags.
 * 
 * @author Samuel Halliday
 */
@Entity
@Data
@Log
@ToString(exclude = "contents")
public class Note implements Serializable {

    private static final int CONTENTS_MAX = 8192;

    @Column
    private UUID source;

    @Id
    private UUID id = UUID.randomUUID();

    @Column
    private String title;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Tag> tags = Sets.newTreeSet();

    @Lob
    @Column(length = CONTENTS_MAX)
    @Basic(fetch = FetchType.EAGER)
    private String contents;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTime;

    public void setContents(String contents) {
        if (contents.length() > CONTENTS_MAX) {
            log.warning("Cutting contents of " + toString());
            this.contents = contents.substring(0, CONTENTS_MAX);
        } else {
            this.contents = contents;
        }
    }

    /**
     * @param other
     * @return true if all the properties are the same as this
     */
    public boolean propertiesEquals(Note other) {
        Preconditions.checkNotNull(other);
        if (this == other) {
            return true;
        }
        return Objects.equal(source, other.source)
                && Objects.equal(title, other.title)
                && Objects.equal(tags, other.tags)
                && Objects.equal(contents, other.contents)
                && Objects.equal(dateTime, other.dateTime);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Note) || id == null) {
            return false;
        }
        Note other = (Note) obj;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        Preconditions.checkNotNull(id, "id must be set before @Entity.hashCode can be called");
        return id.hashCode();
    }
}
