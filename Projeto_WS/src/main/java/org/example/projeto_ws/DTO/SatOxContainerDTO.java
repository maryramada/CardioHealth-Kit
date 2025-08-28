package org.example.projeto_ws.DTO;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.List;

@JacksonXmlRootElement(localName = "SatOxs")
public class SatOxContainerDTO {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "satox")
    private List<SatOxDTO> satOx;

    public SatOxContainerDTO() {
    }

    public SatOxContainerDTO(List<SatOxDTO> satOx) {
        this.satOx = satOx;
    }

    public List<SatOxDTO> getSatox() {
        return satOx;
    }

    public void setSatOx(List<SatOxDTO> satOx) {
        this.satOx = satOx;
    }
}
