package org.example.projeto_ws.DTO;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.example.projeto_ws.Model.Instant;

@JacksonXmlRootElement(
        localName = "alertas"
)

@JsonPropertyOrder({"tipo", "descricao", "valor", "instant"})
public class AlertasDTO {
    @JacksonXmlProperty(
            localName = "tipo"
    )
    private String tipo;
    @JacksonXmlProperty(
            localName = "descricao"
    )
    private String descricao;
    @JacksonXmlProperty(
            localName = "valor"
    )
    private int valor;
    @JacksonXmlProperty(
            localName = "instant"
    )
    private InstantDTO dataHora;

    public AlertasDTO() {
    }

    public AlertasDTO(String tipo, String descricao, int valor, InstantDTO dataHora) {
        this.tipo = tipo;
        this.descricao = descricao;
        this.valor = valor;
        this.dataHora = dataHora;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public InstantDTO getDataHora() {
        return dataHora;
    }

    public void setDataHora(InstantDTO dataHora) {
        this.dataHora = dataHora;
    }
}