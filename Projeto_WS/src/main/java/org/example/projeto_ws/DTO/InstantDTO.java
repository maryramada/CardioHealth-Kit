package org.example.projeto_ws.DTO;



import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.example.projeto_ws.Model.Data;

@JsonPropertyOrder({"hora", "minuto", "segundo", "data"})
@JacksonXmlRootElement(
        localName = "instant"
)
public class InstantDTO {
    @JacksonXmlProperty(
            localName = "hora"
    )
    private int hora;
    @JacksonXmlProperty(
            localName = "minuto"
    )
    private int minuto;
    @JacksonXmlProperty(
            localName = "segundo"
    )
    private int segundo;
    @JacksonXmlProperty(
            localName = "data"
    )
    private DataDTO data;

    public InstantDTO() {
    }

    public InstantDTO(int hora, int minuto, int segundo, DataDTO data) {
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
