/*
 * Created 01-Jun-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.beans;

import com.google.common.base.Preconditions;
import java.awt.Component;
import java.beans.*;
import javax.swing.JComponent;
import javax.swing.JPanel;

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
 * through {@link #setEnder(JavaEnder)}, then it will be used.
 * <p>
 * Features:
 * <ul>
 * <li>{@link BeanInfo#getIcon(int)} is used, if available.</li>
 * <li>{@link PropertyDescriptor#isHidden()} is respected.</li>
 * <li>{@link PropertyDescriptor#isExpert()} will produce a read-only entry
 * (a useful interpretation of a vague API).</li>
 * </ul>
 * TODO: Convenience methods are provided to make use of the features.
 * <p>
 * The latter two features provide a simple alternative to registering listeners
 * directly with the JavaBeans.
 * <p>
 * This is not capable of detecting changes made to the
 * underlying bean by others, so a call to {@link #revalidate()} is recommended
 * if changes are made.
 * 
 * @see <a href="http://stackoverflow.com/questions/10840078">Question on Stack Overflow</a>
 * @author Samuel Halliday
 */
public class JavabeansEditorForm extends JComponent {

    private volatile JavaEnder ender;

    public JavabeansEditorForm() {
        super();
    }

    @Override
    public void revalidate() {
        // TODO: update fields

        super.revalidate();
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
    }
}
