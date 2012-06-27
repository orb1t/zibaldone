/*
 * Created 11-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone;

import com.google.common.base.Objects;
import java.io.Serializable;
import java.util.UUID;
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
    private UUID source;

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

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    public UUID getSource() {
        return source;
    }

    /**
     * @param source
     */
    public void setSource(UUID source) {
        this.source = source;
    }
    // </editor-fold>
}
