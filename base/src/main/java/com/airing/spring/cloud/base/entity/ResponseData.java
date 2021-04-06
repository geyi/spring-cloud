package com.airing.spring.cloud.base.entity;

import com.airing.spring.cloud.base.exception.BusinessException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 接口出参格式
 *
 * @author GEYI
 * @date 2021年03月31日 9:49
 */
public class ResponseData<T extends Serializable> extends HashMap<String, Object> implements Serializable, Cloneable {
    private String code = "status";
    private String msg = "message";
    private String data = "data";
    private ResponseData.ResponseFieldMapper fieldMapper;

    public ResponseData(Serializable code, String message, T data) {
        this.setCode(code);
        this.setMessage(message);
        this.setData(data);
    }

    public ResponseData(Serializable code, String message, Iterable<T> data) {
        this.setCode(code);
        this.setMessage(message);
        this.setData(data);
    }

    public ResponseData(Serializable code, String message, Object data) {
        this.setCode(code);
        this.setMessage(message);
        this.setData(data);
    }

    public ResponseData(Serializable code, String message) {
        this.setCode(code);
        this.setMessage(message);
    }

    public static <T extends Serializable> ResponseData data(T data) {
        return new ResponseData("200", "请求成功", data);
    }

    public static <T extends Serializable> ResponseData data(Iterable<T> data) {
        return new ResponseData("200", "请求成功", data);
    }

    public static ResponseData data(Map map) {
        return new ResponseData("200", "请求成功", map);
    }

    public static ResponseData success() {
        return new ResponseData("200", "请求成功");
    }

    public static ResponseData error(Throwable throwable) {
        if (throwable instanceof BusinessException) {
            BusinessException ex = (BusinessException) throwable;
            return new ResponseData(ex.getCode(), ex.getMessage());
        } else {
            return new ResponseData("500", "未知错误");
        }
    }

    public static <T extends Serializable> ResponseData<T> data(Serializable code, String message, T data) {
        return new ResponseData(code, message, data);
    }

    public static ResponseData noData() {
        return new ResponseData("1", "没有数据");
    }

    public ResponseData.ResponseFieldMapper getFieldMapper() {
        if (this.fieldMapper == null) {
            this.fieldMapper = new ResponseData.ResponseFieldMapper(code, msg, data);
        }

        return this.fieldMapper;
    }

    public void setFieldMapper(ResponseData.ResponseFieldMapper fieldMapper) {
        Object obj = this.remove(this.fieldMapper.codeKey);
        this.put(fieldMapper.codeKey, obj);
        obj = this.remove(this.fieldMapper.msgKey);
        this.put(fieldMapper.msgKey, obj);
        obj = this.remove(this.fieldMapper.dataKey);
        this.put(fieldMapper.dataKey, obj);
        this.fieldMapper = fieldMapper;
    }

    public Serializable getCode() {
        return (Serializable) super.get(this.getFieldMapper().codeKey);
    }

    public Object getData() {
        return super.get(this.getFieldMapper().dataKey);
    }

    public String getMessage() {
        return (String) super.get(this.getFieldMapper().msgKey);
    }

    public Object getProperty(String key) {
        return super.get(key);
    }

    public ResponseData<T> setProperty(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public void setCode(Serializable code) {
        super.put(this.getFieldMapper().codeKey, code);
    }

    public void setMessage(String message) {
        super.put(this.getFieldMapper().msgKey, message);
    }

    public void setData(Object data) {
        super.put(this.getFieldMapper().dataKey, data);
    }

    public static class ResponseFieldMapper {
        private String codeKey;
        private String msgKey;
        private String dataKey;

        public ResponseFieldMapper(String codeKey, String msgKey, String dataKey) {
            this.codeKey = codeKey;
            this.msgKey = msgKey;
            this.dataKey = dataKey;
        }
    }
}
