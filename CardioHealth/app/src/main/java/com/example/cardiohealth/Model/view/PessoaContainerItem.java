package com.example.cardiohealth.Model.view;

import com.example.cardiohealth.DTO.DataDTO;

public class PessoaContainerItem {
    private int id;
    private String nome;
    private String password;
    private DataDTO nascimento;
    private String email;
    private String telemovel;

    public PessoaContainerItem(int id, String nome, String password, DataDTO nascimento, String email, String telemovel) {
        this.id = id;
        this.nome = nome;
        this.password = password;
        this.nascimento = nascimento;
        this.email = email;
        this.telemovel = telemovel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public DataDTO getNascimento() {
        return nascimento;
    }

    public void setNascimento(DataDTO nascimento) {
        this.nascimento = nascimento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelemovel() {
        return telemovel;
    }

    public void setTelemovel(String telemovel) {
        this.telemovel = telemovel;
    }
}

