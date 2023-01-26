package com.cv.demo.responcestatus;

import org.json.JSONObject;

public class NotificationMessage {
    public static final String ERROR = "error";
    public static final String WARNING = "warning";
    public static final String INFO = "info";
    public static final String SUCCESS = "success";

    String type;
    String message;

    public NotificationMessage(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public boolean isSuccess() {
        return SUCCESS.equalsIgnoreCase(type);
    }

    public boolean isErrorOrWarning() {
        return ERROR.equalsIgnoreCase(type) || WARNING.equalsIgnoreCase(type);
    }

    public String getText() {
        return message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @deprecated dont use. instead return object directly.
     */
    @Deprecated
    public String toJSONString() {
        JSONObject responseJSON = new JSONObject();
        responseJSON.put("type", type);
        responseJSON.put("status", type);
        responseJSON.put("message", message);
        return responseJSON.toString(2);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
