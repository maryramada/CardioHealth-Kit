package com.example.cardiohealth.Model;

import java.io.Serializable;

public class Data implements Comparable<Data> {
    private final int dia;
    private final int mes;
    private final int ano;

    public Data(int dia, int mes, int ano) {
        if (dia < 1 || dia > 31 || mes < 1 || mes > 12 || ano < 0) {
            throw new IllegalArgumentException("Data inválida.");
        }
        this.dia = dia;
        this.mes = mes;
        this.ano = ano;
    }

    public int getDia() {
        return dia;
    }

    public int getMes() {
        return mes;
    }

    public int getAno() {
        return ano;
    }

    @Override
    public String toString() {
        return String.format("%02d/%02d/%04d", dia, mes, ano);
    }

    public static Data stringToData(String data) {
        String[] pedacos = data.split("/");
        if (pedacos.length != 3) {
            throw new IllegalArgumentException("Formato de data inválido. Use DD/MM/AAAA.");
        }

        int dia = Integer.parseInt(pedacos[0]);
        int mes = Integer.parseInt(pedacos[1]);
        int ano = Integer.parseInt(pedacos[2]);

        return new Data(dia, mes, ano);
    }

    @Override
    public int compareTo(Data other) {
        // Primeiro, compara o ano
        int anoComparison = Integer.compare(this.ano, other.ano);
        if (anoComparison != 0) {
            return anoComparison;
        }

        // Se os anos forem iguais, compara o mês
        int mesComparison = Integer.compare(this.mes, other.mes);
        if (mesComparison != 0) {
            return mesComparison;
        }

        // Se o mês também for igual, compara o dia
        return Integer.compare(this.dia, other.dia);
    }
}
