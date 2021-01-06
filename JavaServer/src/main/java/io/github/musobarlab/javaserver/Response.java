package io.github.musobarlab.javaserver;

public class Response {

    private Integer code;
    private Boolean success;
    private Object data;
    private String message;

    public Response() {

    }

    public Response(Integer code, Boolean success, Object data, String message) {
        this.code = code;
        this.success = success;
        this.data = data;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Boolean isSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
