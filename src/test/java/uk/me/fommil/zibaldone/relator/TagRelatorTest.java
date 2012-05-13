/*
 * Created 13-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.relator;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import org.junit.*;
import static org.junit.Assert.*;
import uk.me.fommil.zibaldone.Equivalence;
import uk.me.fommil.zibaldone.Note;
import uk.me.fommil.zibaldone.Tag;

/**
 *
 * @author Samuel Halliday
 */
public class TagRelatorTest {
    
    private Equivalence equivalence(String ... words) {
        Equivalence e = new Equivalence();
        Set<Tag> tags = Tag.asTags(Lists.newArrayList(words));
        e.setTags(tags);
        e.setContext(Equivalence.Context.USER_DEFINED);
        return e;
    }
    
    /**
     * Test of relate method, of class TagRelator.
     */
    @Test
    public void testRelate() {
        List<Equivalence> equivalences = Lists.newArrayList();
        equivalences.add(equivalence("good", "great", "wonderful"));
        equivalences.add(equivalence("bad", "woeful", "terrible"));
        equivalences.add(equivalence("ugly", "discusting", "rank"));
        //equivalences.add(equivalence("good", "bad", "ugly"));
        TagRelator instance = new TagRelator(equivalences);

        
    }
}
