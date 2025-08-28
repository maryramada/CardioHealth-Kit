package org.example.projeto_ws.Model;

import java.io.Serializable;

import static org.example.projeto_ws.Model.Data.stringToData;

public class Instant implements Serializable {
    private static final long serialVersionUID = 6556237225583349795L;
    private final int hora;
    private final int minuto;
    private final int segundo;
    private final Data data;

    public Instant(int hora, int minuto, int segundo, Data data) {
        if (hora < 0 || hora > 23 || minuto < 0 || minuto > 59 || segundo < 0 || segundo > 59) {
            throw new IllegalArgumentException("Hora inválida.");
        }
        this.hora = hora;
        this.minuto = minuto;
        this.segundo = segundo;
        this.data = data;
    }

    public int getHora() {
        return hora;
    }

    public int getMinuto() {
        return minuto;
    }

    public int getSegundo() {
        return segundo;
    }

    public Data getData() {
        return data;
    }

    @Override
    public String toString() {

        return String.format("%02d:%02d:%02d em %s", hora, minuto, segundo, data);
    }
    public static Instant stringToInstant(String instantStr) {
        String[] partes = instantStr.split(" em ");
        if (partes.length != 2) {
            throw new IllegalArgumentException("Formato inválido. O formato esperado é 'HH:MM:SS em DD/MM/AAAA'.");
        }

        // Processa a parte do tempo
        String tempoStr = partes[0];  // Ex: "12:30:45"
        String[] tempoPartes = tempoStr.split(":");
        if (tempoPartes.length != 3) {
            throw new IllegalArgumentException("Formato de tempo inválido. Use HH:MM:SS.");
        }
        int hora = Integer.parseInt(tempoPartes[0]);
        int minuto = Integer.parseInt(tempoPartes[1]);
        int segundo = Integer.parseInt(tempoPartes[2]);

        // Processa a parte da data
        Data data = stringToData(partes[1]);  // Ex: "01/01/2020"

        return new Instant(hora, minuto, segundo, data);
    }

}
