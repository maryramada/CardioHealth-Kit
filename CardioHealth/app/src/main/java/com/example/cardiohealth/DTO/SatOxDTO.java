package com.example.cardiohealth.DTO;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;

@Order(elements = {"valor", "instant"})
@Root(name =  "satox")
public class SatOxDTO {
    @Element(name = "valor")
    private int valor;
    @Element(name = "instant")
    private InstantDTO instant;

    public SatOxDTO() {
    }

    public SatOxDTO(int valor, InstantDTO instant) {
        this.valor = valor;
        this.instant = instant;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public InstantDTO getInstant() {
        return instant;
    }

    public void setInstant(InstantDTO instant) {
        this.instant = instant;
    }
}
