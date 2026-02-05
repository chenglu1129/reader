package com.htmake.reader.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * HTTP请求工具类
 */
@Slf4j
public class HttpUtils {

    private static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType FORM_TYPE = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

    private static OkHttpClient client;

    static {
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .followRedirects(true)
                .followSslRedirects(true)
                .build();
    }

    /**
     * GET请求
     */
    public static String get(String url) throws IOException {
        return get(url, null);
    }

    /**
     * GET请求（带请求头）
     */
    public static String get(String url, Map<String, String> headers) throws IOException {
        Request.Builder builder = new Request.Builder().url(url);

        if (headers != null) {
            headers.forEach(builder::addHeader);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("请求失败: " + response.code());
            }

            ResponseBody body = response.body();
            if (body == null) {
                throw new IOException("响应体为空");
            }

            return body.string();
        }
    }

    /**
     * POST请求（JSON）
     */
    public static String postJson(String url, String json) throws IOException {
        return postJson(url, json, null);
    }

    /**
     * POST请求（JSON，带请求头）
     */
    public static String postJson(String url, String json, Map<String, String> headers) throws IOException {
        RequestBody body = RequestBody.create(json, JSON_TYPE);
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(body);

        if (headers != null) {
            headers.forEach(builder::addHeader);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("请求失败: " + response.code());
            }

            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                throw new IOException("响应体为空");
            }

            return responseBody.string();
        }
    }

    /**
     * POST请求（表单）
     */
    public static String postForm(String url, Map<String, String> formData) throws IOException {
        return postForm(url, formData, null);
    }

    /**
     * POST请求（表单，带请求头）
     */
    public static String postForm(String url, Map<String, String> formData, Map<String, String> headers)
            throws IOException {
        FormBody.Builder formBuilder = new FormBody.Builder();
        if (formData != null) {
            formData.forEach(formBuilder::add);
        }

        RequestBody body = formBuilder.build();
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(body);

        if (headers != null) {
            headers.forEach(builder::addHeader);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("请求失败: " + response.code());
            }

            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                throw new IOException("响应体为空");
            }

            return responseBody.string();
        }
    }

    /**
     * 获取OkHttpClient实例
     */
    public static OkHttpClient getClient() {
        return client;
    }

    public static String normalizeUrl(String baseUrl, String url) {
        if (url == null) {
            return null;
        }

        String u = stripUrlWrappers(url);
        if (u.isEmpty()) {
            return u;
        }

        if (u.matches("(?i)^[a-z][a-z0-9+.-]*:.*")) {
            return u;
        }

        String b = stripUrlWrappers(baseUrl);
        if (u.startsWith("//")) {
            String scheme = "http";
            if (b != null && !b.isEmpty()) {
                int idx = b.indexOf(':');
                if (idx > 0) {
                    scheme = b.substring(0, idx);
                }
            }
            return scheme + ":" + u;
        }

        if (b == null || b.isEmpty()) {
            return u;
        }

        if (!b.matches("(?i)^[a-z][a-z0-9+.-]*:.*")) {
            b = "https://" + b;
        }

        try {
            URI base = URI.create(b);
            return base.resolve(u).toString();
        } catch (Exception e) {
            return u;
        }
    }

    /**
     * 设置自定义OkHttpClient
     */
    public static void setClient(OkHttpClient customClient) {
        client = customClient;
    }

    private static String stripUrlWrappers(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim().replace("`", "");
        if (t.length() >= 2) {
            char first = t.charAt(0);
            char last = t.charAt(t.length() - 1);
            if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
                t = t.substring(1, t.length() - 1).trim();
            }
        }
        return t;
    }
}
