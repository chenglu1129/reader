package com.htmake.reader.controller;

import com.htmake.reader.entity.ReplaceRule;
import com.htmake.reader.entity.ReturnData;
import com.htmake.reader.service.ReplaceRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 替换规则Controller
 */
@Slf4j
@RestController
@RequestMapping("/reader3")
public class ReplaceRuleController {

    @Autowired
    private ReplaceRuleService replaceRuleService;

    /**
     * 获取所有替换规则
     */
    @GetMapping("/getReplaceRules")
    public ReturnData getReplaceRules(@RequestParam(value = "username", defaultValue = "default") String username) {
        try {
            List<ReplaceRule> rules = replaceRuleService.getAllRules(username);
            return ReturnData.success(rules);
        } catch (Exception e) {
            log.error("获取替换规则失败", e);
            return ReturnData.error("获取替换规则失败: " + e.getMessage());
        }
    }

    /**
     * 保存替换规则
     */
    @PostMapping("/saveReplaceRule")
    public ReturnData saveReplaceRule(@RequestBody ReplaceRule rule,
            @RequestParam(value = "username", defaultValue = "default") String username) {
        try {
            boolean success = replaceRuleService.saveRule(rule, username);
            if (success) {
                return ReturnData.success(rule);
            } else {
                return ReturnData.error("保存替换规则失败");
            }
        } catch (Exception e) {
            log.error("保存替换规则失败", e);
            return ReturnData.error("保存替换规则失败: " + e.getMessage());
        }
    }

    /**
     * 删除替换规则
     */
    @PostMapping("/deleteReplaceRule")
    public ReturnData deleteReplaceRule(@RequestParam("ruleId") Long ruleId,
            @RequestParam(value = "username", defaultValue = "default") String username) {
        try {
            boolean success = replaceRuleService.deleteRule(ruleId, username);
            if (success) {
                return ReturnData.success("删除成功");
            } else {
                return ReturnData.error("删除失败");
            }
        } catch (Exception e) {
            log.error("删除替换规则失败", e);
            return ReturnData.error("删除替换规则失败: " + e.getMessage());
        }
    }
}
