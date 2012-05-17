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
import uk.me.fommil.zibaldone.Synonym;
import uk.me.fommil.zibaldone.Note;
import uk.me.fommil.zibaldone.Tag;

/**
 *
 * @author Samuel Halliday
 */
public class TagRelatorTest {

    private Synonym synonym(String... words) {
        Synonym e = new Synonym();
        Set<Tag> tags = Tag.asTags(words);
        e.setTags(tags);
        e.setContext(Synonym.Context.USER_DEFINED);
        return e;
    }

    @Test
    public void testRelate() {
        List<Synonym> synonyms = Lists.newArrayList();
        synonyms.add(synonym("good", "great", "wonderful"));
        synonyms.add(synonym("bad", "woeful", "terrible"));
        synonyms.add(synonym("ugly", "discusting", "rank"));
        synonyms.add(synonym("good", "bad", "ugly"));
        synonyms.add(synonym("smug", "smugness", "smuggy"));

        TagRelator relator = new TagRelator(synonyms);
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
        List<Synonym> synonyms = Lists.newArrayList();
        synonyms.add(synonym("a", "b"));
        synonyms.add(synonym("b", "c"));
        synonyms.add(synonym("c", "d"));
        synonyms.add(synonym("d", "a"));

        synonyms.add(synonym("e", "f"));
        synonyms.add(synonym("f", "g"));
        synonyms.add(synonym("g", "h"));
        synonyms.add(synonym("h", "a"));

        TagRelator relator = new TagRelator(synonyms);
        {
            Note a = new Note();
            a.setTags(Tag.asTags("a"));
            Note b = new Note();
            b.setTags(Tag.asTags("f"));
            assertEquals(0, relator.relate(a, b), 0);
        }
    }
}
