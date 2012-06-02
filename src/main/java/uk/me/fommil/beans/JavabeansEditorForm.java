/*
 * Created 01-Jun-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.beans;

import uk.me.fommil.beans.editors.FilePropertyEditor;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.awt.*;
import java.beans.*;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.*;
import org.jdesktop.swingx.JXImagePanel;
import org.jdesktop.swingx.JXTable;
import uk.me.fommil.beans.JavaEnder.Property;
import uk.me.fommil.beans.editors.DatePropertyEditor;

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
 * This is not capable of detecting changes made to the
 * underlying bean by others, so a call to {@link #refresh()} is recommended
 * if changes are made.
 * <ul> 
 * <li>TODO: white text background for default text editor</li>
 * <li>TODO: lose the black background on selection</li>
 * <li>TODO: use the Highlighter to mark rows needing input</li>
 * <li>TODO: Convenience methods are provided to make it easy to use the features.</li>
 * </ul>
 * 
 * @see <a href="http://stackoverflow.com/questions/10840078">Question on Stack Overflow</a>
 * @author Samuel Halliday
 */
public final class JavabeansEditorForm extends JPanel {

    private static final Logger log = Logger.getLogger(JavabeansEditorForm.class.getName());

    /** @param args */
    public static void main(String[] args) {
        PropertyEditorManager.registerEditor(File.class, FilePropertyEditor.class);
        PropertyEditorManager.registerEditor(Date.class, DatePropertyEditor.class);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JavabeansEditorForm editor = new JavabeansEditorForm();
        JavaEnder ender = new JavaEnder(new Object() {

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

        editor.setEnder(ender);
        frame.add(editor, BorderLayout.CENTER);
        frame.setSize(600, 400);
        frame.pack();
        frame.setVisible(true);
    }
    private final JXImagePanel logo;

    // links the table to the ender
    private static class MyTableModel extends AbstractTableModel {

        private final List<Property> properties;

        public MyTableModel(List<Property> properties) {
            this.properties = properties;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            Preconditions.checkArgument(row >= 0 && row < getRowCount());
            Preconditions.checkArgument(col >= 0 && col < getColumnCount());

            properties.get(row).setValue(value);
        }

        @Override
        public int getRowCount() {
            return properties.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        public Class<?> getClassAt(int row, int col) {
            Preconditions.checkArgument(row >= 0 && row < getRowCount());
            Preconditions.checkArgument(col >= 0 && col < getColumnCount());
            switch (col) {
                case 0:
                    return String.class;
                default:
                    return properties.get(row).getPropertyClass();
            }
        }

        @Override
        public Object getValueAt(int row, int col) {
            Preconditions.checkArgument(row >= 0 && row < getRowCount());
            Preconditions.checkArgument(col >= 0 && col < getColumnCount());
            switch (col) {
                case 0:
                    return properties.get(row).getDisplayName();
                default:
                    return properties.get(row).getValue();
            }
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            Preconditions.checkArgument(row >= 0 && row < getRowCount());
            if (col == 0) {
                return false;
            }
            return !properties.get(row).isExpert();
        }

        @Override
        public String getColumnName(int col) {
            Preconditions.checkArgument(col >= 0 && col < getColumnCount());
            switch (col) {
                case 0:
                    return "names";
                default:
                    return "values";
            }
        }

        public boolean isReadOnly(int row, int col) {
            switch (col) {
                case 0:
                    return true;
                default:
                    return properties.get(row).isExpert();
            }
        }
    }
    private volatile JavaEnder ender;

    // allow per-cell rendering and editing via JavaBeans
    private final JXTable table = new JXTable() {

        @Override
        public TableCellRenderer getCellRenderer(int row, int column) {
            if (column == 0) {
                DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
                renderer.setHorizontalAlignment(JLabel.RIGHT);
                return renderer;
                //                TableCellRenderer renderer = getDefaultRenderer(String.class);
                //                log.info(renderer.getClass().toString());
                //                if (renderer instanceof DefaultTableCellRenderer) {
                //                    ((DefaultTableCellRenderer)renderer).setHorizontalAlignment(JLabel.RIGHT);
                //                }
                //                if (renderer instanceof DefaultTableRenderer) {
                //                    // TODO: how to set right aligned text for SwingX renderer?
                //                }
                //                return renderer;
            }

            MyTableModel model = (MyTableModel) getModel();
            Class<?> klass = model.getClassAt(row, column);
            TableCellRenderer javaBeansRenderer = PropertyTableCellEditorAdapter.forClass(klass);
            TableCellRenderer defaultRenderer = getDefaultRenderer(klass);
//            if (klass.equals(String.class)) {
//                return defaultRenderer;
//            }
            if (javaBeansRenderer != null) {
                return javaBeansRenderer;
            }
            if (defaultRenderer != null) {
                return defaultRenderer;
            }
            log.warning("No TableCellRenderer for " + klass.getName());
            return null;
        }

        // code repetition because of TableCell{Renderer, Editor} non-inheritance
        @Override
        public TableCellEditor getCellEditor(int row, int column) {
            MyTableModel model = (MyTableModel) getModel();
            if (model.isReadOnly(row, column)) {
                return null;
            }

            Class<?> klass = model.getClassAt(row, column);
            TableCellEditor javaBeansRenderer = PropertyTableCellEditorAdapter.forClass(klass);
            TableCellEditor defaultRenderer = getDefaultEditor(klass);
            if (klass.equals(String.class)) {
                return defaultRenderer;
            }
            if (javaBeansRenderer != null) {
                return javaBeansRenderer;
            }
            if (defaultRenderer != null) {
                return defaultRenderer;
            }
            log.warning("No TableCellEditor for " + klass.getName());
            return null;
        }
    };

    public JavabeansEditorForm() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        JPanel top = new JPanel(new BorderLayout());
        logo = new JXImagePanel();
        logo.setSize(0, 0);
        top.add(logo, BorderLayout.LINE_START);
        add(top);

        add(table);

        table.setTableHeader(null);
        table.setBackground(null);
        table.setShowGrid(false);
        table.setCellSelectionEnabled(false);

        Dimension d = table.getIntercellSpacing();
        int gapWidth = 10;
        int gapHeight = 10;
        table.setIntercellSpacing(new Dimension(gapWidth, gapHeight));
        table.setRowHeight(table.getRowHeight() + gapHeight + 10);

        table.setFocusable(false);
    }

    public void refresh() {
        Image icon = ender.getIcon(BeanInfo.ICON_COLOR_32x32);
        logo.setImage(icon);
        if (icon == null) {
            logo.setMinimumSize(new Dimension(0, 0));
        } else {
            int width = icon.getWidth(null);
            int height = icon.getHeight(null);
            logo.setMinimumSize(new Dimension(width, height));
        }

        final List<Property> properties = Lists.newArrayList(Iterables.filter(ender.getProperties(), new Predicate<Property>() {

            @Override
            public boolean apply(Property input) {
                return !input.isHidden();
            }
        }));
        table.setModel(new MyTableModel(properties));
        TableColumn names = table.getColumnModel().getColumn(0);
        int preferred = resetColumnWidths(0);
        table.getColumn("names").setMaxWidth(preferred);
        resetColumnWidths(1);

        invalidate();
    }

    private int resetColumnWidths(int column) {
        int min = Integer.MAX_VALUE;
        int max = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            int width = (int) table.getCellRect(i, column, true).getWidth();
            if (width < min) {
                min = width;
            }
            if (width > max) {
                max = width;
            }
        }
        table.getColumnModel().getColumn(column).setMinWidth(max);
        return max;
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
