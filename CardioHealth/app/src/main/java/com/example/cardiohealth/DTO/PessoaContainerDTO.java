package com.example.cardiohealth.DTO;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;

import java.util.List;

@Order(elements = {"pessoas"})
@Root(name = "pessoas")
public class PessoaContainerDTO {
    @ElementList(name = "pessoa", inline = true)
    private List<PessoaContainerItemDTO> pessoas;

    public PessoaContainerDTO() {
    }

    public PessoaContainerDTO(List<PessoaContainerItemDTO> pessoas) {
        this.pessoas = pessoas;
    }

    public List<PessoaContainerItemDTO> getPessoas() {
        return pessoas;
    }

    public void setPessoas(List<PessoaContainerItemDTO> pessoas) {
        this.pessoas = pessoas;
    }
}
