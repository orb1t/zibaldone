/*
 * Created 19-Jul-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.control;

import com.google.common.base.Preconditions;
import java.util.Map;
import lombok.ListenerSupport;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import uk.me.fommil.zibaldone.Tag;
import uk.me.fommil.zibaldone.control.Listeners.TagListener;

/**
 * Controller for {@link Tag} changes.
 * 
 * @author Samuel Halliday
 */
@RequiredArgsConstructor
@ListenerSupport(TagListener.class)
public class TagController {

    @NonNull
    private final Settings settings;

    public enum TagChoice {

        IGNORE, SHOW, HIDE

    }

    /**
     * @param choice
     * @param tag
     */
    public void selectTag(Tag tag, TagChoice choice) {
        Preconditions.checkNotNull(tag);
        Preconditions.checkNotNull(choice);

        Map<Tag, TagChoice> selected = settings.getSelectedTags();
        boolean change;
        if (choice == TagChoice.IGNORE) {
            change = (selected.remove(tag) != null);
        } else {
            change = (selected.put(tag, choice) != choice);
        }
        if (change) {
            fireTagSelection(tag, choice);
        }
    }
}
