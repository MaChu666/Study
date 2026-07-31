package com.machugit.entity.vo;


public class ResponseVO<T> {
    private String status;
    private Integer code;
    private String info;
    private T data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }



    public static <T> ResponseVO<T> ok() {
        ResponseVO<T> vo = new ResponseVO<>();
        vo.setStatus("success");
        vo.setCode(200);
        vo.setInfo("ok");
        return vo;
    }

    public static <T> ResponseVO<T> fail(String message) {
        ResponseVO<T> vo = new ResponseVO<>();
        vo.setStatus("fail");
        vo.setCode(500);
        vo.setInfo(message);
        return vo;
    }

    public static <T> ResponseVO<T> success(T data) {
        ResponseVO<T> vo = new ResponseVO<>();
        vo.setStatus("success");
        vo.setCode(200);
        vo.setInfo("\u6210\u529f");
        vo.setData(data);
        return vo;
    }
}
