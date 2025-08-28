package com.example.cardiohealth.Model;

import java.util.List;

public class Pessoa {
    private int id;
    private String nome;
    private String password;
    private Data nascimento;
    private String email;
    private int telemovel;
    private int contactoEmergencia;
    private List<FreqCardiaca> freqCardiacas;
    private List<SatOx> satOx;
    private List<Alertas> alertas;

    public Pessoa (String nome, String password, Data nascimento, String email, int telemovel, int contactoEmergencia)
    {
        setNome(nome);
        setPassword(password);
        setNascimento(nascimento);
        setEmail(email);
        setTelemovel(telemovel);
        setContactoEmergencia(contactoEmergencia);

    }
    public Pessoa (Pessoa pessoa)
    {
        setId (id);
        setNome(nome);
        setPassword(password);
        setNascimento(nascimento);
        setEmail(email);
        setTelemovel(telemovel);
        setContactoEmergencia(contactoEmergencia);
        setFreqCardiacas(freqCardiacas);
        setSatOx(satOx);
        setAlertas(alertas);
    }
    public int getId()
    {
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

    public Data getNascimento() {
        return nascimento;
    }

    public void setNascimento(Data nascimento) {
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

    public List<FreqCardiaca> getFreqCardiacas() {
        return freqCardiacas;
    }

    public void setFreqCardiacas(List<FreqCardiaca> freqCardiacas) {
        this.freqCardiacas = freqCardiacas;
    }

    public List<SatOx> getSatOx() {
        return satOx;
    }

    public void setSatOx(List<SatOx> satOx) {
        this.satOx = satOx;
    }

    public List<Alertas> getAlertas() {
        return alertas;
    }

    public void setAlertas(List<Alertas> alertas) {
        this.alertas = alertas;
    }
}
