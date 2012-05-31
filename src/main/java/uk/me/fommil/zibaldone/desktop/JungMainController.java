/*
 * Created 28-May-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import edu.uci.ics.jung.graph.ObservableGraph;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import uk.me.fommil.zibaldone.Importer;
import uk.me.fommil.zibaldone.Note;
import uk.me.fommil.zibaldone.Relator;

/**
 * An MVC Controller that uses JUNG to organise the core
 * Zibaldone objects (the Model). Most tasks
 * are performed by specialist controllers, obtained here.
 * <p>
 * The View primarily receives asynchronous updates via {@link ObservableGraph}.
 * However, some information is available synchronously through getters/setters.
 * Standard Java objects are preferred in the Controller APIs to simplify
 * the Views and to minimise the risk of incorrect persistence handling.
 * <p>
 * The JUNG graph has {@link Note}s on vertices and {@link Double} values
 * {@code [0, 1]}, as defined in the {@link Relator}, on the edges.
 * 
 * @see JungGraphView
 * @author Samuel Halliday
 */
public class JungMainController {

    private final ObservableGraph<Note, Double> graph;

    private final Settings settings = new Settings();

    /**
     * @param graph the model
     */
    public JungMainController(ObservableGraph<Note, Double> graph) {
        this.graph = graph;
    }

    /**
     * Updates the model based on current settings.
     */
    public void doRefresh() {
        // TODO: implement method
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * Automatically create a cluster, as a starting point.
     */
    public void doAutoCluster() {
        // TODO: implement method
        throw new UnsupportedOperationException("not implemented yet");
    }

    public SynonymController getSynonymController() {
        return new SynonymController();
    }

    public JungClusterController getClusterController() {
        return new JungClusterController(graph);
    }

    public ImporterController getImporterController() {
        return new ImporterController();
    }

    public ExporterController getExporterController() {
        return new ExporterController();
    }

    /**
     * @return all tags with their usage counts
     */
    public Map<String, Integer> getTags() {
        // TODO: implement method
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * Keeps all the settings in one place. Callers are encouraged to
     * edit mutable entries in-place but should immediately call one of
     * the controller's actions.
     * <p>
     * TODO: persist across sessions
     */
    public static class Settings implements Serializable {

        private boolean serendipity, tags = true, content, user;

        private double noise;

        private int seeds;

        private String search = "";

        private final List<String> includeTags = Lists.newArrayList();

        private final List<String> excludeTags = Lists.newArrayList();

        private final List<String> includeClusters = Lists.newArrayList();

        private final ListMultimap<String, Properties> importers = ArrayListMultimap.create();

        // <editor-fold defaultstate="collapsed" desc="BOILERPLATE GETTERS/SETTERS">
        public boolean isContent() {
            return content;
        }

        public void setContent(boolean content) {
            this.content = content;
        }

        public double getNoise() {
            return noise;
        }

        public void setNoise(double noise) {
            Preconditions.checkArgument(noise >= 0 && noise <= 1, noise);
            this.noise = noise;
        }

        public boolean isSerendipity() {
            return serendipity;
        }

        public int getSeeds() {
            return seeds;
        }

        public void setSeeds(int seeds) {
            Preconditions.checkArgument(seeds > 0);
            this.seeds = seeds;
        }

        public void setSerendipity(boolean serendipity) {
            this.serendipity = serendipity;
        }

        public boolean isTags() {
            return tags;
        }

        public void setTags(boolean tags) {
            this.tags = tags;
        }

        public boolean isUser() {
            return user;
        }

        public void setUser(boolean user) {
            this.user = user;
        }

        public void setSearch(String search) {
            Preconditions.checkNotNull(search);
            this.search = search;
        }

        public String getSearch() {
            return search;
        }

        public List<String> getExcludeTags() {
            return excludeTags;
        }

        public List<String> getIncludeTags() {
            return includeTags;
        }

        public List<String> getIncludeClusters() {
            return includeClusters;
        }

        public ListMultimap<String, Properties> getImporters() {
            return importers;
        }
        // </editor-fold>
    }

    public Settings getSettings() {
        return settings;
    }
}
