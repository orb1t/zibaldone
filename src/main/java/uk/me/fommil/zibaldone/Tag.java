/*
 * Created 08-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone;

import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Embeddable;

/**
 * Provided as a type safe alternative to {@link String}.
 *
 * @author Samuel Halliday
 */
@Embeddable
public class Tag implements Serializable {

        /**
         * serial version 1
         */
        public static final long serialVersionUID = 1L;

        private String text;

        /**
         *
         */
        public Tag() {
        }

        /**
         *
         * @param strings
         * @return
         */
        public static List<Tag> asTags(Iterable<String> strings) {
                List<Tag> tags = Lists.newArrayList();
                for (String string : strings) {
                        Tag tag = new Tag();
                        tag.setText(string);
                        tags.add(tag);
                }
                return tags;
        }

        // <editor-fold defaultstate="collapsed" desc="BOILERPLATE GETTERS/SETTERS">
        public String getText() {
                return text;
        }

        public void setText(String text) {
                this.text = text;
        }
        // </editor-fold>        
}
