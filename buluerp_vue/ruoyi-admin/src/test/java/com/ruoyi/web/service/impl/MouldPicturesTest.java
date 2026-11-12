package com.ruoyi.web.service.impl;

import com.ruoyi.common.config.RuoYiConfig;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.exception.excel.ListValidationException;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.web.controller.erp.ErpMouldController;
import com.ruoyi.web.domain.ErpManufacturer;
import com.ruoyi.web.domain.ErpMould;
import com.ruoyi.web.mapper.ErpManufacturerMapper;
import com.ruoyi.web.mapper.ErpMouldMapper;
import com.ruoyi.web.request.mould.AddMouldRequest;
import com.ruoyi.web.request.mould.UpdateMouldRequest;
import com.ruoyi.web.service.IErpMouldService;
import com.ruoyi.web.service.support.MouldImages;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.Configuration;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.imageio.ImageIO;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class MouldPicturesTest {
    @Rule public TemporaryFolder folder = new TemporaryFolder();
    private String previousProfile;
    private ValidatorFactory validatorFactory;

    @Before public void setup() {
        previousProfile = RuoYiConfig.getProfile();
        new RuoYiConfig().setProfile(folder.getRoot().getAbsolutePath().replace('\\', '/'));
        TransactionSynchronizationManager.initSynchronization();
        validatorFactory = Validation.buildDefaultValidatorFactory();
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(new Configuration(), "mould-picture-tests");
        assistant.setCurrentNamespace(ErpMouldMapper.class.getName());
        TableInfoHelper.initTableInfo(assistant, ErpMould.class);
    }

    @After public void cleanup() {
        finish(false);
        new RuoYiConfig().setProfile(previousProfile);
        validatorFactory.close();
    }

    @Test public void multipartCreateAndPutBindFilesAndIgnoreClientImageUrls() throws Exception {
        AtomicReference<Object> received = new AtomicReference<>();
        MockMvc mvc = MockMvcBuilders.standaloneSetup(controller((p, method, args) -> {
            received.set(args[0]); return null;
        })).build();
        MvcResult create = mvc.perform(multipart("/system/mould/with-manufacturer").file(image())
                .param("mouldNumber", "M001").param("manufacturerName", "厂商A")
                .param("remark", "优先生产").param("pictureUrl", "/profile/another-file.png")).andReturn();
        assertNull(create.getResolvedException());
        AddMouldRequest add = (AddMouldRequest) received.get();
        assertArrayEquals(image().getBytes(), add.getPicture().getBytes());
        assertEquals("优先生产", add.getRemark());
        assertNull(add.getPictureUrl());

        MvcResult update = mvc.perform(multipart("/system/mould").file(image()).param("id", "12")
                .param("deletePicture", "true").param("trialDate", "2026-09-15")
                .param("remark", "编辑备注")
                .with(request -> { request.setMethod("PUT"); return request; })).andReturn();
        assertNull(update.getResolvedException());
        UpdateMouldRequest edit = (UpdateMouldRequest) received.get();
        assertEquals(Long.valueOf(12), edit.getId());
        assertNotNull(edit.getTrialDate());
        assertNotNull(edit.getPicture());
        assertTrue(edit.getDeletePicture());
        assertEquals("编辑备注", edit.getRemark());
    }

    @Test public void oldJsonCreateAndUpdateRemainSupported() throws Exception {
        AtomicReference<Object> received = new AtomicReference<>();
        MockMvc mvc = MockMvcBuilders.standaloneSetup(controller((p, method, args) -> {
            received.set(args[0]); return null;
        })).build();
        assertNull(mvc.perform(post("/system/mould/with-manufacturer").contentType(MediaType.APPLICATION_JSON)
                .content("{\"mouldNumber\":\"M001\",\"manufacturerName\":\"A\",\"pictureUrl\":\"/profile/other.png\"}"))
                .andReturn().getResolvedException());
        assertNull(((AddMouldRequest) received.get()).getPictureUrl());
        assertNull(mvc.perform(put("/system/mould").contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":12,\"status\":\"已外发\"}")).andReturn().getResolvedException());
        assertNull(((UpdateMouldRequest) received.get()).getPicture());
    }

    @Test public void importsEmbeddedPicturesForBothExcelFormatsWithoutShiftingRows() throws Exception {
        for (boolean xlsx : new boolean[]{true, false}) {
            List<AddMouldRequest> rows = parse(workbook(xlsx, true, false));
            assertEquals(3, rows.size());
            assertEquals("M001", rows.get(0).getMouldNumber());
            assertEquals("备注1", rows.get(0).getRemark());
            assertArrayEquals(image().getBytes(), Files.readAllBytes(path(rows.get(0).getPictureUrl())));
            assertTrue(rows.get(1).getPictureUrl() == null || rows.get(1).getPictureUrl().isEmpty());
            assertArrayEquals(image().getBytes(), Files.readAllBytes(path(rows.get(2).getPictureUrl())));
        }
    }

    @Test public void oldTemplateWithoutPictureColumnStillImports() throws Exception {
        List<AddMouldRequest> rows = parse(workbook(true, false, false));
        assertEquals(3, rows.size());
        for (AddMouldRequest row : rows) assertNull(row.getPictureUrl());
    }

    @Test public void templateContainsOptionalImageColumnAndExportEmbedsStoredImage() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        controller((p, method, args) -> null).exportTemplate(response);
        try (Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(response.getContentAsByteArray()))) {
            Row header = wb.getSheetAt(0).getRow(0);
            assertEquals(4, header.getLastCellNum());
            assertEquals("模具图片", header.getCell(2).getStringCellValue());
            assertEquals("备注", header.getCell(3).getStringCellValue());
        }
        ErpMould mould = new ErpMould();
        mould.setMouldNumber("M001");
        mould.setPictureUrl(MouldImages.upload(image()));
        response = new MockHttpServletResponse();
        new ExcelUtil<>(ErpMould.class).exportExcel(response, Collections.singletonList(mould), "模具列表");
        try (Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(response.getContentAsByteArray()))) {
            assertEquals(1, wb.getAllPictures().size());
            assertArrayEquals(image().getBytes(), wb.getAllPictures().get(0).getData());
        }
    }

    @Test public void importFailureCleansPicturesIncludingValidationFailures() throws Exception {
        ErpMouldController invalid = controller((p, method, args) -> { throw new AssertionError("Invalid rows must not write"); });
        assertThrows(ListValidationException.class, () -> invalid.importExcelWithManufacturer(workbook(true, true, true)));
        assertTrue(fileCount() > 0);
        finish(false);
        assertEquals(0, fileCount());

        TransactionSynchronizationManager.initSynchronization();
        ErpMouldController failed = controller((p, method, args) -> { throw new ServiceException("Database failure"); });
        assertThrows(ServiceException.class, () -> failed.importExcelWithManufacturer(workbook(true, true, false)));
        finish(false);
        assertEquals(0, fileCount());
    }

    @Test public void batchImportPersistsImageUrlsAndKeepsRowsWithoutImages() throws Exception {
        List<AddMouldRequest> rows = parse(workbook(true, true, false));
        AtomicReference<List<ErpMould>> inserted = new AtomicReference<>();
        ErpMouldServiceImpl service = service((p, method, args) -> {
            if (method.getName().equals("selectList")) return Collections.emptyList();
            assertEquals("insertBatch", method.getName());
            inserted.set((List<ErpMould>) args[0]);
            return ((List<?>) args[0]).size();
        });
        ErpManufacturer manufacturer = new ErpManufacturer();
        manufacturer.setName("A"); manufacturer.setId(9L);
        ReflectionTestUtils.setField(service, "manufacturerMapper", proxy(ErpManufacturerMapper.class,
                (p, method, args) -> Collections.singletonList(manufacturer)));
        service.importWithManufacturer(rows);
        for (int i = 0; i < rows.size(); i++) {
            assertEquals(rows.get(i).getPictureUrl(), inserted.get().get(i).getPictureUrl());
        }
    }

    @Test public void createSavesUploadedImage() throws Exception {
        AtomicReference<ErpMould> inserted = new AtomicReference<>();
        ErpMouldServiceImpl service = service((p, method, args) -> {
            if (method.getName().equals("selectCount")) return 0L;
            assertEquals("insert", method.getName());
            inserted.set((ErpMould) args[0]); return 1;
        });
        ErpManufacturer manufacturer = new ErpManufacturer();
        manufacturer.setName("A"); manufacturer.setId(9L);
        ReflectionTestUtils.setField(service, "manufacturerMapper", proxy(ErpManufacturerMapper.class,
                (p, method, args) -> Collections.singletonList(manufacturer)));
        AddMouldRequest request = new AddMouldRequest();
        request.setMouldNumber("M001"); request.setManufacturerName("A"); request.setPicture(image());
        service.addWithManufacturer(request);
        assertArrayEquals(image().getBytes(), Files.readAllBytes(path(inserted.get().getPictureUrl())));
    }

    @Test public void editingWithoutAFilePreservesOriginalImage() throws Exception {
        ErpMould original = original();
        ErpMouldServiceImpl service = service((p, method, args) -> {
            if (method.getName().equals("selectById")) return original;
            assertEquals("updateById", method.getName());
            assertNull(((ErpMould) args[0]).getPictureUrl()); return 1;
        });
        UpdateMouldRequest request = update(); request.setStatus(ErpMould.STATUS_DISTRIBUTED);
        service.update(request);
        finish(true);
        assertTrue(Files.exists(path(original.getPictureUrl())));
    }

    @Test public void replacementWinsOverDeleteAndOldImageIsDeletedOnlyAfterCommit() throws Exception {
        ErpMould original = original();
        AtomicReference<ErpMould> changed = new AtomicReference<>();
        ErpMouldServiceImpl service = updating(original, changed, false);
        UpdateMouldRequest request = update(); request.setPicture(image()); request.setDeletePicture(true);
        service.update(request);
        assertTrue(Files.exists(path(original.getPictureUrl())));
        assertTrue(Files.exists(path(changed.get().getPictureUrl())));
        finish(true);
        assertFalse(Files.exists(path(original.getPictureUrl())));
        assertTrue(Files.exists(path(changed.get().getPictureUrl())));
    }

    @Test public void failedReplacementPreservesOldImageAndRemovesNewUpload() throws Exception {
        ErpMould original = original();
        AtomicReference<ErpMould> changed = new AtomicReference<>();
        ErpMouldServiceImpl service = updating(original, changed, true);
        UpdateMouldRequest request = update(); request.setPicture(image());
        assertThrows(ServiceException.class, () -> service.update(request));
        finish(false);
        assertTrue(Files.exists(path(original.getPictureUrl())));
        assertFalse(Files.exists(path(changed.get().getPictureUrl())));
    }

    @Test public void pictureOnlyDeletionDoesNotIssueEmptyUpdateAndWaitsForCommit() throws Exception {
        ErpMould original = original();
        ErpMouldServiceImpl service = service((p, method, args) -> {
            if (method.getName().equals("selectById")) return original;
            assertEquals("clearPicture", method.getName()); assertEquals(12L, args[0]); return 1;
        });
        UpdateMouldRequest request = update(); request.setDeletePicture(true);
        service.update(request);
        assertTrue(Files.exists(path(original.getPictureUrl())));
        finish(true);
        assertFalse(Files.exists(path(original.getPictureUrl())));
    }

    @Test public void nonImageUploadsAreRejectedWithoutWritingFiles() {
        assertThrows(ServiceException.class, () -> MouldImages.upload(
                new MockMultipartFile("picture", "script.txt", "text/plain", new byte[]{1})));
        assertEquals(0, fileCount());
    }

    @Test public void mapperIncludesPictureForReadsAndBatchInsertsButNotStatusUpdates() throws Exception {
        Configuration configuration = new Configuration();
        String resource = "mapper/web/ErpMouldMapper.xml";
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(resource)) {
            new XMLMapperBuilder(input, configuration, resource, configuration.getSqlFragments()).parse();
        }
        String prefix = ErpMouldMapper.class.getName() + ".";
        assertTrue(configuration.getMappedStatement(prefix + "findMouldInfoById").getBoundSql("M001").getSql().contains("em.picture_url"));
        Map<String, Object> params = new HashMap<>();
        params.put("moulds", Collections.singletonList(new ErpMould()));
        BoundSql insert = configuration.getMappedStatement(prefix + "insertBatch").getBoundSql(params);
        assertTrue(insert.getSql().contains("picture_url"));
        assertTrue(insert.getParameterMappings().stream().anyMatch(p -> p.getProperty().endsWith(".pictureUrl")));
        params.put("ids", Collections.singletonList(12L)); params.put("status", ErpMould.STATUS_ACCEPTED);
        assertFalse(configuration.getMappedStatement(prefix + "updateBatchStatus").getBoundSql(params).getSql().contains("picture_url"));
    }

    private ErpMould original() throws Exception {
        ErpMould mould = new ErpMould(); mould.setId(12L);
        Path old = Paths.get(RuoYiConfig.getUploadPath(), "old.png");
        Files.createDirectories(old.getParent()); Files.write(old, image().getBytes());
        mould.setPictureUrl("/profile/upload/old.png"); return mould;
    }

    private ErpMouldServiceImpl updating(ErpMould original, AtomicReference<ErpMould> changed, boolean fail) {
        return service((p, method, args) -> {
            if (method.getName().equals("selectById")) return original;
            assertEquals("updateById", method.getName()); changed.set((ErpMould) args[0]);
            if (fail) throw new ServiceException("Database failure");
            return 1;
        });
    }

    private UpdateMouldRequest update() { UpdateMouldRequest request = new UpdateMouldRequest(); request.setId(12L); return request; }
    private Path path(String url) { return Paths.get(RuoYiConfig.getProfile()).resolve(url.substring("/profile/".length())); }
    private long fileCount() {
        try (java.util.stream.Stream<Path> files = Files.walk(folder.getRoot().toPath())) { return files.filter(Files::isRegularFile).count(); }
        catch (IOException e) { throw new AssertionError(e); }
    }
    private void finish(boolean commit) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) return;
        List<TransactionSynchronization> callbacks = TransactionSynchronizationManager.getSynchronizations();
        if (commit) callbacks.forEach(TransactionSynchronization::afterCommit);
        callbacks.forEach(c -> c.afterCompletion(commit ? TransactionSynchronization.STATUS_COMMITTED : TransactionSynchronization.STATUS_ROLLED_BACK));
        TransactionSynchronizationManager.clearSynchronization();
    }
    private ErpMouldServiceImpl service(InvocationHandler handler) {
        ErpMouldServiceImpl service = new ErpMouldServiceImpl();
        ErpMouldMapper mapper = proxy(ErpMouldMapper.class, handler);
        ReflectionTestUtils.setField(service, "baseMapper", mapper);
        ReflectionTestUtils.setField(service, "erpMouldMapper", mapper); return service;
    }
    private ErpMouldController controller(InvocationHandler handler) {
        ErpMouldController controller = new ErpMouldController();
        ReflectionTestUtils.setField(controller, "mouldService", proxy(IErpMouldService.class, handler));
        ListValidationServiceImpl validation = new ListValidationServiceImpl();
        ReflectionTestUtils.setField(validation, "validator", validatorFactory.getValidator());
        ReflectionTestUtils.setField(controller, "listValidationService", validation); return controller;
    }
    private List<AddMouldRequest> parse(MockMultipartFile file) throws Exception {
        AtomicReference<List<AddMouldRequest>> rows = new AtomicReference<>();
        controller((p, method, args) -> { rows.set((List<AddMouldRequest>) args[0]); return null; }).importExcelWithManufacturer(file);
        return rows.get();
    }
    private static <T> T proxy(Class<T> type, InvocationHandler handler) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, handler));
    }
    private MockMultipartFile image() throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB), "png", bytes);
        return new MockMultipartFile("picture", "mould.png", "image/png", bytes.toByteArray());
    }
    private MockMultipartFile workbook(boolean xlsx, boolean includePicture, boolean invalid) throws IOException {
        try (Workbook wb = xlsx ? new XSSFWorkbook() : new HSSFWorkbook(); ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("模具"); Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("模具编号"); header.createCell(1).setCellValue("生产商名称");
            if (includePicture) header.createCell(2).setCellValue("模具图片");
            header.createCell(3).setCellValue("备注");
            Drawing<?> drawing = sheet.createDrawingPatriarch();
            for (int i = 1; i <= 3; i++) {
                Row row = sheet.createRow(i); row.createCell(0).setCellValue(invalid ? "" : "M00" + i);
                row.createCell(1).setCellValue("A");
                row.createCell(3).setCellValue("备注" + i);
                if (includePicture && i != 2) {
                    ClientAnchor anchor = wb.getCreationHelper().createClientAnchor();
                    anchor.setRow1(i); anchor.setRow2(i + 1); anchor.setCol1(2); anchor.setCol2(3);
                    drawing.createPicture(anchor, wb.addPicture(image().getBytes(), Workbook.PICTURE_TYPE_PNG));
                }
            }
            wb.write(bytes);
            return new MockMultipartFile("file", xlsx ? "moulds.xlsx" : "moulds.xls", "application/octet-stream", bytes.toByteArray());
        }
    }
}
