/*
 * Created 17-Jul-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.control;

import com.google.common.collect.Lists;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.UUID;
import lombok.BoundSetter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.java.Log;
import uk.me.fommil.utils.ObservableCollection;
import uk.me.fommil.utils.ObservableMap;
import uk.me.fommil.utils.ObservableSet;
import uk.me.fommil.zibaldone.Importer;
import uk.me.fommil.zibaldone.Relator;
import uk.me.fommil.zibaldone.Tag;
import uk.me.fommil.zibaldone.control.TagController.TagChoice;

/**
 * Keeps all the persistent user settings in one place.
 * <p>
 * Note that although this is considered to be the <i>de facto</i> repository
 * of all current settings, it is not the official source for change events.
 * Change support is added here so that the settings can be persisted. Objects
 * wishing to be informed of changes should register with the appropriate
 * controller.
 *
 * @author Samuel Halliday
 * @see Listeners
 */
@Getter
@EqualsAndHashCode
@ToString
@Log
public class Settings implements Serializable {

    // add transient to 
    private final transient PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    @BoundSetter
    @Deprecated // with preference for sparsity value
    private int connections = 500;

    @BoundSetter
    private String search = "";

    private final ObservableMap<Tag, TagChoice> selectedTags = ObservableMap.newObservableTreeMap();

    private final ObservableSet<UUID> selectedBunches = ObservableSet.newObservableHashSet();

    // TODO: careful about persistence - Importer is mutable
    private final ObservableMap<UUID, Importer> importers = ObservableMap.newObservableHashMap();

    // TODO: careful about persistence - Relators can be mutable
    private final ObservableCollection<Relator> relators = ObservableCollection.newObservableCollection(Lists.<Relator>newArrayList());

    {
        log.info("STARTING UP");
        ObservableMap.propertyChangeAdapter(getPropertyChangeSupport(), selectedTags, "selectedTags");
        ObservableCollection.propertyChangeAdapter(getPropertyChangeSupport(), selectedBunches, "selectedBunches");
        ObservableMap.propertyChangeAdapter(getPropertyChangeSupport(), importers, "importers");
        ObservableCollection.propertyChangeAdapter(getPropertyChangeSupport(), relators, "relators");
    }

    public static void main(String[] args) {
        Settings settings = new Settings();
        settings.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                log.info("PROPERTY CHANGE " + evt.getPropertyName() + " to " + evt.getNewValue());
            }
        });
        settings.setSearch("superman");
        settings.setConnections(100);
        settings.getSelectedBunches().add(UUID.randomUUID());
        settings.getImporters().clear();
    }
}