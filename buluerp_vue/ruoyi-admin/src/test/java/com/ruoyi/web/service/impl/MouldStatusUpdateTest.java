package com.ruoyi.web.service.impl;

import com.ruoyi.web.domain.ErpMould;
import com.ruoyi.web.mapper.ErpMouldMapper;
import com.ruoyi.web.request.mould.BatchUpdateMouldStatusRequest;
import com.ruoyi.web.request.mould.UpdateMouldRequest;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MouldStatusUpdateTest {
    private static final String[] NEW_STATUSES = {
            ErpMould.STATUS_PRODUCE_THEN_REPAIR,
            ErpMould.STATUS_TRIALED
    };

    @Test
    public void newStatusesAreAcceptedByValidation() {
        for (String status : NEW_STATUSES) {
            assertTrue(ErpMould.isStatusValid(status));
        }
    }

    @Test
    public void singleMouldUpdateAcceptsNewStatuses() {
        for (String status : NEW_STATUSES) {
            AtomicReference<ErpMould> updated = new AtomicReference<>();
            ErpMouldServiceImpl service = service((proxy, method, args) -> {
                if (method.getName().equals("selectById")) {
                    return new ErpMould();
                }
                assertEquals("updateById", method.getName());
                updated.set((ErpMould) args[0]);
                return 1;
            });

            UpdateMouldRequest request = new UpdateMouldRequest();
            request.setId(1L);
            request.setStatus(status);
            service.update(request);

            assertEquals(status, updated.get().getStatus());
        }
    }

    @Test
    public void singleMouldUpdatePersistsRemark() {
        AtomicReference<ErpMould> updated = new AtomicReference<>();
        ErpMouldServiceImpl service = service((proxy, method, args) -> {
            if (method.getName().equals("selectById")) {
                return new ErpMould();
            }
            assertEquals("updateById", method.getName());
            updated.set((ErpMould) args[0]);
            return 1;
        });

        UpdateMouldRequest request = new UpdateMouldRequest();
        request.setId(1L);
        request.setRemark("优先生产");
        service.update(request);

        assertEquals("优先生产", updated.get().getRemark());
    }

    @Test
    public void batchUpdateAcceptsNewStatuses() {
        for (String status : NEW_STATUSES) {
            AtomicReference<String> updatedStatus = new AtomicReference<>();
            ErpMouldServiceImpl service = service((proxy, method, args) -> {
                if (method.getName().equals("selectBatchIds")) {
                    return Collections.singletonList(new ErpMould());
                }
                assertEquals("updateBatchStatus", method.getName());
                updatedStatus.set((String) args[1]);
                return 1;
            });

            BatchUpdateMouldStatusRequest request = new BatchUpdateMouldStatusRequest();
            request.setScope("selected");
            request.setIds(Collections.singletonList(1L));
            request.setStatus(status);

            assertEquals(1, service.batchUpdateStatus(request));
            assertEquals(status, updatedStatus.get());
        }
    }

    private ErpMouldServiceImpl service(InvocationHandler handler) {
        ErpMouldServiceImpl service = new ErpMouldServiceImpl();
        ErpMouldMapper mapper = proxy(ErpMouldMapper.class, handler);
        ReflectionTestUtils.setField(service, "baseMapper", mapper);
        ReflectionTestUtils.setField(service, "erpMouldMapper", mapper);
        return service;
    }

    private static <T> T proxy(Class<T> type, InvocationHandler handler) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, handler));
    }
}
