/*
 * Created 01-Jun-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.beans;

import com.google.common.base.Preconditions;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.beans.*;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import org.jdesktop.swingx.JXTable;
import uk.me.fommil.beans.JavaEnder.Property;

/**
 * An automatically-generated Swing Form for editing arbitrary objects
 * matching the JavaBeans get/set pattern.
 * Unless documented here, the JavaBeans API and surrounding ecosystem is ignored.
 * <p>
 * This class was created in order to provide a light-weight,
 * framework-independent, simple editor to do a lot of boilerplate work in
 * GUI design. Existing implementations require the installation of huge
 * frameworks or commercial licences.
 * <p>
 * Editing of properties is supported at runtime through the global provision of
 * a suitable {@link PropertyEditor} to the {@link PropertyEditorManager}, or
 * for a specific property by setting the value to be returned by
 * {@link PropertyDescriptor#createPropertyEditor(Object)} from the
 * {@link BeanInfo}.
 * <p>
 * If the JavaBean implements {@link BeanInfo}, or one is provided
 * through {@link #setEnder(JavaEnder)}, then it will be used for the following:
 * <ul>
 * <li>{@link BeanInfo#getIcon(int)} is used, if available.</li>
 * <li>{@link PropertyDescriptor#isHidden()} is respected.</li>
 * <li>{@link PropertyDescriptor#isExpert()} will produce a read-only entry
 * (a useful interpretation of a vague API).</li>
 * </ul>
 * TODO: Convenience methods are provided to make it easy to use these features.
 * <p>
 * This is not capable of detecting changes made to the
 * underlying bean by others, so a call to {@link #refresh()} is recommended
 * if changes are made.
 * 
 * @see <a href="http://stackoverflow.com/questions/10840078">Question on Stack Overflow</a>
 * @author Samuel Halliday
 */
public final class JavabeansEditorForm extends JPanel {

    private static final Logger log = Logger.getLogger(JavabeansEditorForm.class.getName());

    /** @param args */
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JavabeansEditorForm editor = new JavabeansEditorForm();
        JavaEnder ender = new JavaEnder(new Object() {

            private String name = "test";

            // <editor-fold defaultstate="collapsed" desc="BOILERPLATE GETTERS/SETTERS">
            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
            // </editor-fold>
        });
        ender.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                log.info("Received Change: " + evt.getNewValue());
            }
        });
        ender.addVetoableChangeListener(new VetoableChangeListener() {

            @Override
            public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
                log.info("Received VetoableChange: " + evt.getNewValue());
                throw new PropertyVetoException("No", evt);
            }
        });

        editor.setEnder(ender);
        frame.add(editor, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }
    private volatile JavaEnder ender;

    private final JXTable table = new JXTable();

    public JavabeansEditorForm() {
        super();
        setLayout(new FlowLayout());

        table.setTableHeader(null);

        JScrollPane scroll = new JScrollPane(table);
        add(scroll);
    }

    public void refresh() {
        final List<Property> properties = ender.getProperties();

        table.setModel(new AbstractTableModel() {

            @Override
            public int getRowCount() {
                return properties.size();
            }

            @Override
            public int getColumnCount() {
                return 2;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                Preconditions.checkArgument(rowIndex >= 0 && rowIndex < getRowCount());
                Preconditions.checkArgument(columnIndex >= 0 && columnIndex < getColumnCount());
                switch(columnIndex) {
                    case 0: return properties.get(rowIndex).getName();
                    default: return properties.get(rowIndex).getValue();
                }
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                Preconditions.checkArgument(rowIndex >= 0 && rowIndex < getRowCount());
                if (columnIndex == 0) {
                    return false;
                }
                return !properties.get(rowIndex).isExpert();
            }
        });

        invalidate();
    }

    /**
     * @param bean
     */
    public void setBean(Object bean) {
        Preconditions.checkNotNull(bean);
        setEnder(new JavaEnder(bean));
    }

    /**
     * @param ender
     */
    public void setEnder(JavaEnder ender) {
        Preconditions.checkNotNull(ender);
        this.ender = ender;
        refresh();
    }
}
