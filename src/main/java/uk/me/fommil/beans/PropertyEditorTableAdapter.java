/*
 * Created 01-Jun-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.beans;

import com.google.common.base.Preconditions;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import uk.me.fommil.beans.editors.JPropertyEditor;

/**
 * Adapts a {@link PropertyEditor} into {@link TableCellEditor} and {@link TableCellRenderer}.
 * 
 * @author Samuel Halliday
 */
public class PropertyEditorTableAdapter extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {

    private static final long serialVersionUID = 1L;

    private final PropertyEditor editor;

    private final boolean expert;

    /**
     * @param klass
     * @return
     */
    public static PropertyEditorTableAdapter forClass(Class<?> klass) {
        return forClass(klass, false);
    }

    /**
     * @param klass
     * @param expert true is a hint to some implementations to disable editing
     * @return
     */
    public static PropertyEditorTableAdapter forClass(Class<?> klass, boolean expert) {
        PropertyEditor editor = PropertyEditorManager.findEditor(klass);
        if (editor == null || !editor.supportsCustomEditor()) {
            return null;
        }
        return new PropertyEditorTableAdapter(editor, expert);
    }

    /**
     * @param editor
     * @param expert true is a hint to some implementations to disable editing
     */
    public PropertyEditorTableAdapter(PropertyEditor editor, boolean expert) {
        super();
        Preconditions.checkNotNull(editor);
        this.editor = editor;
        this.expert = expert;
        Preconditions.checkArgument(editor.supportsCustomEditor());
        editor.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                fireEditingStopped();
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        editor.setValue(value);
        Component editorComponent;

        if (editor instanceof JPropertyEditor) {
            editorComponent = ((JPropertyEditor) editor).getCustomEditor(expert);
        } else {
            editorComponent = editor.getCustomEditor();
        }

        Preconditions.checkState(editorComponent != null);
        return editorComponent;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return getTableCellEditorComponent(table, value, isSelected, row, column);
    }

    @Override
    public Object getCellEditorValue() {
        return editor.getValue();
    }
}