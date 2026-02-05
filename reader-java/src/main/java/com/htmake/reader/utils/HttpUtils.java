package com.htmake.reader.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
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

    /**
     * 设置自定义OkHttpClient
     */
    public static void setClient(OkHttpClient customClient) {
        client = customClient;
    }
}
