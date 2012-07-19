/*
 * Created 19-Jul-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import java.beans.BeanDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * So that NetBeans doesn't try to set the LayoutManager.
 * 
 * @see <a href="http://netbeans.org/bugzilla/show_bug.cgi?id=215528">NetBeans #215528</a>
 * @author Samuel Halliday
 */
public class TagsViewBeanInfo extends SimpleBeanInfo {

    @Override
    public BeanDescriptor getBeanDescriptor() {
        BeanDescriptor desc = new BeanDescriptor(TagsView.class);
        desc.setValue("isContainer", Boolean.FALSE);
        return desc;
    }
}
