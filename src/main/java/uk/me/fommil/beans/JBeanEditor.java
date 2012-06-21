/*
 * Created 01-Jun-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.beans;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.awt.*;
import java.beans.*;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.*;
import org.jdesktop.swingx.JXImagePanel;
import org.jdesktop.swingx.JXTable;
import uk.me.fommil.beans.BeanHelper.Property;

/**
 * An automatically-generated Swing Form for editing arbitrary objects
 * matching the JavaBeans get/set pattern (properties) and optionally providing
 * a {@link BeanInfo}.
 * <p>
 * This class was created in order to provide a light-weight editor to do a lot
 * of boilerplate work in GUI design. Existing solutions require huge frameworks
 * and are neither attractive nor intuitive to end users.
 * <p>
 * Editing of Javabean properties is supported at runtime through the programmatic
 * provision of a suitable {@link PropertyEditor} to the {@link PropertyEditorManager},
 * or - for a specific property - by setting the value to be returned by
 * {@link PropertyDescriptor#createPropertyEditor(Object)} in the
 * {@link BeanInfo}.
 * <p>
 * The {@link BeanInfo} is used for the following:
 * <ul>
 * <li>{@link BeanInfo#getIcon(int)} is displayed, if available.</li>
 * <li>{@link PropertyDescriptor#isHidden()} is respected.</li>
 * <li>{@link PropertyDescriptor#isExpert()} will produce a read-only entry
 * (a useful interpretation of a vague API).</li>
 * </ul>
 * This is not capable of detecting changes made to the
 * underlying bean by means other than the {@link BeanHelper} API,
 * in which case a call to {@link #refresh()} is recommended.
 * <ul> 
 * <li>TODO: white text background for default text editor</li>
 * <li>TODO: lose the black background on focus (flashes when boolean is edited)</li>
 * <li>TODO: support for values which have much bigger heights (e.g. Image)</li>
 * </ul>
 * 
 * @see <a href="http://stackoverflow.com/questions/10840078">Origin on Stack Overflow</a>
 * @author Samuel Halliday
 */
public final class JBeanEditor extends JPanel {

    private static final Logger log = Logger.getLogger(JBeanEditor.class.getName());

    private final JXImagePanel logo = new JXImagePanel();

    private volatile BeanHelper beanHelper;

    // links the table to the BeanHelper
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

    // allow per-cell rendering and editing via JavaBeans
    private final JXTable table = new JXTable() {

        @Override
        public TableCellRenderer getCellRenderer(int row, int column) {
            if (column == 0) {
                DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
                renderer.setHorizontalAlignment(JLabel.RIGHT);
                return renderer;
            }

            // code repetition with getCellEditor because of TableCell{Renderer, Editor} non-inheritance
            MyTableModel model = (MyTableModel) getModel();
            Class<?> klass = model.getClassAt(row, column);
            TableCellRenderer javaBeansRenderer = PropertyEditorTableAdapter.forClass(klass);
            if (javaBeansRenderer != null) {
                return javaBeansRenderer;
            }
            return getDefaultRenderer(klass);
        }

        @Override
        public TableCellEditor getCellEditor(int row, int column) {
            MyTableModel model = (MyTableModel) getModel();
            if (model.isReadOnly(row, column)) {
                return null;
            }

            // code repetition with getCellRenderer because of TableCell{Renderer, Editor} non-inheritance
            Class<?> klass = model.getClassAt(row, column);
            TableCellEditor javaBeansEditor = PropertyEditorTableAdapter.forClass(klass);
            if (javaBeansEditor != null) {
                return javaBeansEditor;
            }
            TableCellEditor defaultEditor = getDefaultEditor(klass);
            if (defaultEditor == null) {
                log.warning("No TableCellEditor for " + klass.getName());
            }
            if (defaultEditor instanceof DefaultCellEditor) {
                // default double-click is bad user interaction
                ((DefaultCellEditor) defaultEditor).setClickCountToStart(0);
            }
            return defaultEditor;
        }

        // Set the width of the column by the largest renderer entry in the column
        @Override
        public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
            final Component prepareRenderer = super.prepareRenderer(renderer, row, column);
            final TableColumn tableColumn = getColumnModel().getColumn(column);
            int componentWidth = prepareRenderer.getMinimumSize().width + getIntercellSpacing().width;
            if (tableColumn.getWidth() < componentWidth) {
                tableColumn.setMinWidth(componentWidth);
                if (column == 0) {
                    tableColumn.setMaxWidth(componentWidth);
                }
            }
            return prepareRenderer;
        }
    };

    public JBeanEditor() {
        super();
        setLayout(new BorderLayout());

        JPanel top = new JPanel();
        logo.setSize(0, 0);
        top.add(logo);
        add(top, BorderLayout.NORTH);

        add(table, BorderLayout.CENTER);

        table.setTableHeader(null);
        table.setBackground(null);
        table.setShowGrid(false);
        table.setCellSelectionEnabled(false);

        Dimension spacing = new Dimension(10, 5);
        table.setIntercellSpacing(spacing);
        table.setRowHeight(table.getRowHeight() + 2 * spacing.height);

        table.setFocusable(false);
    }

    public void refresh() {
        Image icon = beanHelper.getIcon(BeanInfo.ICON_COLOR_32x32);
        logo.setImage(icon);
        if (icon == null) {
            logo.setMinimumSize(new Dimension(0, 0));
        } else {
            int width = icon.getWidth(null);
            int height = icon.getHeight(null);
            logo.setMinimumSize(new Dimension(width, height));
        }

        final List<Property> properties = Lists.newArrayList(Iterables.filter(beanHelper.getProperties(), new Predicate<Property>() {

            @Override
            public boolean apply(Property input) {
                return !input.isHidden();
            }
        }));
        table.setModel(new MyTableModel(properties));

        table.getColumnModel().getColumn(0).setPreferredWidth(0);
        table.getColumnModel().getColumn(1).setPreferredWidth(0);
        invalidate();
    }

    /**
     * @param bean
     */
    public void setBean(Object bean) {
        Preconditions.checkNotNull(bean);
        setBeanHelper(new BeanHelper(bean));
    }

    /**
     * @param beanHelper
     */
    public void setBeanHelper(BeanHelper beanHelper) {
        Preconditions.checkNotNull(beanHelper);
        this.beanHelper = beanHelper;
        refresh();
    }

    public BeanHelper getBeanHelper() {
        return beanHelper;
    }
}