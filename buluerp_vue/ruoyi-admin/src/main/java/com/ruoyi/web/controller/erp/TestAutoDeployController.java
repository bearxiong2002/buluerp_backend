package com.ruoyi.web.controller.erp;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @BelongsProject: buluerp_backend
 * @BelongsPackage: com.ruoyi.web.controller.erp
 * @Author: 陈俊曦
 * @CreateTime: 2025-04-25  13:19
 * @Description: TODO
 * @Version: 1.0
 */
@RestController
@RequestMapping("/erp/test")
public class TestAutoDeployController {

    @ApiOperation(value = "自动部署测试接口", notes = "自动部署测试接口")
    @GetMapping("/helloWorld")
    public String helloWorld() {
        return "自动部署测试";
    }
}
