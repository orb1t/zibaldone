/*
 * Created 13-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.relator;

import org.junit.*;
import static org.junit.Assert.*;
import uk.me.fommil.zibaldone.Note;
import uk.me.fommil.zibaldone.Tag;

/**
 *
 * @author Samuel Halliday
 */
public class TagRelatorTest {

    @Test
    public void testRelate() {
        TagRelator relator = new TagRelator();
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
            assertEquals(1, relator.relate(a, b), 0.01);
        }
    }
}
