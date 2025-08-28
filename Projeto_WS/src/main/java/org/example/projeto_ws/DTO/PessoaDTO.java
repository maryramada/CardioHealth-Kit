package org.example.projeto_ws.DTO;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.example.projeto_ws.Model.FreqCardiaca;

import java.util.List;

@JacksonXmlRootElement(
        localName = "pessoa"
)

@JsonPropertyOrder({"nome", "password", "nascimento", "email", "telemovel", "contactoEmergencia"})
public class PessoaDTO {
//    @JacksonXmlProperty(
//            localName = "id"
//    )
//
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

//    @JacksonXmlProperty(
//            localName = "freqCardiacas"
//    )
//    private List <FreqCardiacaDTO> freqCardiacas;
//    @JacksonXmlProperty(
//            localName = "satOx"
//    )
//    private List <SatOxDTO> satOx;
//    @JacksonXmlProperty(
//            localName = "alertas"
//    )
//    private List<AlertasDTO> alertas;

    public PessoaDTO() {
    }

    public PessoaDTO(String nome, String password, DataDTO nascimento, String email, int telemovel, int contactoEmergencia, List<FreqCardiacaDTO> freqCardiacas, List<SatOxDTO> satOx, List<AlertasDTO> alertas) {
        this.setNome(nome);
        this.setPassword(password);
        this.setNascimento(nascimento);
        this.setEmail(email);
        this.setTelemovel(telemovel);
        this.setContactoEmergencia(contactoEmergencia);

    }

    public PessoaDTO(String nome, String password, DataDTO nascimento, String email, int telemovel, int contactoEmergencia) {
        this.setNome(nome);
        this.setPassword(password);
        this.setNascimento(nascimento);
        this.setEmail(email);
        this.setTelemovel(telemovel);
        this.setContactoEmergencia(contactoEmergencia);
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

}





