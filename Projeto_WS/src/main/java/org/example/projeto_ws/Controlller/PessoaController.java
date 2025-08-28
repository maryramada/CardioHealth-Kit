package org.example.projeto_ws.Controlller;

import org.example.projeto_ws.DTO.*;
import org.example.projeto_ws.Service.PessoaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api"})

public class PessoaController {
    public PessoaController() {
    }

    @PostMapping(
            value = {"/pessoas"},
            consumes = {"application/xml"},
            produces = {"application/xml"}

    )
    public ResponseEntity<Object> adicionarPessoa(@RequestBody PessoaDTO arg) {
        try {
            PessoaService.adicionarPessoa(arg);
            return new ResponseEntity(HttpStatus.CREATED);
        } catch (Exception var3) {
            Exception e = var3;
            return new ResponseEntity(new ErrorDTO(e.getMessage()), HttpStatus.CONFLICT);
        }
    }

    @PostMapping(
            value = {"/login"},
            consumes = {"application/xml"},
            produces = {"application/xml"}

    )
    public ResponseEntity<Object> logInPessoa(@RequestBody PessoaLogInDTO arg) {
        try {
            int id = PessoaService.loginPessoa(arg);
            return new ResponseEntity(id, HttpStatus.ACCEPTED);
        } catch (Exception var3) {
            Exception e = var3;
            return new ResponseEntity(new ErrorDTO(e.getMessage()), HttpStatus.CONFLICT);
        }
    }

    @GetMapping(
            value = {"/pessoas"},
            produces = {"application/xml"}
    )
    public ResponseEntity<Object> getPessoas() {
        try {
            PessoaContainerDTO result = PessoaService.getPessoas();
            return new ResponseEntity(result, HttpStatus.OK);
        } catch (Exception var2) {
            Exception e = var2;
            return new ResponseEntity(new ErrorDTO(e.getMessage()), HttpStatus.CONFLICT);
        }
    }

    @GetMapping(
            value = {"/pessoas/{id}"},
            produces = {"application/xml"}
    )
    public ResponseEntity<Object> getPessoa(@PathVariable("id") int id) {
        try {
            PessoaDTO result = PessoaService.getPessoa(id);
            return new ResponseEntity(result, HttpStatus.OK);
        } catch (Exception var3) {
            Exception e = var3;
            return new ResponseEntity(new ErrorDTO(e.getMessage()), HttpStatus.CONFLICT);
        }
    }

    @PutMapping(
            value = {"/pessoas/{id}"},
            consumes = {"application/xml"},
            produces = {"application/xml"}

    )
    public ResponseEntity<Object> editarPessoa(@PathVariable int id, @RequestBody PessoaDTO pessoaDTO) {
        try {
            PessoaDTO pessoaEditada = PessoaService.editarPessoa(id, pessoaDTO);
            return new ResponseEntity<>(pessoaEditada, HttpStatus.OK);
        } catch (Exception e) {
            // Caso a pessoa não seja encontrada ou ocorra um erro
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(
            value = {"/pessoas/{id}"},
            produces = {"application/xml"}
    )
    public ResponseEntity<Object> eliminarPessoa(@PathVariable int id) {
        try {
            boolean eliminado = PessoaService.eliminarPessoa(id);
            if (eliminado) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(new ErrorDTO("Pessoa com ID " + id + " não encontrada."), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(
            value = "/pessoas/{id}/frequenciaCardiaca",
            consumes = {"application/xml"},
            produces = {"application/xml"}
    )
    public ResponseEntity<Object> adicionarFrequenciaCardiaca(
            @PathVariable("id") int id,
            @RequestBody FreqCardiacaDTO freqCardiacaDTO) {
        try {
            PessoaService.adicionarFrequenciaCardiaca(id, freqCardiacaDTO);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorDTO("Erro interno: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(
            value = "/pessoas/{id}/saturacaoOxigenio",
            consumes = {"application/xml"},
            produces = {"application/xml"}
    )
    public ResponseEntity<Object> adicionarSaturacaoOxigenio(
            @PathVariable("id") int id,
            @RequestBody SatOxDTO satOxDTO) {
        try {
            PessoaService.adicionarSaturacaoOxigenio(id, satOxDTO);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorDTO("Erro interno: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(
            value = "/pessoas/{id}/frequenciaCardiaca",
            produces = {"application/xml"}
    )
    public ResponseEntity<Object> getFrequenciasCardiacas(@PathVariable("id") int id) {
        try {
            FreqCardiacaContainerDTO result = PessoaService.getFrequenciasCardiacas(id);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.CONFLICT);
        }
    }

    @GetMapping(
            value = "/pessoas/{id}/saturacaoOxigenio",
            produces = {"application/xml"}
    )
    public ResponseEntity<Object> getSatOx(@PathVariable("id") int id) {
        try {
           SatOxContainerDTO result = PessoaService.getSaturacoesOx(id);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.CONFLICT);
        }
    }
    @GetMapping(
            value = "/pessoas/{id}/alertas",
            produces = {"application/xml"}
    )
    public ResponseEntity<Object> getAlertas(@PathVariable("id") int id) {
        try {
            AlertaContainerDTO result = PessoaService.getAlertas(id);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping(
            value = {"/alertas/{pessoaID}"},
            produces = {"application/xml"}
    )
    public ResponseEntity<Object> eliminarAlertas(@PathVariable int pessoaID) {
        try {
            boolean eliminado = PessoaService.eliminarAlertas(pessoaID);
            if (eliminado) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(new ErrorDTO("Alertas para pessoa com ID " + pessoaID + " não encontrados."), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

