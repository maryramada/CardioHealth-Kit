package org.example.projeto_ws.DTO;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.example.projeto_ws.Model.FreqCardiaca;

@JacksonXmlRootElement(
        localName = "satox"
)

@JsonPropertyOrder({"valor", "instant"})
public class SatOxDTO {
    @JacksonXmlProperty(
            localName = "valor"
    )
    private int valor;
    @JacksonXmlProperty(
            localName = "instant"
    )
    private InstantDTO instant;

    public SatOxDTO() {
    }

    public SatOxDTO(int num, InstantDTO instant) {
        this.valor = num;
        this.instant = instant;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int num) {
        this.valor = num;
    }

    public InstantDTO getInstant() {
        return instant;
    }

    public void setInstant(InstantDTO instant) {
        this.instant = instant;
    }
}
