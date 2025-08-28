package com.example.cardiohealth.DTO;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;
@Order(elements={"hora", "minuto", "segundo", "data"})
@Root(name = "instant")
public class InstantDTO {
    @Element(name = "hora")
    private int hora;
    @Element(name = "minuto")
    private int minuto; // 1- january, ... 12- December
    @Element(name = "segundo")
    private int segundo;
    @Element(name = "data")
    private DataDTO data;

    public InstantDTO() {
    }

    public InstantDTO(int hora, int minuto, int segundo,  DataDTO data) {
        this.hora = hora;
        this.minuto = minuto;
        this.segundo = segundo;
        this.data = data;
    }

    public int getHora() {
        return hora;
    }

    public void setHora(int hora) {
        this.hora = hora;
    }

    public int getMinuto() {
        return minuto;
    }

    public void setMinuto(int minuto) {
        this.minuto = minuto;
    }

    public int getSegundo() {
        return segundo;
    }

    public void setSegundo(int segundo) {
        this.segundo = segundo;
    }

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }
}