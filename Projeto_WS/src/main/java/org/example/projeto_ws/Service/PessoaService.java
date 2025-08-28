package org.example.projeto_ws.Service;

import org.example.projeto_ws.Controlller.Mapper;
import org.example.projeto_ws.DTO.*;
import org.example.projeto_ws.Model.*;

import java.io.*;
import java.util.*;

public class PessoaService {
    private static List<Pessoa> pessoas = new ArrayList<>(); // Lista para armazenar pessoas
    private static List<Integer> idsDisponiveis = new ArrayList<>(); // IDs removidos disponíveis para reutilização
    private static int nextId = 1; // ID inicial será 1
    private static final String FILENAME = "C:/Users/35191/Desktop/projeto_lsisb/pessoas.dat";
    private File tempFile;
    File parentDir = tempFile.getParentFile();

    public PessoaService() {
    }

    public static void adicionarPessoa(PessoaDTO arg) {
//        carregarPessoas();
//
//        int id;
//
//        // Verifica se há IDs removidos para reutilizar
//        if (!idsDisponiveis.isEmpty()) {
//            idsDisponiveis.sort(Integer::compareTo); // Ordena os IDs disponíveis
//            id = idsDisponiveis.getFirst(); // Usa o menor ID disponível
//            idsDisponiveis.removeFirst();
//        } else {
//            id = nextId; // Se não houver IDs disponíveis, usa o próximo ID sequencial
//            nextId++; // Incrementa para o próximo ID a ser atribuído
//            for (int i = 0; i < idsDisponiveis.size(); i++) {
//                if (idsDisponiveis.get(i) == id) {
//                    idsDisponiveis.remove(i);
//                }
//
//            }
//        }

        pessoas = PessoaFacade.getPessoasSQL();

        for (Pessoa p : pessoas) {
            if (p.getEmail().equalsIgnoreCase(arg.getEmail())) {
                throw new IllegalArgumentException("Este email já se encontra registado noutra conta: " + arg.getEmail());
            }
        }

        Pessoa pessoa = Mapper.pessoaDTO2pessoa(arg);
        PessoaFacade.addPessoaSQL(pessoa);
//        pessoas.add(pessoa);
//        salvarPessoas();
    }

    private static void salvarPessoas() {
        File originalFile = new File(FILENAME);
        originalFile.delete();

        File newFile = new File(FILENAME); // Arquivo temporário
        File parentDir = newFile.getParentFile(); // Diretório pai do arquivo

        // Verificar e criar o diretório, se necessário
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs(); // Cria todos os diretórios necessários
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(newFile))) {
            oos.writeObject(pessoas); // Salva a lista de pessoas
            oos.writeObject(idsDisponiveis); // Salva a lista de IDs disponíveis
            oos.writeInt(nextId); // Salva o próximo ID
            System.out.println("Dados salvos com sucesso.");

        } catch (IOException e) {
            System.out.println("Erro ao salvar os dados: " + e.getMessage());
        }
    }

    public static PessoaContainerDTO getPessoas() {
//        carregarPessoas();
//        pessoas.sort(Comparator.comparingInt(Pessoa::getId)); // Ordena por ID
        pessoas = PessoaFacade.getPessoasSQL();
        PessoaContainerDTO pessoaContainerDTO = Mapper.pessoasContainer2pessoasDTO(pessoas);
        return pessoaContainerDTO;
    }

    public static PessoaDTO getPessoa(int id) {
//        carregarPessoas();
//        for (int i = 0; i < pessoas.size(); i++) {
//            Pessoa p = pessoas.get(i);
//            if (p.getId() == id) {
                PessoaDTO pessoaDTO = Mapper.pessoa2pessoaDTO(PessoaFacade.getPessoaSQL(id));
//                return pessoaDTO;
//            }
//        }
        return pessoaDTO;
    }

    private static void carregarPessoas() {
        File file = new File(FILENAME);
        if (!file.exists()) {
            System.out.println("Nenhum dado pré-existente foi encontrado.");
            return; // Arquivo não existe, nada a carregar
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            pessoas = (List<Pessoa>) ois.readObject(); // Carrega a lista de pessoas
            idsDisponiveis = (List<Integer>) ois.readObject(); // Carrega a lista de IDs disponíveis
            nextId = ois.readInt(); // Carrega o próximo ID
            System.out.println("Dados carregados com sucesso.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erro ao carregar os dados: " + e.getMessage());
            pessoas = new ArrayList<>(); // Reverte para listas vazias em caso de erro
            idsDisponiveis = new LinkedList<>();
            nextId = 1;
        }

    }

    public static void adicionarFrequenciaCardiaca(int id, FreqCardiacaDTO freqCardiacaDTO) {
//        carregarPessoas();
//        Pessoa pessoa = null;
//        for (Pessoa p : pessoas) {
//            if (p.getId() == id) {
//                pessoa = p;
//                break;
//            }
//        }
//        if (pessoa == null) {
//            throw new IllegalArgumentException("Pessoa com ID " + id + " não encontrada.");
//        }
        FreqCardiaca novaFreq = Mapper.freqCardiacaDTO2freqCardiaca(freqCardiacaDTO);
        FreqCardiacaFacade.addFreqCardiacaSQL(novaFreq, id);
        gerarAlertas(id, "Frequencia Cardiaca", novaFreq.getNum());
        // Adicionar a frequência cardíaca à pessoa
//        if ((pessoa.getFreqCardiacas() == null)) {
//            pessoa.setFreqCardiacas(new ArrayList<>());
//        }
//        pessoa.getFreqCardiacas().add(novaFreq);
//        // Gerar alertas com base na nova frequência
//        pessoa.gerarAlertas();
        // Salvar as alterações
//        salvarPessoas();
    }

    public static void gerarAlertas(int pessoaID, String tipo, int valor) {
        if (tipo.equals("Frequencia Cardiaca")) {
            if (valor < 60 || valor > 100) {
                String descricao = valor < 60 ? "Frequência cardíaca baixa" : "Frequência cardíaca alta";
                Alertas alertas = new Alertas(tipo, descricao, valor);
                AlertasFacade.addAlertasSQL(alertas, pessoaID);
            } else if (tipo.equals("Saturacao de Oxigenio")) {
                if (valor < 95) { // Exemplo de limite para saturação
                    String descricao = "Saturação de oxigênio baixa";
                    Alertas alertas = new Alertas(tipo, descricao, valor);
                    AlertasFacade.addAlertasSQL(alertas, pessoaID);
                }
            }
        }
    }

    public static void adicionarSaturacaoOxigenio(int id, SatOxDTO satOxDTO) {
//        carregarPessoas();
//
//        // Buscar a pessoa pelo ID
//        Pessoa pessoa = null;
//        for (Pessoa p : pessoas) {
//            if (p.getId() == id) {
//                pessoa = p;
//                break;
//            }
//        }
//
//        if (pessoa == null) {
//            throw new IllegalArgumentException("Pessoa com ID " + id + " não encontrada.");
//        }
        SatOx novaSatOx = Mapper.satOxDTO2satOx(satOxDTO);
        SatOxFacade.addSatOxSQL(novaSatOx, id);
        gerarAlertas(id, "Saturacao de Oxigenio", novaSatOx.getValor());
//        if ((pessoa.getSatOx() == null)) {
//            pessoa.setSatOx(new ArrayList<>());
//        }
//        // Adicionar o registro à lista da pessoa
//        pessoa.getSatOx().add(novaSatOx);
//
//        // Gerar alertas com base na nova saturação
//        pessoa.gerarAlertas();
//
//        // Salvar as alterações
//        salvarPessoas();
    }

    public static FreqCardiacaContainerDTO getFrequenciasCardiacas(int id) throws Exception {
        FreqCardiacaContainerDTO data = new FreqCardiacaContainerDTO();
//        carregarPessoas();
//        pessoas.sort(Comparator.comparingInt(Pessoa::getId));
//
//        Pessoa pessoa = pessoas.stream()
//                .filter(p -> p.getId() == id)
//                .findFirst()
//                .orElseThrow(() -> new Exception("Pessoa com ID " + id + " não encontrada."));


        data.setFreqCardiaca(Mapper.freq2FreqCardiacasDTO(FreqCardiacaFacade.getFreqCardiacaSQL(id)));
//        if ((pessoa.getFreqCardiacas() == null)) {
//            pessoa.setFreqCardiacas(new ArrayList<>());
//        }

        return data;
    }


    public static SatOxContainerDTO getSaturacoesOx(int id) throws Exception {
        SatOxContainerDTO data = new SatOxContainerDTO();
//        carregarPessoas();
//        pessoas.sort(Comparator.comparingInt(Pessoa::getId));
//
//        Pessoa pessoa = pessoas.stream()
//                .filter(p -> p.getId() == id)
//                .findFirst()
//                .orElseThrow(() -> new Exception("Pessoa com ID " + id + " não encontrada."));

        data.setSatOx(Mapper.sat2SatOxDTO(SatOxFacade.getSatOxSQL(id)));
//        if ((pessoa.getSatOx() == null)) {
//            pessoa.setSatOx(new ArrayList<>());
//        }
//
        return data;
    }



    public static PessoaDTO editarPessoa(int id, PessoaDTO pessoaDTO) {
        PessoaFacade.updatePessoaSQL(id, Mapper.pessoaDTO2pessoa(pessoaDTO));
        return Mapper.pessoa2pessoaDTO(PessoaFacade.getPessoaSQL(id));
    }

    public static boolean eliminarPessoa(int id) {
//        carregarPessoas();
        try {
            PessoaFacade.deletePessoaSQL(id);
            return true;
        }catch (Exception e) {
            return false;
        }


//        Iterator<Pessoa> iterator = pessoas.iterator();
//        while (iterator.hasNext()) {
//            Pessoa pessoa = iterator.next();
//            if (pessoa.getId() == id) {
//                iterator.remove(); // Remove a pessoa da lista
//                idsDisponiveis.add(id); // Adiciona o ID à lista de IDs disponíveis
//                idsDisponiveis.sort(Integer::compareTo); // Ordena a lista de IDs disponíveis
//                salvarPessoas();
//                return true;
//            }
//        }
    }

    public static AlertaContainerDTO getAlertas(int id) throws Exception {
        AlertaContainerDTO data = new AlertaContainerDTO();
//        carregarPessoas();
//        pessoas.sort(Comparator.comparingInt(Pessoa::getId));

//        Pessoa pessoa = pessoas.stream()
//                .filter(p -> p.getId() == id)
//                .findFirst()
//                .orElseThrow(() -> new Exception("Pessoa com ID " + id + " não encontrada."));

        data.setAlertas(Mapper.alertas2AlertasDTO(AlertasFacade.getAlertasSQL(id)));
//        if ((pessoa.getAlertas() == null)) {
//            pessoa.setAlertas(new ArrayList<>());
//        }

        return data;
    }

    public static int loginPessoa(PessoaLogInDTO arg) throws Exception {
//        carregarPessoas();
        Integer idPessoa = PessoaFacade.loginPessoaSQL(arg.getEmail(), arg.getPassword());
        return idPessoa;
    }

    public static boolean eliminarAlertas(int pessoaID) {
        try {
            AlertasFacade.deleteAlertasSQL(pessoaID);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

