package org.example.projeto_ws.DTO;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;
import java.util.List;

@JacksonXmlRootElement(localName = "Alertas")
public class AlertaContainerDTO {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "alerta")
    private List<AlertasDTO> alertas;

    public AlertaContainerDTO() {
        // Construtor vazio necessário para a deserialização
    }

    public AlertaContainerDTO(List<AlertasDTO> alertas) {
        this.alertas = alertas;
    }

    public List<AlertasDTO> getAlertas() {
        return alertas;
    }

    public void setAlertas(List<AlertasDTO> alertas) {
        this.alertas = alertas;
    }
}
