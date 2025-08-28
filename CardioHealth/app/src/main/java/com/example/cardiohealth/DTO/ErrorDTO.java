package com.example.cardiohealth.DTO;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "error")
public class ErrorDTO {
    @Element(name = "info")
    private String message;

    public ErrorDTO() {
    }

    public ErrorDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
