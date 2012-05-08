/*
 * Copyright Samuel Halliday 2011
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 * The atomic element for users: holds a title, rich text and tags.
 * 
 * @author Samuel Halliday
 */
@Entity
public class Note implements Serializable {

        private static final long serialVersionUID = 1L;

        @Column
        private String title;

        @ElementCollection
        private List<Tag> tags = Lists.newArrayList();

        @Lob
        private String contents;

        @Id
        private Long id;

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

        // <editor-fold defaultstate="collapsed" desc="BOILERPLATE GETTERS/SETTERS">
        public Long getId() {
                return id;
        }

        public void setId(Long id) {
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

        public void setContents(String contents) {
                this.contents = contents;
        }
        // </editor-fold>
}
