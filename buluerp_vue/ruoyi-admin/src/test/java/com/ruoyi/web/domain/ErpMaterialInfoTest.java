package com.ruoyi.web.domain;

import org.junit.Test;
import org.apache.ibatis.reflection.Reflector;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ErpMaterialInfoTest {
    @Test
    public void purchasedPropertyShouldFollowJavaBeanConvention() throws Exception {
        ErpMaterialInfo materialInfo = new ErpMaterialInfo();

        materialInfo.setPurchased(Boolean.TRUE);

        assertEquals(Boolean.TRUE, materialInfo.getPurchased());
        PropertyDescriptor purchasedDescriptor = Arrays.stream(
                        Introspector.getBeanInfo(ErpMaterialInfo.class).getPropertyDescriptors())
                .filter(descriptor -> "purchased".equals(descriptor.getName()))
                .findFirst()
                .orElse(null);

        assertNotNull(purchasedDescriptor);
        assertEquals(Boolean.class, purchasedDescriptor.getPropertyType());
        assertTrue("purchased should have a readable getter", purchasedDescriptor.getReadMethod() != null);
        assertTrue("purchased should have a writable setter", purchasedDescriptor.getWriteMethod() != null);
        assertEquals(Boolean.class, new Reflector(ErpMaterialInfo.class).getGetterType("purchased"));
    }
}
