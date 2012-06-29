/*
 * Created 11-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone;

import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.Data;

/**
 * A custom key 
 * 
 * @author Samuel Halliday
 * @deprecated there must be a better way to identify Note instances
 */
@Embeddable
@Data
@Deprecated
public class NoteId implements Serializable {

    @Column
    private UUID source;

    @Column
    private Long id;
}
