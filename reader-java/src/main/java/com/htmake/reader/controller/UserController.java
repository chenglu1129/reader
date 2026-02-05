package com.htmake.reader.controller;

import com.htmake.reader.entity.ReturnData;
import com.htmake.reader.entity.User;
import com.htmake.reader.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理Controller
 */
@Slf4j
@RestController
@RequestMapping("/reader3")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ReturnData login(@RequestBody Map<String, String> loginData) {
        try {
            String username = loginData.get("username");
            String password = loginData.get("password");

            if (username == null || username.isEmpty()) {
                return ReturnData.error("用户名不能为空");
            }
            if (password == null || password.isEmpty()) {
                return ReturnData.error("密码不能为空");
            }

            User user = userService.login(username, password);
            if (user == null) {
                return ReturnData.error("用户名或密码错误");
            }

            // 返回用户信息（不包含密码）
            Map<String, Object> result = new HashMap<>();
            result.put("username", user.getUsername());
            result.put("isAdmin", user.getIsAdmin());
            result.put("enableWebdav", user.getEnableWebdav());
            result.put("enableLocalStore", user.getEnableLocalStore());
            result.put("enableBookSource", user.getEnableBookSource());
            result.put("enableRssSource", user.getEnableRssSource());

            return ReturnData.success(result);
        } catch (Exception e) {
            log.error("用户登录失败", e);
            return ReturnData.error("登录失败: " + e.getMessage());
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ReturnData register(@RequestBody Map<String, String> registerData) {
        try {
            String username = registerData.get("username");
            String password = registerData.get("password");

            if (username == null || username.isEmpty()) {
                return ReturnData.error("用户名不能为空");
            }
            if (password == null || password.isEmpty()) {
                return ReturnData.error("密码不能为空");
            }

            // 用户名长度验证
            if (username.length() < 3 || username.length() > 20) {
                return ReturnData.error("用户名长度必须在3-20个字符之间");
            }

            // 密码长度验证
            if (password.length() < 6) {
                return ReturnData.error("密码长度不能少于6个字符");
            }

            boolean success = userService.register(username, password);
            if (success) {
                return ReturnData.success("注册成功");
            } else {
                return ReturnData.error("注册失败，用户名可能已存在或已达到用户数量限制");
            }
        } catch (Exception e) {
            log.error("用户注册失败", e);
            return ReturnData.error("注册失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/getUserInfo")
    public ReturnData getUserInfo(@RequestParam("username") String username) {
        try {
            if (username == null || username.isEmpty()) {
                return ReturnData.error("用户名不能为空");
            }

            User user = userService.getUserByUsername(username);
            if (user == null) {
                return ReturnData.error("用户不存在");
            }

            // 返回用户信息（不包含密码）
            Map<String, Object> result = new HashMap<>();
            result.put("username", user.getUsername());
            result.put("isAdmin", user.getIsAdmin());
            result.put("enableWebdav", user.getEnableWebdav());
            result.put("enableLocalStore", user.getEnableLocalStore());
            result.put("enableBookSource", user.getEnableBookSource());
            result.put("enableRssSource", user.getEnableRssSource());
            result.put("bookSourceLimit", user.getBookSourceLimit());
            result.put("bookLimit", user.getBookLimit());
            result.put("createTime", user.getCreateTime());
            result.put("lastLoginTime", user.getLastLoginTime());

            return ReturnData.success(result);
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return ReturnData.error("获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有用户（仅管理员）
     */
    @GetMapping("/getAllUsers")
    public ReturnData getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();

            // 移除密码信息
            users.forEach(user -> user.setPassword(null));

            return ReturnData.success(users);
        } catch (Exception e) {
            log.error("获取用户列表失败", e);
            return ReturnData.error("获取用户列表失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户信息
     */
    @PostMapping("/updateUser")
    public ReturnData updateUser(@RequestBody User user) {
        try {
            if (user == null || user.getUsername() == null || user.getUsername().isEmpty()) {
                return ReturnData.error("用户信息不完整");
            }

            // 获取现有用户
            User existingUser = userService.getUserByUsername(user.getUsername());
            if (existingUser == null) {
                return ReturnData.error("用户不存在");
            }

            // 只更新允许修改的字段
            if (user.getEnableWebdav() != null) {
                existingUser.setEnableWebdav(user.getEnableWebdav());
            }
            if (user.getEnableLocalStore() != null) {
                existingUser.setEnableLocalStore(user.getEnableLocalStore());
            }
            if (user.getEnableBookSource() != null) {
                existingUser.setEnableBookSource(user.getEnableBookSource());
            }
            if (user.getEnableRssSource() != null) {
                existingUser.setEnableRssSource(user.getEnableRssSource());
            }

            boolean success = userService.saveUser(existingUser);
            if (success) {
                return ReturnData.success("更新成功");
            } else {
                return ReturnData.error("更新失败");
            }
        } catch (Exception e) {
            log.error("更新用户信息失败", e);
            return ReturnData.error("更新用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PostMapping("/changePassword")
    public ReturnData changePassword(@RequestBody Map<String, String> passwordData) {
        try {
            String username = passwordData.get("username");
            String oldPassword = passwordData.get("oldPassword");
            String newPassword = passwordData.get("newPassword");

            if (username == null || username.isEmpty()) {
                return ReturnData.error("用户名不能为空");
            }
            if (oldPassword == null || oldPassword.isEmpty()) {
                return ReturnData.error("旧密码不能为空");
            }
            if (newPassword == null || newPassword.isEmpty()) {
                return ReturnData.error("新密码不能为空");
            }
            if (newPassword.length() < 6) {
                return ReturnData.error("新密码长度不能少于6个字符");
            }

            // 验证旧密码
            User user = userService.login(username, oldPassword);
            if (user == null) {
                return ReturnData.error("旧密码错误");
            }

            // 更新密码
            user.setPassword(com.htmake.reader.utils.MD5Utils.md5Encode(newPassword));
            boolean success = userService.saveUser(user);

            if (success) {
                return ReturnData.success("密码修改成功");
            } else {
                return ReturnData.error("密码修改失败");
            }
        } catch (Exception e) {
            log.error("修改密码失败", e);
            return ReturnData.error("修改密码失败: " + e.getMessage());
        }
    }
}
