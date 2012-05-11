/*
 * Copyright Samuel Halliday 2011
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;
import javax.persistence.*;

/**
 * The atomic element for users: holds a title, rich text and tags.
 * 
 * @author Samuel Halliday
 */
@Entity
public class Note implements Serializable {

        private static final Logger log = Logger.getLogger(Note.class.getName());

        private static final long serialVersionUID = 1L;

        private static final int CONTENTS_MAX = 8192;

        @Column
        private String title;

        @ElementCollection
        private List<Tag> tags = Lists.newArrayList();

        @Lob
        @Column(length = CONTENTS_MAX)
        @Basic(fetch = FetchType.LAZY)
        private String contents;

        @EmbeddedId
        private NoteId id;

        @Override
        public boolean equals(Object obj) {
                if (!(obj instanceof Note) || id == null) {
                        return false;
                }
                final Note other = (Note) obj;
                return Objects.equal(id, other.id);
        }

        @Override
        public int hashCode() {
                return Objects.hashCode(id);
        }

        @Override
        public String toString() {
                return Objects.toStringHelper(getClass()).addValue(title).add("tags", tags).toString();
        }

        public void setContents(String contents) {
                if (contents.length() > CONTENTS_MAX) {
                        log.warning("Cutting contents of " + toString());
                        contents = contents.substring(0, CONTENTS_MAX);
                }
                this.contents = contents;
        }

        // <editor-fold defaultstate="collapsed" desc="BOILERPLATE GETTERS/SETTERS">
        public NoteId getId() {
                return id;
        }

        public void setId(NoteId id) {
                this.id = id;
        }

        public String getTitle() {
                return title;
        }

        public void setTitle(String title) {
                this.title = title;
        }

        public List<Tag> getTags() {
                return tags;
        }

        public void setTags(List<Tag> tags) {
                this.tags = tags;
        }

        public String getContents() {
                return contents;
        }
        // </editor-fold>
}
