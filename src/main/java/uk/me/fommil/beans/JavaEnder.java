/*
 * Created 01-Jun-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.beans;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.awt.Image;
import java.beans.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;

/**
 * Abstracts the JavaBeans API providing sensible actions and accessors.
 * Unless documented here, the JavaBeans API and surrounding ecosystem is
 * ignored.
 * <p>
 * In addition, the following listeners are managed:
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

    private static final Logger log = Logger.getLogger(JavaEnder.class.getName());

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

    /**
     * A property held by a JavaBean
     */
    public class Property {

        private final PropertyDescriptor descriptor;

        private Property(PropertyDescriptor descriptor) {
            Preconditions.checkNotNull(descriptor);
            this.descriptor = descriptor;
            Method getter = descriptor.getReadMethod();
            Method setter = descriptor.getWriteMethod();
            if (getter == null || getter.getParameterTypes().length != 0 || Modifier.isStatic(getter.getModifiers()) || !Modifier.isPublic(getter.getModifiers())) {
                Preconditions.checkArgument(false, "bad getter");
            }
            if (setter == null || setter.getParameterTypes().length != 1 || Modifier.isStatic(setter.getModifiers()) || !Modifier.isPublic(setter.getModifiers())) {
                Preconditions.checkArgument(false, "bad setter");
            }
        }

        public Class<?> getPropertyClass() {
            return descriptor.getPropertyType();
        }

        public Object getValue() {
            Method method = descriptor.getReadMethod();
            try {
                return method.invoke(bean, new Object[0]);
            } catch (Exception e) {
                throw new RuntimeException(bean.getClass() + " doesn't support "
                        + method.getName() + " as a JavaBean getter", e);
            }
        }

        /**
         * @param value a veto from a listener will result in a silent failure
         */
        public void setValue(Object value) {
            Object old = getValue();
            Method method = descriptor.getWriteMethod();
            try {
                vetoListeners.fireVetoableChange(getName(), old, value);
            } catch (PropertyVetoException ex) {
                log.info("Veto: " + bean.getClass() + "." + method.getName());
                return;
            }
            try {
                method.invoke(bean, value);
                propListeners.firePropertyChange(getName(), old, value);
            } catch (Exception e) {
                throw new RuntimeException(bean.getClass() + " doesn't support "
                        + method.getName() + " as a JavaBean setter", e);
            }
        }

        // <editor-fold defaultstate="collapsed" desc="BOILERPLATE GETTERS/SETTERS">
        public String getShortDescription() {
            return descriptor.getShortDescription();
        }

        public String getDisplayName() {
            return descriptor.getDisplayName();
        }

        public String getName() {
            return descriptor.getName();
        }

        public boolean isHidden() {
            return descriptor.isHidden();
        }

        public boolean isExpert() {
            return descriptor.isExpert();
        }
        // </editor-fold>
    }

    /**
     * @return the properties that are considered to be "bean like"
     */
    public List<Property> getProperties() {
        PropertyDescriptor[] descriptors = getPropertyDescriptors();
        if (descriptors == null || descriptors.length == 0) {
            return Collections.emptyList();
        }
        List<Property> properties = Lists.newArrayList();
        for (PropertyDescriptor descriptor : descriptors) {
            if ("class".equals(descriptor.getName())) {
                // crazy JavaBeans people
                continue;
            }
            try {
                Property property = new Property(descriptor);
                properties.add(property);
            } catch (IllegalArgumentException e) {
                log.info("ignoring " + bean.getClass() + "." + descriptor.getName());
            }
        }
        return properties;
    }

    private PropertyDescriptor[] getPropertyDescriptors() {
        PropertyDescriptor[] properties = beaninfo.getPropertyDescriptors();
        if (properties != null) {
            return properties;
        }
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            properties = beanInfo.getPropertyDescriptors();
            return properties;
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
