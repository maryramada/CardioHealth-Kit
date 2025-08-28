package org.example.projeto_ws.Model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Alertas implements Serializable {
    private final String tipo;
    private final String descricao;
    private final int valor;
    private final Instant instant;

    public Alertas(String tipo, String descricao, int valor, Instant instant) {
        LocalDateTime agora = LocalDateTime.now();
        this.tipo = tipo;
        this.descricao = descricao;
        this.valor = valor;
        this.instant = instant;
    }
    public Alertas(String tipo, String descricao, int valor) {
        LocalDateTime agora = LocalDateTime.now();
        this.tipo = tipo;
        this.descricao = descricao;
        this.valor = valor;
        this.instant = new Instant(agora.getHour(), agora.getMinute(), agora.getSecond(), new Data(agora.getDayOfMonth(), agora.getMonthValue(), agora.getYear())); ;
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

    public Instant getInstant() {
        return instant;
    }
}


