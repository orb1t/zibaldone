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
import java.lang.reflect.Constructor;
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
 * The following listeners are kept informed of changes made via the
 * {@link Property} methods, but (unless manually managed) they will not
 * be informed of changes through other means:
 * <ul>
 * <li>{@link PropertyChangeListener}s - added via
 * {@link #addPropertyChangeListener(PropertyChangeListener)}
 * - are informed after any change made here to the JavaBean's properties.</li>
 * <li>{@link VetoableChangeListener}s - added via
 * {@link #addVetoableChangeListener(VetoableChangeListener)}
 * - are informed prior to any change made here to the JavaBean's properties.</li>
 * </ul>
 * Any other listeners are the business of the bean itself.
 * 
 * @author Samuel Halliday
 */
public class BeanHelper {

    private static final Logger log = Logger.getLogger(BeanHelper.class.getName());

    private final Object bean;

    private final BeanInfo beaninfo;
            
    private final PropertyChangeSupport propListeners;

    private final VetoableChangeSupport vetoListeners;

    /**
     * Attempts to obtain a {@link BeanInfo} for the given bean by
     * instantiating a class of the same name with {@code BeanInfo} appended
     * to the end.
     *
     * @param bean
     * @return {@code null} if no suitable BeanInfo was found
     */
    static public BeanInfo loadBeanInfo(Object bean) {
        Preconditions.checkNotNull(bean);

        String canonical = bean.getClass().getCanonicalName();
        if (canonical == null) {
            return null;
        }
        String testName = canonical + "BeanInfo";
        try {
            Class<?> klass = Class.forName(testName);
            if (!klass.isInstance(BeanInfo.class)) {
                throw new ClassCastException(testName + " not a BeanInfo");
            }
            Constructor<?> constructor = klass.getConstructor(new Class<?>[0]);
            return (BeanInfo) constructor.newInstance(new Object[0]);
        } catch (ClassNotFoundException ex) {
        } catch (Exception e) {
            log.log(Level.INFO, "Reflection problems with " + testName, e);
        }
        return null;
    }

    /**
     * Creates the helper by using {@link #loadBeanInfo(Object)} to find any
     * {@link BeanInfo}s, then delegating to {@link #BeanHelper(Object, BeanInfo)}.
     * 
     * @param bean
     */
    public BeanHelper(Object bean) {
        this(bean, loadBeanInfo(bean));
    }

    /**
     * @param bean
     * @param beaninfo if {@code null} then creates a {@link SimpleBeanInfo}.
     */
    public BeanHelper(Object bean, @Nullable BeanInfo beaninfo) {
        Preconditions.checkNotNull(bean);
        this.bean = bean;
        if (beaninfo != null) {
            this.beaninfo = beaninfo;
        } else {
            this.beaninfo = new SimpleBeanInfo();
        }
        this.propListeners = new PropertyChangeSupport(bean);
        this.vetoListeners = new VetoableChangeSupport(bean);
    }

    /**
     * @param iconKind one of
     * {@link BeanInfo#ICON_MONO_16x16}, {@link BeanInfo#ICON_MONO_32x32},
     * {@link BeanInfo#ICON_COLOR_16x16}, {@link BeanInfo#ICON_COLOR_32x32}
     * @return
     */
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
            } catch (NullPointerException n) {
                return null;
            } catch (Exception e) {
                throw new RuntimeException(bean.getClass() + " doesn't support "
                        + method.getName() + " as a JavaBean getter", e);
            }
        }

        /**
         * @param value a veto from a listener will result in a silent failure
         */
        public void setValue(Object value) {
            setValue(value, null);
        }

        /**
         * @param value a veto from a listener will result in a silent failure
         * @param propagationId this is a "reserved" value in Javabeans, but we
         * expose this to track where a change originated - making it possible to
         * ignore events that you created.
         */
        public void setValue(Object value, Object propagationId) {
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
                PropertyChangeEvent pce = new PropertyChangeEvent(bean, getName(), old, value);
                pce.setPropagationId(propagationId);
                propListeners.firePropertyChange(pce);
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
     * @param name
     * @return
     */
    public Property getProperty(String name) {
        Preconditions.checkNotNull(name);
        Preconditions.checkArgument(!name.isEmpty());
        PropertyDescriptor[] descriptors = getPropertyDescriptors();
        if (descriptors == null || descriptors.length == 0) {
            return null;
        }
        for (PropertyDescriptor descriptor : descriptors) {
            if (name.equals(descriptor.getName())) {
                return new Property(descriptor);
            }
        }
        return null;
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
