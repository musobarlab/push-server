package io.github.musobarlab.javaserver;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;

import java.util.Date;

public class Notification {

    private String header;
    private String content;
    private Date date;

    public Notification() {

    }

    public Notification(String header, String content, Date date) {
        this.header = header;
        this.content = content;
        this.date = date;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public byte[] toJson() {
        return Json.encode(this).getBytes();
    }

    public static Notification fromBuffer(Buffer buffer) {
        return Json.decodeValue(buffer, Notification.class);
    }
}
