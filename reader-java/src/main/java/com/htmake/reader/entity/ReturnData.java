package com.htmake.reader.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一返回结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReturnData {

    /** 是否成功 */
    private Boolean isSuccess;

    /** 错误消息 */
    private String errorMsg;

    /** 返回数据 */
    private Object data;

    public static ReturnData success(Object data) {
        return new ReturnData(true, "", data);
    }

    public static ReturnData success() {
        return new ReturnData(true, "", null);
    }

    public static ReturnData error(String errorMsg) {
        return new ReturnData(false, errorMsg, null);
    }
}
