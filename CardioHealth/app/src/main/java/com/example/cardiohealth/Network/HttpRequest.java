package com.example.cardiohealth.Network;

public class HttpRequest {
    private HttpRequestType type;
    private String url;
    private String body;

    public HttpRequest(HttpRequestType type, String url, String body) {
        this.type = type;
        this.url = url;
        this.body = body;
    }

    public HttpRequestType getType() {
        return type;
    }

    public void setType(HttpRequestType type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
