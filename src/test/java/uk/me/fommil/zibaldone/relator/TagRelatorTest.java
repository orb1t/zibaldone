/*
 * Created 13-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.relator;

import com.google.common.collect.Lists;
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

    private Equivalence equivalence(String... words) {
        Equivalence e = new Equivalence();
        Set<Tag> tags = Tag.asTags(words);
        e.setTags(tags);
        e.setContext(Equivalence.Context.USER_DEFINED);
        return e;
    }

    @Test
    public void testRelate() {
        List<Equivalence> equivalences = Lists.newArrayList();
        equivalences.add(equivalence("good", "great", "wonderful"));
        equivalences.add(equivalence("bad", "woeful", "terrible"));
        equivalences.add(equivalence("ugly", "discusting", "rank"));
        equivalences.add(equivalence("good", "bad", "ugly"));
        equivalences.add(equivalence("smug", "smugness", "smuggy"));

        TagRelator relator = new TagRelator(equivalences);
        {
            Note a = new Note();
            a.setTags(Tag.asTags("smug", "silly"));
            Note b = new Note();
            b.setTags(Tag.asTags("smug", "silly"));
            assertEquals(0, relator.relate(a, b), 0);
        }
        {
            Note a = new Note();
            a.setTags(Tag.asTags("smug", "silly", "ugly"));
            Note b = new Note();
            b.setTags(Tag.asTags("smug", "silly"));
            assertEquals(0.33, relator.relate(a, b), 0.01);
        }
        {
            Note a = new Note();
            a.setTags(Tag.asTags("smug", "silly", "ugly"));
            Note b = new Note();
            b.setTags(Tag.asTags("good"));
            assertEquals(0.66, relator.relate(a, b), 0.01);
        }
    }

    @Test
    public void testConstructor() {
        // corner case
        List<Equivalence> equivalences = Lists.newArrayList();
        equivalences.add(equivalence("a", "b"));
        equivalences.add(equivalence("b", "c"));
        equivalences.add(equivalence("c", "d"));
        equivalences.add(equivalence("d", "a"));

        equivalences.add(equivalence("e", "f"));
        equivalences.add(equivalence("f", "g"));
        equivalences.add(equivalence("g", "h"));
        equivalences.add(equivalence("h", "a"));

        TagRelator relator = new TagRelator(equivalences);
        {
            Note a = new Note();
            a.setTags(Tag.asTags("a"));
            Note b = new Note();
            b.setTags(Tag.asTags("f"));
            assertEquals(0, relator.relate(a, b), 0);
        }
    }
}
