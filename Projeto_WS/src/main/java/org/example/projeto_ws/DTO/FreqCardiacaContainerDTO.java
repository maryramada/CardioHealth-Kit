package org.example.projeto_ws.DTO;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.List;

@JacksonXmlRootElement(localName = "freqCardiacas")
public class FreqCardiacaContainerDTO {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "freqCardiaca")
    private List<FreqCardiacaDTO> freqCardiaca;

    public FreqCardiacaContainerDTO() {
    }

    public FreqCardiacaContainerDTO(List<FreqCardiacaDTO> alertas) {
        this.freqCardiaca = freqCardiaca;
    }

    public List<FreqCardiacaDTO> getFreqCardiaca() {
        return freqCardiaca;
    }

    public void setFreqCardiaca(List<FreqCardiacaDTO> freqCardiaca) {
        this.freqCardiaca = freqCardiaca;
    }
}
