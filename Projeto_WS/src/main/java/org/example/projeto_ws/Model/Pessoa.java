package org.example.projeto_ws.Model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Pessoa implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L; // Para compatibilidade da serialização

    private int id;
    private String nome;
    private String password;
    private Data nascimento;
    private String email;
    private int telemovel;
    private int contactoEmergencia;
    private List<FreqCardiaca> freqCardiacas; // Lista para armazenar objetos FreqCardiaca
    private List<SatOx> satOx; // Lista para armazenar objetos SatOx
    private List<Alertas> alertas; // Lista para armazenar objetos Alertas

    // Construtor da classe Pessoa


    public Pessoa(int id, String nome, String password, Data nascimento, String email, int telemovel, int contactoEmergencia, List<FreqCardiaca> freqCardiacas, List<SatOx> satOx, List<Alertas> alertas) {
        this.id = id;
        this.nome = nome;
        this.password = password;
        this.nascimento = nascimento;
        this.email = email;
        this.telemovel = telemovel;
        this.contactoEmergencia = contactoEmergencia;
        this.freqCardiacas = freqCardiacas;
        this.satOx = satOx;
        this.alertas = alertas;
    }
    public Pessoa(){

    }

    public Pessoa(int id, String nome, String password, Data nascimento, String email, int telemovel, int contactoEmergencia) {
        this.id = id;
        this.nome = nome;
        this.password = password;
        this.nascimento = nascimento;
        this.email = email;
        this.telemovel = telemovel;
        this.contactoEmergencia = contactoEmergencia;
    }

    public Pessoa(String nome, String password, Data nascimento, String email, int telemovel, int contactoEmergencia) {
        this.nome = nome;
        this.password = password;
        this.nascimento = nascimento;
        this.email = email;
        this.telemovel = telemovel;
        this.contactoEmergencia = contactoEmergencia;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {this.id = id;}

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

    // Métodos para adicionar registros
    public void adicionarFrequenciaCardiaca(FreqCardiaca freq) {
        freqCardiacas.add(freq);
    }

    public void adicionarSaturacaoOxigenio(SatOx sat) {
        satOx.add(sat);
    }

    public void adicionarAlerta(Alertas alerta) {
        alertas.add(alerta);
    }

    // Método para gerar alertas de frequência cardíaca e saturação de oxigênio
    public void gerarAlertas() {
        // Inicializar a lista de alertas, se necessário
        List<Alertas> alertasExistentes = getAlertas();
        if (alertasExistentes == null) {
            alertasExistentes = new ArrayList<>();
            setAlertas(alertasExistentes); // Atualiza a lista de alertas na classe
        }

        // Garantir que a lista de frequência cardíaca não é nula
        List<FreqCardiaca> freqCardiacas = getFreqCardiacas();
        if (freqCardiacas == null) {
            freqCardiacas = new ArrayList<>();
        }

        // Verificar os alertas de frequência cardíaca
        for (FreqCardiaca freq : freqCardiacas) {
            if (freq.getNum() < 60 || freq.getNum() > 100) {
                String descricao = freq.getNum() < 60 ? "Frequência cardíaca baixa" : "Frequência cardíaca alta";
                boolean alertaExistente = alertasExistentes.stream()
                        .anyMatch(alerta -> alerta.getDescricao().equals(descricao) && alerta.getValor() == freq.getNum());

                if (!alertaExistente) {
                    alertasExistentes.add(new Alertas("Frequência Cardíaca", descricao, freq.getNum()));
                }
            }
        }

        // Garantir que a lista de saturação de oxigênio não é nula
        List<SatOx> satOxList = getSatOx();
        if (satOxList == null) {
            satOxList = new ArrayList<>();
        }

        // Verificar os alertas de saturação de oxigênio
        for (SatOx sat : satOxList) {
            if (sat.getValor() < 95) { // Exemplo de limite para saturação
                String descricao = "Saturação de oxigênio baixa";
                boolean alertaExistente = alertasExistentes.stream()
                        .anyMatch(alerta -> alerta.getDescricao().equals(descricao) && alerta.getValor() == sat.getValor());

                if (!alertaExistente) {
                    alertasExistentes.add(new Alertas("Saturação Oxigênio", descricao, sat.getValor()));
                }
            }
        }
    }
}
