/*
 * Copyright Samuel Halliday 2011
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone;

import com.google.common.collect.Sets;
import java.io.Serializable;
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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String title;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Tag> tags = Sets.newTreeSet();

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

    // http://stackoverflow.com/questions/11604370
    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Note)) {
            return false;
        }
        final Note other = (Note) o;
        if (!other.canEqual((java.lang.Object) this)) {
            return false;
        }
        if (this.getSource() == null ? other.getSource() != null : !this.getSource().equals((java.lang.Object) other.getSource())) {
            return false;
        }
        if (this.getId() == null ? other.getId() != null : !this.getId().equals((java.lang.Object) other.getId())) {
            return false;
        }
        if (this.getTitle() == null ? other.getTitle() != null : !this.getTitle().equals((java.lang.Object) other.getTitle())) {
            return false;
        }
        if (this.getTags() == null ? other.getTags() != null : !this.getTags().equals((java.lang.Object) other.getTags())) {
            return false;
        }
        if (this.getContents() == null ? other.getContents() != null : !this.getContents().equals((java.lang.Object) other.getContents())) {
            return false;
        }
        return true;
    }

    @java.lang.SuppressWarnings("all")
    public boolean canEqual(final java.lang.Object other) {
        return other instanceof Note;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = result * PRIME + (this.getSource() == null ? 0 : this.getSource().hashCode());
        result = result * PRIME + (this.getId() == null ? 0 : this.getId().hashCode());
        result = result * PRIME + (this.getTitle() == null ? 0 : this.getTitle().hashCode());
//        result = result * PRIME + (this.getTags() == null ? 0 : this.getTags().hashCode());
        result = result * PRIME + (this.getContents() == null ? 0 : this.getContents().hashCode());
        return result;
    }
}
