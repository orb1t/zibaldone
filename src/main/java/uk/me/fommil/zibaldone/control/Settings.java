/*
 * Created 17-Jul-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.control;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.thoughtworks.xstream.XStream;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.UUID;
import java.util.logging.Level;
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
 * Change support is added here so that the settings can be persisted
 * (for most changes).
 * Objects wishing to be informed of changes should register with the appropriate
 * controller.
 *
 * @author Samuel Halliday
 * @see Listeners
 */
@Getter
@EqualsAndHashCode
@ToString
@Log
public class Settings {

    @BoundSetter
    @Deprecated // with preference for sparsity value
    private int connections = 500;

    @BoundSetter
    private String search = "";

    private final ObservableMap<Tag, TagChoice> selectedTags = ObservableMap.newObservableTreeMap();

    private final ObservableSet<UUID> selectedBunches = ObservableSet.newObservableHashSet();

    private final ObservableMap<UUID, Importer> importers = ObservableMap.newObservableHashMap();

    private final ObservableCollection<Relator> relators = ObservableCollection.newObservableCollection(Lists.<Relator>newArrayList());

    {
        ObservableMap.propertyChangeAdapter(getPropertyChangeSupport(), selectedTags, "selectedTags");
        ObservableCollection.propertyChangeAdapter(getPropertyChangeSupport(), selectedBunches, "selectedBunches");
        ObservableMap.propertyChangeAdapter(getPropertyChangeSupport(), importers, "importers");
        ObservableCollection.propertyChangeAdapter(getPropertyChangeSupport(), relators, "relators");

        // we could potentially add a timer to check if any mutable Map values change without us knowing
        // but that would involve dictating that they implement hashCode/equals. Best handle that as a
        // special case.
    }

    // JAXB is more powerful and quicker, but XStream is a lot easier to use
    // http://www.oracle.com/technetwork/articles/javase/index-140168.html
    // http://jaxb.java.net/tutorial/
    private static final ThreadLocal<XStream> xstreams = new ThreadLocal<XStream>() {
        @Override
        protected XStream initialValue() {
            XStream xstream = new XStream();
            xstream.alias("settings", Settings.class);
            xstream.alias("uuid", UUID.class);
            xstream.alias("tag", Tag.class);
            xstream.alias("tagchoice", TagChoice.class);
            xstream.alias("importer", Importer.class);
            xstream.alias("relator", Relator.class);

            // BUG: https://github.com/peichhorn/lombok-pg/issues/118
            xstream.omitField(ObservableCollection.class, "$registeredCollectionListener");
            xstream.omitField(ObservableMap.class, "$registeredMapListener");
            xstream.omitField(Settings.class, "$propertyChangeSupport");
            xstream.omitField(Settings.class, "$propertyChangeSupportLock");
//            try {
//                Field field = Settings.class.getDeclaredField("$propertyChangeSupportLock");
//                log.info("modifiers = " + Modifier.toString(field.getModifiers()));
//
//            } catch (Exception e) {
//                log.log(Level.WARNING, "Reflection FAIL", e);
//            }

            return xstream;
        }
    };

    /**
     * @param file
     * @return 
     */
    public static Settings load(File file) {
        Preconditions.checkNotNull(file);
        Preconditions.checkArgument(file.exists());
        XStream xstream = xstreams.get();
        return (Settings) xstream.fromXML(file);
    }

    /**
     * @param file
     * @throws IOException 
     */
    public void save(File file) throws IOException {
        Preconditions.checkNotNull(file);
        XStream xstream = xstreams.get();
        String xml = xstream.toXML(this);
        Files.write(xml, file, Charsets.UTF_8);
    }

    public static Settings loadAutoSavingInstance(final File file) {
        Preconditions.checkNotNull(file);
        final Settings settings = new Settings();
        if (file.exists()) {
            Settings loaded = load(file);
            // it's this or implement readResolve
            settings.connections = loaded.connections;
            settings.importers.putAll(loaded.importers);
            settings.relators.addAll(loaded.relators);
            settings.search = loaded.search;
            settings.selectedBunches.addAll(loaded.selectedBunches);
            settings.selectedTags.putAll(loaded.selectedTags);
        }

        settings.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                try {
                    log.fine("Saving " + file.getName() + ": " + evt.getPropertyName());
                    settings.save(file);
                } catch (IOException e) {
                    log.log(Level.WARNING, "Problem saving " + file.getName(), e);
                }
            }
        });
        return settings;
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