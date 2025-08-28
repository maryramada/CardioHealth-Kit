package com.example.cardiohealth.Model;

public class SatOx {
    private final int valor;
    private final Instant instant;

    public SatOx(int valor, Instant instant) {
        this.valor = valor;
        this.instant = instant;
    }

    public int getValor() {
        return valor;
    }

    public Instant getInstant() {
        return instant;
    }

    @Override
    public String toString() {
        return valor + "% , " + instant;
    }

}
