package org.example.projeto_ws.DTO;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.example.projeto_ws.Model.FreqCardiaca;

@JacksonXmlRootElement(
        localName = "pessoa"
)

@JsonPropertyOrder({"id", "nome", "password", "nascimento", "email", "telemovel","contactoEmergencia", "freqCardiacas", "satOx", "alertas"})
public class PessoaContainerItemDTO {
    @JacksonXmlProperty(
            localName = "id"
    )

    private int id;
    @JacksonXmlProperty(
            localName = "nome"
    )
    private String nome;
    @JacksonXmlProperty(
            localName = "password"
    )
    private String password;
    @JacksonXmlProperty(
            localName = "nascimento"
    )
    private DataDTO nascimento;
    @JacksonXmlProperty(
            localName = "email"
    )
    private String email;
    @JacksonXmlProperty(
            localName = "telemovel"
    )
    private int telemovel;
    @JacksonXmlProperty(
            localName = "contactoEmergencia"
    )
    private int contactoEmergencia;
    @JacksonXmlProperty(
            localName = "freqCardiacas"
    )
    private FreqCardiacaDTO freqCardiacas;
    @JacksonXmlProperty(
            localName = "satOx"
    )
    private SatOxDTO satOx;
    @JacksonXmlProperty(
            localName = "alertas"
    )
    private AlertasDTO alertas;

    public PessoaContainerItemDTO() {
    }

    public PessoaContainerItemDTO(int id, String nome, String password, DataDTO nascimento, String email, int telemovel, int contactoEmergencia,FreqCardiacaDTO freqCardiacas, SatOxDTO satOx, AlertasDTO alertas) {
        this.setId(id);
        this.setNome(nome);
        this.setPassword(password);
        this.setNascimento(nascimento);
        this.setEmail(email);
        this.setTelemovel(telemovel);
        this.setContactoEmergencia(contactoEmergencia);
        this.setFreqCardiacas(freqCardiacas);
        this.setSatOx(satOx);
        this.setAlertas(alertas);
    }

    public PessoaContainerItemDTO(int id, String nome, String password, DataDTO dataDTO, String email, int telemovel, int contactoEmergencia) {
        this.setId(id);
        this.setNome(nome);
        this.setPassword(password);
        this.setNascimento(nascimento);
        this.setEmail(email);
        this.setTelemovel(telemovel);
        this.setContactoEmergencia(contactoEmergencia);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public DataDTO getNascimento() {
        return nascimento;
    }

    public void setNascimento(DataDTO nascimento) {
        this.nascimento = nascimento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getTelemovel() {
        return telemovel;
    }

    public void setTelemovel(int telemovel) {
        this.telemovel = telemovel;
    }

    public int getContactoEmergencia() {
        return contactoEmergencia;
    }
    public void setContactoEmergencia(int contactoEmergencia) {
        this.contactoEmergencia = contactoEmergencia;
    }

    public FreqCardiacaDTO getFreqCardiacas() {
        return freqCardiacas;
    }

    public void setFreqCardiacas(FreqCardiacaDTO freqCardiacas) {
        this.freqCardiacas = freqCardiacas;
    }

    public SatOxDTO getSatOx() {
        return satOx;
    }

    public void setSatOx(SatOxDTO satOx) {
        this.satOx = satOx;
    }

    public AlertasDTO getAlertas() {
        return alertas;
    }

    public void setAlertas(AlertasDTO alertas) {
        this.alertas = alertas;
    }
}





