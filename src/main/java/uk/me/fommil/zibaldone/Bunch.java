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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import lombok.Data;

/**
 * Collects {@link Note}s together with additional user data.
 * Called Bunch instead of Group/Grouping because of SQL compatibility
 * and to avoid confusion with machine learning "clusters".
 * 
 * @author Samuel Halliday
 */
@Data
@Entity
public class Bunch implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "BUNCH_ID")
    private Long id;
    
    @Column
    private String title;

    // TODO: investigate how to magically delete references to Notes that are removed
    @ManyToMany(mappedBy = "bunches")
    private Set<Note> notes = Sets.newHashSet();
    
}
