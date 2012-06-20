/*
 * Created 20-Jun-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.beans;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditorManager;
import java.beans.SimpleBeanInfo;
import java.io.File;
import java.util.Date;
import java.util.logging.Logger;
import javax.swing.JFrame;
import uk.me.fommil.beans.editors.DatePropertyEditor;
import uk.me.fommil.beans.editors.FilePropertyEditor;

/**
 * Interactive demo.
 * 
 * @author Samuel Halliday
 */
public class JBeanEditorTest {

    private static final Logger log = Logger.getLogger(JBeanEditorTest.class.getName());

    public static void main(String[] args) {
        PropertyEditorManager.registerEditor(File.class, FilePropertyEditor.class);
        PropertyEditorManager.registerEditor(Date.class, DatePropertyEditor.class);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JBeanEditor editor = new JBeanEditor();
        BeanHelper ender = new BeanHelper(new Object() {

            private File file;

            private Boolean button = false;

            private String name = "text";

//            private Color colour;
            private Date date;

            // <editor-fold defaultstate="collapsed" desc="BOILERPLATE GETTERS/SETTERS">
            public String getName() {
                return name;
            }

            public Date getDate() {
                return date;
            }

            public void setDate(Date date) {
                this.date = date;
            }

            public File getFile() {
                return file;
            }

            public void setFile(File file) {
                this.file = file;
            }

            public Boolean getButton() {
                return button;
            }

            public void setButton(Boolean button) {
                this.button = button;
            }

            public void setName(String name) {
                this.name = name;
            }
            // </editor-fold>
        }, new SimpleBeanInfo() {
//            @Override
//            public Image getIcon(int iconKind) {
//                String logo = "http://docs.oracle.com/javase/6/docs/technotes/guides/deployment/deployment-guide/upgrade-guide/images/java_logo.gif";
//                try {
//                    return ImageIO.read(new URL(logo));
//                } catch (IOException ex) {
//                }
//                return null;
//            }
        });
        ender.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                log.info("Received Change: " + evt.getNewValue());
            }
        });
//        ender.addVetoableChangeListener(new VetoableChangeListener() {
//
//            @Override
//            public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
//                log.info("Received VetoableChange: " + evt.getNewValue());
//                throw new PropertyVetoException("No", evt);
//            }
//        });

        editor.setBeanHelper(ender);
        frame.add(editor, BorderLayout.CENTER);
        frame.setSize(600, 400);
        frame.pack();
        frame.setVisible(true);
    }
}
