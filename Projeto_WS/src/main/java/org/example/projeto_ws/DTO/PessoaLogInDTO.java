package org.example.projeto_ws.DTO;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(
        localName = "login"
)

@JsonPropertyOrder({  "email","password"})
public class PessoaLogInDTO {
    @JacksonXmlProperty(
            localName = "email"
    )
    private String email;
    @JacksonXmlProperty(
            localName = "password"
    )
    private String password;


    public PessoaLogInDTO() {
    }

    public PessoaLogInDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

