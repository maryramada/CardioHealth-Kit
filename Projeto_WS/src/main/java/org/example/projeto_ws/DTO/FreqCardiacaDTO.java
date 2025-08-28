package org.example.projeto_ws.DTO;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.example.projeto_ws.Model.FreqCardiaca;

@JacksonXmlRootElement(
        localName = "freqCardiacas"
)

@JsonPropertyOrder({"num", "instant"})
public class FreqCardiacaDTO {
    @JacksonXmlProperty(
            localName = "num"
    )
    private int num;
    @JacksonXmlProperty(
            localName = "instant"
    )
    private InstantDTO instant;

    public FreqCardiacaDTO () {
    }

    public FreqCardiacaDTO(int num, InstantDTO instant) {
        this.num = num;
        this.instant = instant;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public InstantDTO getInstant() {
        return instant;
    }

    public void setInstant(InstantDTO instant) {
        this.instant = instant;
    }
}
