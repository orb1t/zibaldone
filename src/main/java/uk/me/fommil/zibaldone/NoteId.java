/*
 * Created 11-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone;

import com.google.common.base.Objects;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * A custom key 
 * 
 * @author Samuel Halliday
 */
@Embeddable
public class NoteId implements Serializable {

        /** serial version 1 */
        public static final long serialVersionUID = 1L;

        @Column
        private String source;

        @Column
        private Long id;

        @Override
        public boolean equals(Object obj) {
                if (!(obj instanceof NoteId) || id == null) {
                        return false;
                }
                final NoteId other = (NoteId) obj;
                return Objects.equal(id, other.id) && Objects.equal(source, other.source);
        }

        @Override
        public int hashCode() {
                return Objects.hashCode(id, source);
        }

        @Override
        public String toString() {
                return "NoteKey{" + "source=" + source + ", id=" + id + '}';
        }

        public NoteId() {
        }

        // <editor-fold defaultstate="collapsed" desc="BOILERPLATE GETTERS/SETTERS">
        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public String getSource() {
                return source;
        }

        public void setSource(String source) {
                this.source = source;
        }
        // </editor-fold>
}
