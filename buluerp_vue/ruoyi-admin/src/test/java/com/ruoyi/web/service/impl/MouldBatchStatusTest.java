package com.ruoyi.web.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.web.controller.erp.ErpMouldController;
import com.ruoyi.web.domain.ErpMould;
import com.ruoyi.web.domain.ErpMouldHouse;
import com.ruoyi.web.mapper.ErpMouldMapper;
import com.ruoyi.web.request.mould.BatchUpdateMouldStatusRequest;
import com.ruoyi.web.request.mould.ListMouldRequest;
import com.ruoyi.web.service.IErpMouldHouseService;
import com.ruoyi.web.service.IErpMouldService;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.Configuration;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class MouldBatchStatusTest {
    @Test
    public void putBatchStatusReachesControllerAndBindsFrontendPayload() throws Exception {
        AtomicInteger calls = new AtomicInteger();
        IErpMouldService service = proxy(IErpMouldService.class, (p, method, args) -> {
            assertEquals("batchUpdateStatus", method.getName());
            BatchUpdateMouldStatusRequest request = (BatchUpdateMouldStatusRequest) args[0];
            assertEquals("filtered", request.getScope());
            assertEquals("A", request.getFilters().getMouldNumber());
            assertEquals(ErpMould.STATUS_ACCEPTED, request.getStatus());
            calls.incrementAndGet();
            return 1200;
        });
        ErpMouldController controller = new ErpMouldController();
        ReflectionTestUtils.setField(controller, "mouldService", service);
        MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).build();
        MvcResult result = mvc.perform(put("/system/mould/batch-status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"scope\":\"filtered\",\"filters\":{\"mouldNumber\":\"A\"},"
                        + "\"status\":\"验收合格(已入库)\"}")).andReturn();
        assertEquals(200, result.getResponse().getStatus());
        assertNull(result.getResolvedException());
        JsonNode response = new ObjectMapper().readTree(result.getResponse().getContentAsByteArray());
        assertEquals(200, response.get("code").asInt());
        assertEquals(1200, response.get("data").asInt());
        assertEquals(1, calls.get());
    }

    @Test
    public void selectedIdsAreDeduplicatedAndOtherFieldsArePreserved() {
        List<Long> updated = new ArrayList<>();
        ErpMouldServiceImpl service = service((p, method, args) -> {
            if (method.getName().equals("selectBatchIds")) return Arrays.asList(new ErpMould(), new ErpMould());
            assertEquals("updateBatchStatus", method.getName());
            updated.addAll((List<Long>) args[0]);
            assertEquals(ErpMould.STATUS_ACCEPTED, args[1]);
            assertNull(args[2]);
            return 2;
        });
        BatchUpdateMouldStatusRequest request = selected(2L, 1L, 2L);
        request.setMouldHouseId(99L);
        assertEquals(2, service.batchUpdateStatus(request));
        assertEquals(Arrays.asList(1L, 2L), updated);
    }

    @Test
    public void filteredScopeUpdatesAllMatchingIdsInBatches() {
        List<Long> matches = LongStream.rangeClosed(1, 1201).boxed().collect(Collectors.toList());
        List<Long> updated = new ArrayList<>();
        List<Integer> batchSizes = new ArrayList<>();
        ListMouldRequest filters = new ListMouldRequest();
        filters.setManufacturerName("厂商A");
        filters.setStatus(ErpMould.STATUS_CREATED);
        ErpMouldServiceImpl service = service((p, method, args) -> {
            if (method.getName().equals("selectMouldIds")) {
                assertSame(filters, args[0]);
                return matches;
            }
            assertEquals("updateBatchStatus", method.getName());
            List<Long> batch = (List<Long>) args[0];
            updated.addAll(batch);
            batchSizes.add(batch.size());
            return batch.size();
        });
        BatchUpdateMouldStatusRequest request = filtered(filters);
        assertEquals(1201, service.batchUpdateStatus(request));
        assertEquals(matches, updated);
        assertEquals(Arrays.asList(500, 500, 201), batchSizes);
    }

    @Test
    public void invalidScopeStatusAndIdsCannotWrite() {
        ErpMouldServiceImpl service = service((p, method, args) -> { throw new AssertionError("Unexpected database access"); });
        for (BatchUpdateMouldStatusRequest request : Arrays.asList(selected(), selected(-1L), selected((Long) null), filtered(null))) {
            assertThrows(ServiceException.class, () -> service.batchUpdateStatus(request));
        }
        BatchUpdateMouldStatusRequest request = selected(1L);
        request.setScope("all");
        assertThrows(ServiceException.class, () -> service.batchUpdateStatus(request));
        request.setScope("selected");
        request.setStatus("未知状态");
        assertThrows(ServiceException.class, () -> service.batchUpdateStatus(request));
        BatchUpdateMouldStatusRequest ambiguous = filtered(new ListMouldRequest());
        ambiguous.setIds(Collections.singletonList(1L));
        assertThrows(ServiceException.class, () -> service.batchUpdateStatus(ambiguous));
    }

    @Test
    public void missingSelectedRecordStopsAllWrites() {
        ErpMouldServiceImpl service = service((p, method, args) -> {
            assertEquals("selectBatchIds", method.getName());
            return Collections.singletonList(new ErpMould());
        });
        assertThrows(ServiceException.class, () -> service.batchUpdateStatus(selected(1L, 2L)));
    }

    @Test
    public void emptyQueryDoesNotIssueUnboundedUpdate() {
        ErpMouldServiceImpl service = service((p, method, args) -> {
            assertEquals("selectMouldIds", method.getName());
            return Collections.emptyList();
        });
        assertEquals(0, service.batchUpdateStatus(filtered(new ListMouldRequest())));
    }

    @Test
    public void repairingRequiresExistingHouseAndWritesIt() {
        ErpMouldServiceImpl service = service((p, method, args) -> {
            if (method.getName().equals("selectBatchIds")) return Collections.singletonList(new ErpMould());
            assertEquals("updateBatchStatus", method.getName());
            assertEquals(Long.valueOf(8), args[2]);
            return 1;
        });
        ReflectionTestUtils.setField(service, "mouldHouseService", proxy(IErpMouldHouseService.class,
                (p, method, args) -> Long.valueOf(8).equals(args[0]) ? new ErpMouldHouse() : null));
        BatchUpdateMouldStatusRequest request = selected(1L);
        request.setStatus(ErpMould.STATUS_REPAIRING);
        for (Long id : Arrays.asList(null, -1L, 999L)) {
            request.setMouldHouseId(id);
            assertThrows(ServiceException.class, () -> service.batchUpdateStatus(request));
        }
        request.setMouldHouseId(8L);
        assertEquals(1, service.batchUpdateStatus(request));
    }

    @Test
    public void mapperSharesListFiltersAndGuardsEmptyUpdateIds() throws Exception {
        Configuration configuration = new Configuration();
        String resource = "mapper/web/ErpMouldMapper.xml";
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(resource)) {
            assertNotNull(input);
            new XMLMapperBuilder(input, configuration, resource, configuration.getSqlFragments()).parse();
        }
        ListMouldRequest filters = new ListMouldRequest();
        filters.setMouldNumber("A");
        filters.setManufacturerName("厂商");
        filters.setStatus(ErpMould.STATUS_CREATED);
        filters.setTrialDateFrom(new Date());
        filters.setTrialDateTo(new Date());
        filters.setMouldHouseId(8L);
        filters.setId(1L);
        String prefix = ErpMouldMapper.class.getName() + ".";
        BoundSql list = configuration.getMappedStatement(prefix + "selectMouldInfoList").getBoundSql(filters);
        BoundSql ids = configuration.getMappedStatement(prefix + "selectMouldIds").getBoundSql(filters);
        assertEquals(whereClause(list), whereClause(ids));
        assertEquals(7, ids.getParameterMappings().size());
        Map<String, Object> params = new HashMap<>();
        params.put("ids", Collections.emptyList());
        params.put("status", ErpMould.STATUS_ACCEPTED);
        params.put("mouldHouseId", null);
        String sql = configuration.getMappedStatement(prefix + "updateBatchStatus").getBoundSql(params).getSql();
        assertTrue(sql.contains("1 = 0"));
        assertFalse(sql.contains("mould_house_id"));
        assertFalse(sql.contains("manufacturer_id"));
        assertFalse(sql.contains("trial_date"));
        params.put("ids", Arrays.asList(1L, 2L, 3L).subList(0, 2));
        params.put("mouldHouseId", 8L);
        BoundSql update = configuration.getMappedStatement(prefix + "updateBatchStatus").getBoundSql(params);
        assertFalse(update.getSql().contains("1 = 0"));
        assertTrue(update.getSql().contains("mould_house_id"));
        assertEquals(4, update.getParameterMappings().size());
    }

    private String whereClause(BoundSql sql) {
        String normalized = sql.getSql().replaceAll("\\s+", " ");
        return normalized.substring(normalized.indexOf("WHERE"), normalized.indexOf("order by"));
    }

    private ErpMouldServiceImpl service(InvocationHandler handler) {
        ErpMouldServiceImpl service = new ErpMouldServiceImpl();
        ReflectionTestUtils.setField(service, "erpMouldMapper", proxy(ErpMouldMapper.class, handler));
        return service;
    }

    private static <T> T proxy(Class<T> type, InvocationHandler handler) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, handler));
    }

    private BatchUpdateMouldStatusRequest selected(Long... ids) {
        BatchUpdateMouldStatusRequest request = new BatchUpdateMouldStatusRequest();
        request.setScope("selected");
        request.setIds(Arrays.asList(ids));
        request.setStatus(ErpMould.STATUS_ACCEPTED);
        return request;
    }

    private BatchUpdateMouldStatusRequest filtered(ListMouldRequest filters) {
        BatchUpdateMouldStatusRequest request = new BatchUpdateMouldStatusRequest();
        request.setScope("filtered");
        request.setFilters(filters);
        request.setStatus(ErpMould.STATUS_ACCEPTED);
        return request;
    }
}
