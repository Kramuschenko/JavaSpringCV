package com.cv.demo.responcestatus;

import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
public class ServerResponse {
    private Object data;
    private Integer httpStatus;
    private String type;
    private String msg;

    public ServerResponse() {
    }

    public ServerResponse(Integer httpStatus) {
        this.httpStatus = httpStatus;
    }

    public ServerResponse data(Object data) {
        this.data = data;
        return this;
    }

    public ServerResponse type(String type) {
        this.type = type;
        return this;
    }

    public ServerResponse msg(String msg) {
        this.msg = msg;
        return this;
    }

    public ServerResponse httpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus.value();
        return this;
    }

    public ResponseEntity<ServerResponse> build() {
        ServerResponse e = new ServerResponse(this.httpStatus);
        e.setMsg(this.msg);
        e.setType(this.type);
        e.setHttpStatus(this.httpStatus);
        e.setData(this.data);

        return new ResponseEntity<>(e, new HttpHeaders(), org.springframework.http.HttpStatus.valueOf(this.httpStatus));
    }

    public ResponseEntity<ServerResponse> success() {
        if (this.httpStatus == null) {
            this.httpStatus = HttpStatus.OK.value();
        }
        return this
                .httpStatus(HttpStatus.valueOf(this.httpStatus))
                .build();
    }

    public ResponseEntity<ServerResponse> successNotification(String msg) {
        if (this.httpStatus == null) {
            this.httpStatus = HttpStatus.OK.value();
        }
        return this
                .httpStatus(HttpStatus.valueOf(this.httpStatus))
                .type("NOTIFICATION")
                .data(new NotificationMessage(NotificationMessage.SUCCESS, msg))
                .build();
    }

    public ResponseEntity<ServerResponse> warningNotification(String msg) {
        if (this.httpStatus == null) {
            this.httpStatus = HttpStatus.OK.value();
        }
        return this
                .httpStatus(HttpStatus.valueOf(this.httpStatus))
                .type("NOTIFICATION")
                .data(new NotificationMessage(NotificationMessage.WARNING, msg))
                .build();
    }

    public ResponseEntity<ServerResponse> successData(Object data) {
        if (this.httpStatus == null) {
            this.httpStatus = HttpStatus.OK.value();
        }
        return this
                .httpStatus(HttpStatus.valueOf(this.httpStatus))
                .data(data)
                .build();
    }

    public ResponseEntity<ServerResponse> errorNotification(String msg) {
        if (this.httpStatus == null) {
            this.httpStatus = HttpStatus.BAD_REQUEST.value();
        }
        return this
                .httpStatus(HttpStatus.valueOf(this.httpStatus))
                .type("NOTIFICATION")
                .data(new NotificationMessage(NotificationMessage.ERROR, msg))
                .build();
    }

}
