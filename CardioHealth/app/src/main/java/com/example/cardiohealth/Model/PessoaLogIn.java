package com.example.cardiohealth.Model;

public class PessoaLogIn {
    private String password;
    private String email;

    public PessoaLogIn(String email,String password) {
        this.password = password;
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

