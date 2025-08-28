package com.example.cardiohealth.DTO;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;

@Order(elements={"dia", "mes", "ano"})
@Root(name = "data")
public class DataDTO {
    @Element(name = "dia")
    private int dia;
    @Element(name = "mes")
    private int mes; // 1- january, ... 12- December
    @Element(name = "ano")
    private int ano;

    public DataDTO() {
    }

    public DataDTO(int dia, int mes, int ano) {
        this.dia = dia;
        this.mes = mes;
        this.ano = ano;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }
}