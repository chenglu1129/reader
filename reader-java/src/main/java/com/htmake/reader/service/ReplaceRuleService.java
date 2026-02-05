package com.htmake.reader.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.htmake.reader.entity.ReplaceRule;
import com.htmake.reader.utils.StorageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 替换规则服务
 */
@Slf4j
@Service
public class ReplaceRuleService {

    private static final Gson GSON = new Gson();
    private static final AtomicLong ID_GENERATOR = new AtomicLong(System.currentTimeMillis());

    @Autowired
    private StorageHelper storageHelper;

    /**
     * 获取所有替换规则
     */
    public List<ReplaceRule> getAllRules(String username) {
        String path = getRulesPath(username);
        File file = new File(path);

        if (!file.exists()) {
            return new ArrayList<>();
        }

        String json = storageHelper.readFile(path);
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            Type listType = new TypeToken<List<ReplaceRule>>() {
            }.getType();
            List<ReplaceRule> rules = GSON.fromJson(json, listType);
            return rules != null ? rules : new ArrayList<>();
        } catch (Exception e) {
            log.error("解析替换规则失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取启用的替换规则
     */
    public List<ReplaceRule> getEnabledRules(String username) {
        return getAllRules(username).stream()
                .filter(rule -> rule.getIsEnabled() != null && rule.getIsEnabled())
                .sorted((a, b) -> a.getOrder().compareTo(b.getOrder()))
                .collect(Collectors.toList());
    }

    /**
     * 保存替换规则
     */
    public boolean saveRule(ReplaceRule rule, String username) {
        if (rule == null) {
            return false;
        }

        // 如果是新规则，生成ID
        if (rule.getId() == null || rule.getId() == 0) {
            rule.setId(ID_GENERATOR.incrementAndGet());
        }

        List<ReplaceRule> rules = getAllRules(username);

        boolean found = false;
        for (int i = 0; i < rules.size(); i++) {
            if (rules.get(i).getId().equals(rule.getId())) {
                rules.set(i, rule);
                found = true;
                break;
            }
        }

        if (!found) {
            rules.add(rule);
        }

        return saveAllRules(rules, username);
    }

    /**
     * 删除替换规则
     */
    public boolean deleteRule(Long ruleId, String username) {
        if (ruleId == null) {
            return false;
        }

        List<ReplaceRule> rules = getAllRules(username);
        rules.removeIf(rule -> ruleId.equals(rule.getId()));

        return saveAllRules(rules, username);
    }

    /**
     * 保存所有规则
     */
    private boolean saveAllRules(List<ReplaceRule> rules, String username) {
        String path = getRulesPath(username);
        String json = GSON.toJson(rules);
        return storageHelper.writeFile(path, json);
    }

    /**
     * 获取规则文件路径
     */
    private String getRulesPath(String username) {
        return storageHelper.getUserDataPath(username) + File.separator + "replaceRules.json";
    }
}
