package org.example.projeto_ws.Model;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class PessoaConcreta extends Pessoa implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; // Compatibilidade de serialização

    public PessoaConcreta(int id, String nome, String password, Data nascimento, String email, int telemovel, int contactoEmergencia,
                          List<FreqCardiaca> freqCardiacas, List<SatOx> satOx, List<Alertas> alertas) {
        super(id, nome, password, nascimento, email, telemovel, contactoEmergencia, freqCardiacas, satOx, alertas);
    }

    @Override
    public String toString() {
        return "ID: " + getId() +
                ", Nome: " + getNome() +
                ", Email: " + getEmail() +
                ", Telemóvel: " + getTelemovel() +
                ", ContactoEmergencia: " + getContactoEmergencia() +
                ", Data de Nascimento: " + getNascimento();
    }
}
