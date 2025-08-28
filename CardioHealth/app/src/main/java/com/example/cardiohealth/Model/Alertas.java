package com.example.cardiohealth.Model;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Locale;

public class Alertas {
    private final String tipo;
    private final String descricao;
    private final int valor;
    private final Instant dataHora;

    public Alertas(String tipo, String descricao, int valor, Instant dataHora) {
        this.tipo = tipo;
        this.descricao = descricao;
        this.valor = valor;
        this.dataHora = dataHora;
    }

    public String getTipo() {
        return tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getValor() {
        return valor;
    }

    public Instant getDataHora() {
        return dataHora;
    }
}