/*
 * Created 01-Jun-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.beans.editors;

import java.io.File;
import javax.swing.JFileChooser;

/**
 * {@link PropertyEditor} that brings up a {@link JFileChooser}.
 *
 * @author Samuel Halliday
 */
public class FilePropertyEditor extends JPropertyEditor {

    @Override
    public void showEditor() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(false);

        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            setValue(chooser.getSelectedFile());
        }
    }

    @Override
    public String getAsText() {
        File file = (File) getValue();
        return file != null ? file.getName() : "";
    }
}
