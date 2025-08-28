package org.example.projeto_ws.DTO;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(
        localName = "error"
)
public class ErrorDTO {
    @JacksonXmlProperty(
            localName = "info"
    )
    private String message;

    public ErrorDTO() {
    }

    public ErrorDTO(Exception e) {
        this.message = e.getMessage();
    }

    public ErrorDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
