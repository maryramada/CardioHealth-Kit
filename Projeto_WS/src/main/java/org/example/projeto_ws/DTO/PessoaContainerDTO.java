package org.example.projeto_ws.DTO;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.ArrayList;

@JacksonXmlRootElement(
        localName = "pessoas"
)
public class PessoaContainerDTO {
    @JacksonXmlElementWrapper(
            useWrapping = false
    )
    @JacksonXmlProperty(
            localName = "pessoa"
    )
    private ArrayList<PessoaContainerItemDTO> pessoas;

    public PessoaContainerDTO() {
    }

    public PessoaContainerDTO(ArrayList<PessoaContainerItemDTO> pessoas) {
        this.pessoas = pessoas;
    }

    public ArrayList<PessoaContainerItemDTO> getPessoas() {
        return this.pessoas;
    }

    public void setPessoas(ArrayList<PessoaContainerItemDTO> pessoas) {
        this.pessoas = pessoas;
    }
}
