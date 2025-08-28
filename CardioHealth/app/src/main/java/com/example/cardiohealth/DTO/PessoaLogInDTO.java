package com.example.cardiohealth.DTO;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;


@Order(elements = {"email","password"})
@Root(name =  "login")
public class PessoaLogInDTO {
    @Element(name = "email")
    private String email;
    @Element(name = "password")
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
