/*
 * Created 14-Aug-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.control;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.ServiceLoader;
import uk.me.fommil.zibaldone.Exporter;

/**
 * A Controller for implementations of the {@link Exporter} API.
 *
 * @author Samuel Halliday
 */
public class ExporterController {

    /**
     * @return
     */
    public Collection<Exporter> getExporterImplementations() {
        ServiceLoader<Exporter> exporterService = ServiceLoader.load(Exporter.class);
        Collection<Exporter> exporters = Lists.newArrayList();
        for (Exporter exporter : exporterService) {
            exporters.add(exporter);
        }
        return exporters;
    }
}
