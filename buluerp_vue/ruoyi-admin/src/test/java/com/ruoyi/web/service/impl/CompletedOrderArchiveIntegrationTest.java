package com.ruoyi.web.service.impl;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.PageInterceptor;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.web.domain.ErpOrders;
import com.ruoyi.web.domain.ErpProductionArrange;
import com.ruoyi.web.mapper.ErpProductionArrangeMapper;
import com.ruoyi.web.mapper.ErpProductionScheduleMapper;
import com.ruoyi.web.request.productionschedule.ListProductionScheduleRequest;
import com.ruoyi.web.result.ProductionScheduleResult;
import com.ruoyi.web.service.IErpOrdersService;
import com.ruoyi.web.service.IErpProductionArchiveService;
import com.ruoyi.web.service.IErpProductionArrangeService;
import com.ruoyi.web.service.IErpProductionScheduleService;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.After;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * 已完成订单归档功能的只读集成测试。
 * 直接读取 application-test-db.yml，不启动完整应用、定时任务或通知，也不修改业务数据。
 */
public class CompletedOrderArchiveIntegrationTest {
    private static JdbcTemplate jdbc;
    private static IErpOrdersService ordersService;
    private static IErpProductionScheduleService scheduleService;
    private static IErpProductionArrangeService arrangeService;
    private static IErpProductionArchiveService archiveService;

    @BeforeClass
    public static void connectUsingExistingTestConfiguration() throws Exception {
        ClassPathResource configResource = new ClassPathResource("application-test-db.yml");
        String url;
        String username;
        String password;
        if (configResource.exists()) {
            List<PropertySource<?>> sources = new YamlPropertySourceLoader().load("test-db", configResource);
            assertFalse("application-test-db.yml must define a data source", sources.isEmpty());
            PropertySource<?> properties = sources.get(0);
            url = String.valueOf(properties.getProperty("spring.datasource.druid.master.url"));
            username = String.valueOf(properties.getProperty("spring.datasource.druid.master.username"));
            password = String.valueOf(properties.getProperty("spring.datasource.druid.master.password"));
        } else {
            password = System.getenv("BULUERP_TEST_DB_PASSWORD");
            Assume.assumeTrue(
                    "Provide application-test-db.yml or BULUERP_TEST_DB_PASSWORD to run this integration test",
                    password != null && !password.isEmpty()
            );
            url = "jdbc:mysql://154.201.77.135:29783/buluerp?useUnicode=true&characterEncoding=utf8&useSSL=true&serverTimezone=GMT%2B8";
            username = "root";
        }

        DriverManagerDataSource source = new DriverManagerDataSource();
        source.setDriverClassName("com.mysql.cj.jdbc.Driver");
        source.setUrl(url);
        source.setUsername(username);
        source.setPassword(password);
        jdbc = new JdbcTemplate(source);
        assertEquals("buluerp", jdbc.queryForObject("SELECT DATABASE()", String.class));

        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(source);
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.addMapper(ErpProductionScheduleMapper.class);
        configuration.addMapper(ErpProductionArrangeMapper.class);
        factory.setConfiguration(configuration);
        factory.setTypeAliasesPackage("com.ruoyi.web.domain");
        factory.setMapperLocations(new ClassPathResource("mapper/web/ErpProductionScheduleMapper.xml"));
        factory.setPlugins(new PageInterceptor());
        SqlSessionFactory sqlSessionFactory = factory.getObject();
        SqlSessionTemplate session = new SqlSessionTemplate(sqlSessionFactory);

        ordersService = ordersServiceProxy(jdbc);
        ErpProductionScheduleServiceImpl schedule = new ErpProductionScheduleServiceImpl();
        setField(schedule, "erpProductionScheduleMapper", session.getMapper(ErpProductionScheduleMapper.class));
        setField(schedule, "erpOrdersService", ordersService);
        setField(schedule, "erpOrderService", ordersService);
        scheduleService = schedule;

        ErpProductionArrangeServiceImpl arrange = new ErpProductionArrangeServiceImpl();
        setField(arrange, "baseMapper", session.getMapper(ErpProductionArrangeMapper.class));
        setField(arrange, "erpOrdersService", ordersService);
        arrangeService = arrange;

        ErpProductionArchiveServiceImpl archive = new ErpProductionArchiveServiceImpl();
        setField(archive, "erpOrdersService", ordersService);
        setField(archive, "erpProductionScheduleService", scheduleService);
        setField(archive, "erpProductionArrangeService", arrangeService);
        setField(archive, "erpProductionScheduleMapper", session.getMapper(ErpProductionScheduleMapper.class));
        archiveService = archive;
    }

    @After
    public void clearPageHelper() {
        PageHelper.clearPage();
    }

    @Test
    public void completedOrderDetailsArePaginatedAndFiltersParticipateInCounting() {
        String orderCode = completedOrderCodeWithProductionRecords();
        String defaultOrderCode = archiveService.requireCompletedOrderCode(orderCode);
        long expectedScheduleCount = countSchedules(defaultOrderCode);
        long expectedArrangeCount = countArranges(defaultOrderCode);

        PageHelper.startPage(1, 20);
        List<ProductionScheduleResult> schedules = archiveService.listSchedulesByOrderCode(
                defaultOrderCode,
                new ListProductionScheduleRequest()
        );
        PageInfo<ProductionScheduleResult> schedulePage = new PageInfo<>(schedules);
        assertEquals(expectedScheduleCount, schedulePage.getTotal());
        assertEquals(Math.min(20L, expectedScheduleCount), schedules.size());

        String mouldNumber = schedules.get(0).getMouldNumber();
        assertNotNull(mouldNumber);
        ListProductionScheduleRequest scheduleFilter = new ListProductionScheduleRequest();
        scheduleFilter.setMouldNumber(mouldNumber);
        PageHelper.startPage(1, 100);
        List<ProductionScheduleResult> filteredSchedules =
                archiveService.listSchedulesByOrderCode(defaultOrderCode, scheduleFilter);
        PageInfo<ProductionScheduleResult> filteredSchedulePage = new PageInfo<>(filteredSchedules);
        assertFalse(filteredSchedules.isEmpty());
        assertTrue(filteredSchedulePage.getTotal() <= expectedScheduleCount);
        assertTrue(filteredSchedules.stream().allMatch(row -> mouldNumber.equals(row.getMouldNumber())));

        PageHelper.startPage(1, 20);
        List<ErpProductionArrange> arranges = archiveService.listArrangesByOrderCode(
                defaultOrderCode,
                new ErpProductionArrange()
        );
        PageInfo<ErpProductionArrange> arrangePage = new PageInfo<>(arranges);
        assertEquals(expectedArrangeCount, arrangePage.getTotal());
        assertEquals(Math.min(20L, expectedArrangeCount), arranges.size());

        String arrangeMould = arranges.get(0).getMouldNumber();
        assertNotNull(arrangeMould);
        ErpProductionArrange arrangeFilter = new ErpProductionArrange();
        arrangeFilter.setMouldNumber(arrangeMould);
        PageHelper.startPage(1, 100);
        List<ErpProductionArrange> filteredArranges =
                archiveService.listArrangesByOrderCode(defaultOrderCode, arrangeFilter);
        PageInfo<ErpProductionArrange> filteredArrangePage = new PageInfo<>(filteredArranges);
        assertFalse(filteredArranges.isEmpty());
        assertTrue(filteredArrangePage.getTotal() <= expectedArrangeCount);
        assertTrue(filteredArranges.stream().allMatch(row -> arrangeMould.equals(row.getMouldNumber())));
    }

    @Test
    public void productionListsExcludeCompletedOrdersByDefault() {
        String orderCode = completedOrderCodeWithProductionRecords();

        ListProductionScheduleRequest activeRequest = new ListProductionScheduleRequest();
        activeRequest.setOrderCodeExact(orderCode);
        PageHelper.startPage(1, 10);
        List<ProductionScheduleResult> activeSchedules = scheduleService.listResult(activeRequest);
        assertEquals(0L, new PageInfo<>(activeSchedules).getTotal());

        ListProductionScheduleRequest allRequest = new ListProductionScheduleRequest();
        allRequest.setOrderCodeExact(orderCode);
        allRequest.setIncludeCompletedOrders(true);
        PageHelper.startPage(1, 10);
        List<ProductionScheduleResult> allSchedules = scheduleService.listResult(allRequest);
        assertTrue(new PageInfo<>(allSchedules).getTotal() > 0);

        PageHelper.clearPage();
        String defaultOrderCode = archiveService.requireCompletedOrderCode(orderCode);
        List<ErpProductionArrange> archivedArranges =
                archiveService.listArrangesByOrderCode(defaultOrderCode, new ErpProductionArrange());
        assertFalse(archivedArranges.isEmpty());
        Set<Long> archivedArrangeIds = archivedArranges.stream()
                .map(ErpProductionArrange::getId)
                .collect(Collectors.toSet());

        List<ErpProductionArrange> activeArranges =
                arrangeService.listForProductionList(new ErpProductionArrange(), false);
        Set<Long> activeArrangeIds = activeArranges.stream()
                .map(ErpProductionArrange::getId)
                .collect(Collectors.toSet());
        assertTrue(Collections.disjoint(archivedArrangeIds, activeArrangeIds));

        List<ErpProductionArrange> allArranges =
                arrangeService.listForProductionList(new ErpProductionArrange(), true);
        Set<Long> allArrangeIds = allArranges.stream()
                .map(ErpProductionArrange::getId)
                .collect(Collectors.toCollection(HashSet::new));
        assertTrue(allArrangeIds.size() >= archivedArrangeIds.size());
        assertTrue(allArrangeIds.containsAll(archivedArrangeIds));
    }

    @Test
    public void incompleteOrderCannotOpenArchiveDetails() {
        String incompleteOrderCode = jdbc.queryForObject(
                "SELECT o.inner_id FROM erp_orders o WHERE o.status <> ? ORDER BY o.id LIMIT 1",
                String.class,
                completedStatus()
        );
        assertNotNull(incompleteOrderCode);
        assertThrows(ServiceException.class,
                () -> archiveService.requireCompletedOrderCode(incompleteOrderCode));
    }

    @Test
    public void archiveMenuAndIndexesExistInTestDatabase() {
        Integer menuCount = jdbc.queryForObject(
                "SELECT COUNT(*) FROM sys_menu menu " +
                        "JOIN sys_menu parent ON parent.menu_id = menu.parent_id " +
                        "WHERE menu.menu_name = '已完成订单' " +
                        "AND menu.path = 'PMProduceArchive' " +
                        "AND parent.menu_name = '生产'",
                Integer.class
        );
        assertEquals(Integer.valueOf(1), menuCount);

        Integer indexCount = jdbc.queryForObject(
                "SELECT COUNT(DISTINCT index_name) FROM information_schema.statistics " +
                        "WHERE table_schema = DATABASE() AND index_name IN (" +
                        "'idx_erp_orders_status'," +
                        "'idx_erp_production_schedule_order_code'," +
                        "'idx_erp_production_schedule_arrange_id')",
                Integer.class
        );
        assertEquals(Integer.valueOf(3), indexCount);
    }

    private static String completedOrderCodeWithProductionRecords() {
        return jdbc.queryForObject(
                "SELECT o.inner_id FROM erp_orders o WHERE o.status = ? " +
                        "AND EXISTS (SELECT 1 FROM erp_production_schedule ps " +
                        "WHERE ps.order_code = o.inner_id AND ps.arrange_id IS NOT NULL) " +
                        "ORDER BY o.id LIMIT 1",
                String.class,
                completedStatus()
        );
    }

    private static long countSchedules(String orderCode) {
        Long count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM erp_production_schedule WHERE order_code = ?",
                Long.class,
                orderCode
        );
        return count == null ? 0L : count;
    }

    private static long countArranges(String orderCode) {
        Long count = jdbc.queryForObject(
                "SELECT COUNT(DISTINCT a.id) FROM erp_production_arrange a " +
                        "JOIN erp_production_schedule ps ON ps.arrange_id = a.id " +
                        "WHERE ps.order_code = ?",
                Long.class,
                orderCode
        );
        return count == null ? 0L : count;
    }

    private static Integer completedStatus() {
        return jdbc.queryForObject(
                "SELECT CAST(dict_value AS SIGNED) FROM sys_dict_data " +
                        "WHERE dict_type = 'erp_order_status' AND dict_label = '已完成' LIMIT 1",
                Integer.class
        );
    }

    private static IErpOrdersService ordersServiceProxy(JdbcTemplate jdbcTemplate) {
        return (IErpOrdersService) Proxy.newProxyInstance(
                CompletedOrderArchiveIntegrationTest.class.getClassLoader(),
                new Class<?>[]{IErpOrdersService.class},
                (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "getStatusValue":
                            return jdbcTemplate.queryForObject(
                                    "SELECT CAST(dict_value AS SIGNED) FROM sys_dict_data " +
                                            "WHERE dict_type = 'erp_order_status' " +
                                            "AND dict_label = ? LIMIT 1",
                                    Integer.class,
                                    args[0]
                            );
                        case "getStatusLabel":
                            return jdbcTemplate.queryForObject(
                                    "SELECT dict_label FROM sys_dict_data " +
                                            "WHERE dict_type = 'erp_order_status' " +
                                            "AND dict_value = ? LIMIT 1",
                                    String.class,
                                    String.valueOf(args[0])
                            );
                        case "requireDefaultInnerCode":
                            return resolveDefaultOrderCode(jdbcTemplate, String.valueOf(args[0]));
                        case "selectByOrderCode":
                            return findOrder(jdbcTemplate, String.valueOf(args[0]));
                        case "toString":
                            return "CompletedOrderArchiveIntegrationTestOrdersService";
                        case "hashCode":
                            return System.identityHashCode(proxy);
                        case "equals":
                            return proxy == args[0];
                        default:
                            throw new UnsupportedOperationException(method.getName());
                    }
                }
        );
    }

    private static String resolveDefaultOrderCode(JdbcTemplate jdbcTemplate, String code) {
        List<String> codes = jdbcTemplate.queryForList(
                "SELECT o.inner_id FROM erp_orders o " +
                        "WHERE o.inner_id = ? OR EXISTS (" +
                        "SELECT 1 FROM erp_order_inner_code ic " +
                        "WHERE ic.order_id = o.id AND ic.inner_code = ?) " +
                        "ORDER BY CASE WHEN o.inner_id = ? THEN 0 ELSE 1 END LIMIT 1",
                String.class,
                code,
                code,
                code
        );
        if (codes.isEmpty()) {
            throw new ServiceException("订单不存在");
        }
        return codes.get(0);
    }

    private static ErpOrders findOrder(JdbcTemplate jdbcTemplate, String code) {
        List<ErpOrders> orders = jdbcTemplate.query(
                "SELECT o.inner_id, o.status FROM erp_orders o " +
                        "WHERE o.inner_id = ? OR EXISTS (" +
                        "SELECT 1 FROM erp_order_inner_code ic " +
                        "WHERE ic.order_id = o.id AND ic.inner_code = ?) " +
                        "ORDER BY CASE WHEN o.inner_id = ? THEN 0 ELSE 1 END LIMIT 1",
                (rs, rowNum) -> {
                    ErpOrders order = new ErpOrders();
                    order.setInnerId(rs.getString("inner_id"));
                    order.setStatus(rs.getInt("status"));
                    return order;
                },
                code,
                code,
                code
        );
        return orders.isEmpty() ? null : orders.get(0);
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Class<?> type = target.getClass();
        while (type != null) {
            try {
                Field field = type.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(target, value);
                return;
            } catch (NoSuchFieldException ignored) {
                type = type.getSuperclass();
            }
        }
        throw new NoSuchFieldException(fieldName);
    }
}