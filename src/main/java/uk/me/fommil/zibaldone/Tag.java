/*
 * Created 08-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone;

import com.google.common.collect.Lists;
import java.util.List;

/**
 * Provided as a type safe alternative to {@link String}.
 *
 * @author Samuel Halliday
 */
public class Tag {

        private final String tag;

        /**
         * 
         * @param strings
         * @return
         */
        public static List<Tag> asTags(Iterable<String> strings) {
                List<Tag> tags = Lists.newArrayList();
                for (String string : strings) {
                        tags.add(new Tag(string));
                }
                return tags;
        }

        /**
         * @param tag
         */
        public Tag(String tag) {
                this.tag = tag;
        }
}
