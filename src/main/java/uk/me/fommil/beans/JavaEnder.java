/*
 * Created 01-Jun-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.beans;

import com.google.common.base.Preconditions;
import java.awt.Image;
import java.beans.*;
import javax.annotation.Nullable;

/**
 * Abstracts the JavaBeans API providing sensible actions and accessors.
 * Unless documented here, the JavaBeans API and surrounding ecosystem is
 * ignored.
 * <ul>
 * <li>{@link PropertyChangeListener}s - added via
 * {@link #addPropertyChangeListener(PropertyChangeListener)}
 * - are informed after any change made here to the JavaBean's properties.</li>
 * <li>{@link VetoableChangeListener}s - added via
 * {@link #addVetoableChangeListener(VetoableChangeListener)}
 * - are informed prior to any change made here to the JavaBean's properties.</li>
 * </ul>
 * TODO: check what happens if the BeanInfo provides
 * {@link VetoableChangeListener}s or {@link PropertyChangeListener}s. We might
 * want to look at better support for this.
 * 
 * @author Samuel Halliday
 * @see <a href="http://en.wikipedia.org/wiki/Bean_(Ender's_Game)">Bean and Ender</a>
 */
public class JavaEnder {

    private final Object bean;

    private final BeanInfo beaninfo;

    private final PropertyChangeSupport propListeners;

    private final VetoableChangeSupport vetoListeners;

    /**
     * @param bean
     */
    public JavaEnder(Object bean) {
        this(bean, null);
    }

    /**
     * @param bean
     * @param delegate if {@code null} then check if the bean implements
     * {@link BeanInfo}, otherwise use a {@link SimpleBeanInfo}.
     */
    public JavaEnder(Object bean, @Nullable BeanInfo delegate) {
        Preconditions.checkNotNull(bean);
        this.bean = bean;
        if (delegate != null) {
            this.beaninfo = delegate;
        } else {
            if (bean instanceof BeanInfo) {
                beaninfo = (BeanInfo) bean;
            } else {
                this.beaninfo = new SimpleBeanInfo();
            }
        }
        this.propListeners = new PropertyChangeSupport(bean);
        this.vetoListeners = new VetoableChangeSupport(bean);
    }

    public Image getIcon(int iconKind) {
        return beaninfo.getIcon(iconKind);
    }

    // TODO: abstractify so that we have nicer objects to work with
    public PropertyDescriptor[] getPropertyDescriptors() {
        PropertyDescriptor[] properties = beaninfo.getPropertyDescriptors();
        if (properties != null) {
            return properties;
        }
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            return beanInfo.getPropertyDescriptors();
        } catch (IntrospectionException ex) {
            throw new RuntimeException("Failed to access " + bean.getClass() + " as a JavaBean", ex);
        }
    }

    public Object getBean() {
        return bean;
    }

    public BeanInfo getBeanInfo() {
        return beaninfo;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propListeners.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propListeners.removePropertyChangeListener(listener);
    }

    public void addVetoableChangeListener(VetoableChangeListener listener) {
        vetoListeners.addVetoableChangeListener(listener);
    }

    public void removeVetoableChangeListener(VetoableChangeListener listener) {
        vetoListeners.removeVetoableChangeListener(listener);
    }
}
