package com.example.cardiohealth.DTO;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;

import java.util.List;

@Order(elements = {"nome", "password", "nascimento", "email","telemovel","contactoEmergencia"})
@Root(name =  "pessoa")
public class PessoaDTO {
//    @Element(name = "id")
//    private int id;
    @Element (name = "nome")
    private String nome;
    @Element (name = "password")
    private String password;
    @Element(name = "nascimento")
    private DataDTO nascimento;
    @Element(name = "email")
    private String email;
    @Element(name = "telemovel")
    private int telemovel;
    @Element(name = "contactoEmergencia")
    private int contactoEmergencia;
//    @Element(name = "freqCardiacas")
//    private List<FreqCardiacaDTO> freqCardiacas;
//    @Element (name = "satOx")
//    private List <SatOxDTO> satOx;
//    @Element (name = "alertas")
//    private List <AlertasDTO> alertas;

    public PessoaDTO() {
    }

    public PessoaDTO(String nome, String password, DataDTO nascimento, String email, int telemovel, int contactoEmergencia) {
        this.setNome(nome);
        this.setPassword(password);
        this.setNascimento(nascimento);
        this.setEmail(email);
        this.setTelemovel(telemovel);
        this.setContactoEmergencia(contactoEmergencia);
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

    public int getTelemovel() {
        return telemovel;
    }

    public void setTelemovel(int telemovel) {
        this.telemovel = telemovel;
    }

    public int getContactoEmergencia() {
        return contactoEmergencia;
    }

    public void setContactoEmergencia(int contactoEmergencia) {
        this.contactoEmergencia = contactoEmergencia;
    }
}






