package org.example.projeto_ws.Controlller;

import org.example.projeto_ws.DTO.*;
import org.example.projeto_ws.Model.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Mapper {
    public static PessoaContainerDTO pessoasContainer2pessoasDTO(List<Pessoa> arg) {
        ArrayList<PessoaContainerItemDTO> list = new ArrayList();
        Iterator var2 = arg.iterator();

        while (var2.hasNext()) {
            Pessoa pessoa = (Pessoa) var2.next();
            PessoaContainerItemDTO item = pessoas2pessoasContainerItemsDTO(pessoa);
            list.add(item);
        }

        PessoaContainerDTO obj = new PessoaContainerDTO(list);
        return obj;
    }

    private static PessoaContainerItemDTO pessoas2pessoasContainerItemsDTO(Pessoa pessoa) {
        DataDTO dataDTO = data2DataDTO(pessoa.getNascimento());
//        FreqCardiacaDTO freqCardiacaDTO = (FreqCardiacaDTO) freq2FreqCardiacasDTO(pessoa.getFreqCardiacas());
//        SatOxDTO satOxDTO = (SatOxDTO) sat2SatOxDTO(pessoa.getSatOx());
//        AlertasDTO alertasDTO = alerta2AlertaDTO(pessoa.getAlertas());
        PessoaContainerItemDTO item = new PessoaContainerItemDTO(pessoa.getId(), pessoa.getNome(), pessoa.getPassword(), dataDTO, pessoa.getEmail(), pessoa.getTelemovel(), pessoa.getContactoEmergencia());
        return item;
    }

    public static List<FreqCardiacaDTO> freq2FreqCardiacasDTO(List<FreqCardiaca> freqCardiacas) {
        List<FreqCardiacaDTO> list = new ArrayList<>();

        for (FreqCardiaca freqCardiaca : freqCardiacas) {
            FreqCardiacaDTO dto = freqCardiaca2FreqCardiacaDTO(freqCardiaca);
            list.add(dto);
        }
        return list;
    }

    public static List<SatOxDTO> sat2SatOxDTO(List<SatOx> satOxs) {
        List<SatOxDTO> list = new ArrayList<>();

        for (SatOx satOx : satOxs) {
            SatOxDTO dto = satOx2SatOxDTO(satOx);
            list.add(dto);
        }
        return list;
    }


    private static FreqCardiacaDTO freqCardiaca2FreqCardiacaDTO(FreqCardiaca freqCardiaca) {
        if (freqCardiaca == null) {
            throw new IllegalArgumentException("FreqCardiaca não pode ser nula.");
        }

        // Mapeia a Data para DataDTO
        Data data = freqCardiaca.getInstant().getData();
        DataDTO dataDTO = new DataDTO(
                data.getDia(),
                data.getMes(),
                data.getAno()
        );

        // Mapeia o Instant para InstantDTO
        Instant instant = freqCardiaca.getInstant();
        InstantDTO instantDTO = new InstantDTO(
                instant.getHora(),
                instant.getMinuto(),
                instant.getSegundo(),
                dataDTO // Inclui o DataDTO mapeado
        );

        // Retorna o DTO completo
        return new FreqCardiacaDTO(
                freqCardiaca.getNum(),
                instantDTO
        );
    }


    private static SatOxDTO satOx2SatOxDTO(SatOx satOx) {
        if (satOx == null) {
            throw new IllegalArgumentException("Saturação de Oxigénio não válida.");
        }

        // Obter o instant (horário) e mapeá-lo para o DTO
        Instant instant = satOx.getInstant();
        Data data = instant.getData(); // Obtemos a data do Instant

        // Criar o DTO para a Data
        DataDTO dataDTO = new DataDTO(
                data.getDia(),
                data.getMes(),
                data.getAno()
        );

        // Criar o DTO para o Instant
        InstantDTO instantDTO = new InstantDTO(
                instant.getHora(),
                instant.getMinuto(),
                instant.getSegundo(),
                dataDTO
        );

        // Retornar o DTO final
        return new SatOxDTO(
                satOx.getValor(),  // Acessando corretamente o método getNum()
                instantDTO       // Passando o DTO do Instant
        );
    }

    private static DataDTO data2DataDTO(Data nascimento) {

        DataDTO obj = new DataDTO(nascimento.getDia(), nascimento.getMes(), nascimento.getAno());
        return obj;
    }

    public static Pessoa pessoaDTO2pessoa(PessoaDTO pessoa) {
        Data nascimento = dataDTO2data(pessoa.getNascimento());
//        List<FreqCardiaca> freqCardiacas = freqCardiacaDTO2freCardiaca (pessoa.getFreqCardiacas());
//        List<SatOx> satOx = satOxDTO2satOx (pessoa.getSatOx());
//        List<Alertas> alertas = alertasDTO2alertas (pessoa.getAlertas());
        Pessoa pessoanova = new Pessoa(pessoa.getNome(), pessoa.getPassword(), nascimento, pessoa.getEmail(), pessoa.getTelemovel(), pessoa.getContactoEmergencia());
        return pessoanova;
    }


    public static FreqCardiaca freqCardiacaDTO2freqCardiaca(FreqCardiacaDTO freqCardiacasDTO) {
        LocalDateTime agora = LocalDateTime.now();
        Instant instant = new Instant(agora.getHour(), agora.getMinute(), agora.getSecond(), new Data(agora.getDayOfMonth(), agora.getMonthValue(), agora.getYear()));
        FreqCardiaca freqCardiaca = new FreqCardiaca(freqCardiacasDTO.getNum(), instant);
        return freqCardiaca;
    }

    public static SatOx satOxDTO2satOx(SatOxDTO satOxDTO) {
        LocalDateTime agora = LocalDateTime.now();
        Instant instant = new Instant(agora.getHour(), agora.getMinute(), agora.getSecond(), new Data(agora.getDayOfMonth(), agora.getMonthValue(), agora.getYear()));
        SatOx satOx = new SatOx(satOxDTO.getValor(), instant);
        return satOx;
    }

    public static Instant instantDTO2instant(InstantDTO instant) {
        Data instantData = dataDTO2data(instant.getData());
        Instant obj = new Instant(instant.getHora(), instant.getMinuto(), instant.getSegundo(), instantData);
        return obj;
    }

    private static Data dataDTO2data(DataDTO nascimento) {
        Data obj = new Data(nascimento.getDia(), nascimento.getMes(), nascimento.getAno());
        return obj;
    }

    public static PessoaDTO pessoa2pessoaDTO(Pessoa p) {
        System.out.println("Nascimento: " + p.getNascimento());
        DataDTO dataDTO = data2DataDTO(p.getNascimento());
        PessoaDTO pessoaDTO = new PessoaDTO(p.getNome(), p.getPassword(), dataDTO, p.getEmail(), p.getTelemovel(), p.getContactoEmergencia());
        return pessoaDTO;
    }


    public static List<AlertasDTO> alertas2AlertasDTO(List<Alertas> alertas) {
        List<AlertasDTO> list = new ArrayList<>();

        for (Alertas alerta : alertas) {
            AlertasDTO dto = alerta2AlertaDTO(alerta);
            list.add(dto);
        }
        return list;
    }

    public static AlertasDTO alerta2AlertaDTO(Alertas alerta) {
        if (alerta == null) {
            throw new IllegalArgumentException("Alerta não pode ser nula.");
        }

        // Mapeia a Data para DataDTO
        Data data = alerta.getInstant().getData();
        DataDTO dataDTO = new DataDTO(
                data.getDia(),
                data.getMes(),
                data.getAno()
        );

        // Mapeia o Instant para InstantDTO
        Instant instant = alerta.getInstant();
        InstantDTO instantDTO = new InstantDTO(
                instant.getHora(),
                instant.getMinuto(),
                instant.getSegundo(),
                dataDTO // Inclui o DataDTO mapeado
        );
        AlertasDTO alertaDTO = new AlertasDTO(alerta.getTipo(), alerta.getDescricao(), alerta.getValor(), instantDTO);

        // Retorna o DTO completo
        return alertaDTO;
    }
}
