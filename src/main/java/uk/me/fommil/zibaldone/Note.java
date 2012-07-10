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
    @Column(name = "NOTE_ID")
    private Long id;

    @JoinTable(name = "NOTE_BUNCH",
    joinColumns = {@JoinColumn(name = "NOTE_ID", referencedColumnName = "NOTE_ID")},
    inverseJoinColumns = {@JoinColumn(name = "BUNCH_ID", referencedColumnName = "BUNCH_ID")})
    @ManyToMany
    private Set<Bunch> bunches = Sets.newHashSet();

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
}
