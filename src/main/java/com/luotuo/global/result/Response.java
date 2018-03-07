package com.luotuo.global.result;


/**
 * Created by luotuo on 17-6-1.
 */
public class Response {
    public static final int SUCCEED=1;
    public static final int ERROR=0;
    public static final int RESOURCELOCKED=0;//资源占用

    private int success;
    private String message;
    private Object result;

    public Response(int success, String message, Object obj) {
        this.success = success;
        this.message = message;
        this.result = obj;
    }

    public Response(){}

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public void buildResponse(int success, String message, Object obj) {
        this.success = success;
        this.message = message;
        this.result = obj;
    }
}
