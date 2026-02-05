package com.htmake.reader.controller;

import com.htmake.reader.config.ReaderConfig;
import com.htmake.reader.entity.ReturnData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统信息Controller
 */
@RestController
@RequestMapping("/reader3")
public class SystemController {

    @Autowired
    private ReaderConfig readerConfig;

    /**
     * 获取系统信息
     */
    @GetMapping("/getInfo")
    public ReturnData getInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("version", "3.0.0");
        info.put("appName", "Reader");
        info.put("secure", readerConfig.getSecure());
        info.put("userLimit", readerConfig.getUserLimit());
        return ReturnData.success(info);
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ReturnData health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("message", "Reader is running");
        return ReturnData.success(status);
    }
}
