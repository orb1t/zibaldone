/*
 * Created 01-Jun-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.beans;

import java.io.File;
import java.util.logging.Logger;
import javax.swing.JFileChooser;

/**
 * PropertyEditor that brings up a JFileChooser panel to select a File.
 *
 * @author Samuel Halliday
 */
public class FilePropertyEditor extends AbstractSwingPropertyEditor {

    private static final Logger log = Logger.getLogger(FilePropertyEditor.class.getName());

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
