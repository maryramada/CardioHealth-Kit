package com.example.cardiohealth.DTO;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;

import java.util.List;

@Order(elements = {"id", "nome", "password", "nascimento", "email","telemovel","freqCardiacas","SatOx","alertas"})
@Root(name =  "pessoa")
public class PessoaContainerItemDTO {
    @Element(name = "id")
    private int id;
    @Element(name = "nome")
    private String nome;
    @Element(name = "password")
    private String password;
    @Element(name = "nascimento")
    private DataDTO nascimento;
    @Element(name = "email")
    private String email;
    @Element(name = "telemovel")
    private int telemovel;
    @Element(name = "freqCardiacas")
    private List<FreqCardiacaDTO> freqCardiacas;
    @Element(name = "satOx")
    private List<SatOxDTO> satOx;
    @Element(name = "alertas")
    private List<AlertasDTO> alertas;

    public PessoaContainerItemDTO() {
    }

    public PessoaContainerItemDTO(int id, String nome, String password, DataDTO nascimento, String email, int telemovel, List<FreqCardiacaDTO> freqCardiacas, List<SatOxDTO> satOx, List<AlertasDTO> alertas) {
        this.id = id;
        this.nome = nome;
        this.password = password;
        this.nascimento = nascimento;
        this.email = email;
        this.telemovel = telemovel;
        this.freqCardiacas = freqCardiacas;
        this.satOx = satOx;
        this.alertas = alertas;
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

    public int getTelemovel() {
        return telemovel;
    }

    public void setTelemovel(int telemovel) {
        this.telemovel = telemovel;
    }

    public List<FreqCardiacaDTO> getFreqCardiacas() {
        return freqCardiacas;
    }

    public void setFreqCardiacas(List<FreqCardiacaDTO> freqCardiacas) {
        this.freqCardiacas = freqCardiacas;
    }

    public List<SatOxDTO> getSatOx() {
        return satOx;
    }

    public void setSatOx(List<SatOxDTO> satOx) {
        this.satOx = satOx;
    }

    public List<AlertasDTO> getAlertas() {
        return alertas;
    }

    public void setAlertas(List<AlertasDTO> alertas) {
        this.alertas = alertas;
    }
}