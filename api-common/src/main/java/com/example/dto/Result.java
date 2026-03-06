package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = -1323317682831309838L;

    /**
     * 状态码 (200: 成功, 500: 异常, 401: 未授权)
     */
    private int code;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 成功返回 - 带数据
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    /**
     * 成功返回 - 不帶数据
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 失败返回 - 自定义信息
     */
    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * 失败返回
     */
    public static <T> Result<T> error(String message) {
        return error(500, message);
    }

    /**
     * 判斷是否成功
     */
    public boolean isSuccess() {
        return this.code == 200;
    }
}
