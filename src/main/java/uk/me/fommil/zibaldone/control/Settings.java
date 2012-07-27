/*
 * Created 17-Jul-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.control;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.Data;
import uk.me.fommil.zibaldone.Importer;
import uk.me.fommil.zibaldone.Relator;
import uk.me.fommil.zibaldone.Tag;
import uk.me.fommil.zibaldone.control.TagController.TagChoice;

/**
 * Keeps all the persistent user settings in one place.
 *
 * TODO: API review to persist across sessions
 * TODO: expose listeners (tricky for Collections: use GlazedLists or Guava Forwarding)
 * 
 * @author Samuel Halliday
 */
@Data
public class Settings {

    @Deprecated // with preference for sparsity value
    private int connections = 500;

    private String search = "";

    private final Map<Tag, TagChoice> selectedTags = Maps.newTreeMap();

    private final Set<Long> selectedBunches = Sets.newLinkedHashSet();

    private final Map<UUID, Importer> importers = Maps.newHashMap();

    private final List<Relator> relators = Lists.newArrayList();

}