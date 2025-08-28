package com.example.cardiohealth.Controller;

import android.os.Build;
import androidx.annotation.RequiresApi;
import com.example.cardiohealth.DTO.*;
import com.example.cardiohealth.Model.*;
import com.example.cardiohealth.Model.view.AlertasContainer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Mapper {

    public static List<FreqCardiacaDTO> freq2FreqCardiacasDTO (List<FreqCardiaca> freqCardiacas) {
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
            // Passa um único objeto SatOx, não a lista inteira
            SatOxDTO dto = satOx2SatOxDTO(satOx);
            list.add(dto);
        }
        return list;
    }



    public static FreqCardiacaDTO freqCardiaca2FreqCardiacaDTO(FreqCardiaca freqCardiaca) {
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
        Pessoa pessoanova = new Pessoa( pessoa.getNome(), pessoa.getPassword(),nascimento, pessoa.getEmail(), pessoa.getTelemovel(), pessoa.getContactoEmergencia());
        return pessoanova;
    }

//    private static List<Alertas> alertasDTO2alertas(List<AlertasDTO> alertasDTO) {
//        List<Alertas> alertas = null;
//        for (int i = 0; i <alertasDTO.size(); i++) {
//            Alertas alerta = new Alertas(alertasDTO.get(i).getTipo(), alertasDTO.get(i).getDescricao(),alertasDTO.get(i).getValor(), alertasDTO.get(i).getDataHora());
//            alertas.add(alerta);
//        }
//        return alertas;
//    }

    private static List<SatOx> satOxDTO2satOx(List<SatOxDTO> satOxDTO) {
        List<SatOx> satOxes = null;
        for (int i = 0; i < satOxDTO.size(); i++) {
            Instant instant = instantDTO2instant (satOxDTO.get(i).getInstant());
            SatOx satOx =new SatOx(satOxDTO.get(i).getValor(), instant);
            satOxes.add(satOx);
        }
        return satOxes;
    }


    public static FreqCardiaca freqCardiacaDTO2freqCardiaca(FreqCardiacaDTO freqCardiacasDTO) {
        LocalDateTime agora = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            agora = LocalDateTime.now();
        }

        // Criar o objeto Data (classe customizada)
        Data data = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            data = new Data(agora.getDayOfMonth(), agora.getMonthValue(), agora.getYear());
        }

        // Criar o objeto Instant (classe customizada)
        Instant instant = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            instant = new Instant(agora.getHour(), agora.getMinute(), agora.getSecond(), data);
        }

        // Criar o objeto FreqCardiaca
        FreqCardiaca freqCardiaca = new FreqCardiaca(freqCardiacasDTO.getNum(), instant);
        return freqCardiaca;
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
        DataDTO dataDTO = data2DataDTO(p.getNascimento());
        PessoaDTO pessoaDTO = new PessoaDTO(p.getNome(), p.getPassword(), dataDTO, p.getEmail(), p.getTelemovel(), p.getContactoEmergencia());
        return pessoaDTO;
    }
    public static PessoaLogInDTO pessoaLogIn2pessoaLogInDTO (PessoaLogIn p) {
        PessoaLogInDTO pessoaLogInDTO = new PessoaLogInDTO(p.getEmail(),p.getPassword());
        return pessoaLogInDTO;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static AlertasContainer alertaContainerDTO2alertaContainer(AlertaContainerDTO dto) {
        ArrayList<Alertas> list = new ArrayList<>();
        try{
        for(AlertasDTO p : dto.getAlertas()){
            Alertas item = alertaDTO2alerta(p);
            list.add(item);
        }
        }catch(Error ex){
            System.out.println("Pessoa não tem alertas");
        }
        AlertasContainer  obj = new AlertasContainer(list);
        return obj;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static Alertas alertaDTO2alerta(AlertasDTO alertasDTO) {
            Instant instant = new Instant(alertasDTO.getDataHora().getHora(), alertasDTO.getDataHora().getMinuto(), alertasDTO.getDataHora().getSegundo(), new Data(alertasDTO.getDataHora().getData().getDia(), alertasDTO.getDataHora().getData().getMes(), alertasDTO.getDataHora().getData().getAno()));
            Alertas alerta = new Alertas(alertasDTO.getTipo(), alertasDTO.getDescricao(),alertasDTO.getValor(), instant);
        return alerta;
    }

    public static List<FreqCardiaca> freqCardiacaDTO2FreqCardiaca(List<FreqCardiacaDTO> freqCardiacasDTO) {
        List<FreqCardiaca> list = new ArrayList<>();

        for (FreqCardiacaDTO freqCardiacaDTO : freqCardiacasDTO) {
            FreqCardiaca freqCardiaca = freqCardiacaDTO2FreqCardiaca(freqCardiacaDTO);
            list.add(freqCardiaca);
        }
        return list;
    }

    public static FreqCardiaca freqCardiacaDTO2FreqCardiaca(FreqCardiacaDTO freqCardiacaDTO) {
        if (freqCardiacaDTO == null) {
            throw new IllegalArgumentException("FreqCardiacaDTO não pode ser nula.");
        }

        // Aqui você recria os objetos Instant e Data a partir do FreqCardiacaDTO
        InstantDTO instantDTO = freqCardiacaDTO.getInstant();
        DataDTO dataDTO = instantDTO.getData();

        // Criar a Data e Instant a partir dos DTOs
        Data data = new Data(dataDTO.getDia(), dataDTO.getMes(), dataDTO.getAno());
        Instant instant = new Instant(instantDTO.getHora(), instantDTO.getMinuto(), instantDTO.getSegundo(), data);

        // Retorna o objeto FreqCardiaca
        return new FreqCardiaca(freqCardiacaDTO.getNum(), instant);
    }

    public static List<SatOx> satOxDTO2SatOx(List<SatOxDTO> satOxsDTO) {
        List<SatOx> list = new ArrayList<>();

        for (SatOxDTO satOxDTO : satOxsDTO) {
            SatOx satOx = satOxDTO2satOx(satOxDTO);
            list.add(satOx);
        }
        return list;
    }

    public static SatOx satOxDTO2satOx(SatOxDTO satOxDTO) {
        if (satOxDTO == null) {
            throw new IllegalArgumentException("SatOxDTO não pode ser nula.");
        }

        // Aqui você recria os objetos Instant e Data a partir do SatOxDTO
        InstantDTO instantDTO = satOxDTO.getInstant();
        DataDTO dataDTO = instantDTO.getData();

        // Criar a Data e Instant a partir dos DTOs
        Data data = new Data(dataDTO.getDia(), dataDTO.getMes(), dataDTO.getAno());
        Instant instant = new Instant(instantDTO.getHora(), instantDTO.getMinuto(), instantDTO.getSegundo(), data);

        // Retorna o objeto SatOx
        return new SatOx(satOxDTO.getValor(), instant);
    }


}
