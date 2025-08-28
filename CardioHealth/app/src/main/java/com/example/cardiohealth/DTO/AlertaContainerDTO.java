package com.example.cardiohealth.DTO;

import com.example.cardiohealth.Model.Alertas;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

@Root(name = "Alertas")
public class AlertaContainerDTO {

    @ElementList(inline = true, required = false)
    private List<AlertasDTO> alertas;

    // Construtor vazio necessário para a deserialização
    public AlertaContainerDTO() {
    }

    // Construtor com parâmetros
    public AlertaContainerDTO(List<AlertasDTO> alertas) {
        this.alertas = alertas;
    }

    // Getter para a lista de alertas
    public List<AlertasDTO> getAlertas() {
        return alertas;
    }

    // Setter para a lista de alertas
    public void setAlertas(List<AlertasDTO> alertas) {
        this.alertas = alertas;
    }
}
