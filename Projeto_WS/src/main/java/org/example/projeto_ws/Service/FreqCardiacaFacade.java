package org.example.projeto_ws.Service;

import org.example.projeto_ws.Model.FreqCardiaca;
import org.example.projeto_ws.Model.Instant;
import java.sql.*;
import java.util.ArrayList;

public class FreqCardiacaFacade {
    public static void addFreqCardiacaSQL(FreqCardiaca freqCardiaca, int pessoaID) {
        Connection con = PessoaFacade.buildConnection();

        if (con == null) {
            System.out.println("Falha na conexão com o banco de dados.");
            return;
        }

        // Verifica se pessoa_id existe na tabela Pessoa antes de inserir
        String checkPessoaSQL = "SELECT COUNT(*) FROM pessoa WHERE id = ?";
        String insertSQL = "INSERT INTO freqcardiaca (pessoa_id, num, instante) VALUES (?, ?, ?)";

        try (
                PreparedStatement checkStmt = con.prepareStatement(checkPessoaSQL);
                PreparedStatement insertStmt = con.prepareStatement(insertSQL)
        ) {
            con.setAutoCommit(false); // Inicia transação

            // Verifica se pessoa_id existe
            checkStmt.setInt(1, pessoaID);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                System.out.println("Erro: O ID da pessoa fornecido não existe na base de dados.");
                return;
            }

            // Insere os dados na tabela freqcardiaca
            insertStmt.setInt(1, pessoaID);
            insertStmt.setInt(2, freqCardiaca.getNum());
            insertStmt.setString(3, freqCardiaca.getInstant().toString());

            int rowsInserted = insertStmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Frequência Cardíaca adicionada com sucesso!");
            }

            con.commit(); // Confirma a transação

        } catch (SQLException e) {
            System.out.println("Erro ao adicionar frequência cardíaca: " + e.getMessage());
            try {
                con.rollback(); // Reverte em caso de erro
            } catch (SQLException rollbackEx) {
                System.out.println("Erro ao reverter transação: " + rollbackEx.getMessage());
            }
        } finally {
            try {
                con.setAutoCommit(true);
                con.close();
            } catch (SQLException e) {
                System.out.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }

    public static ArrayList<FreqCardiaca> getFreqCardiacaSQL(int pessoaID) {
        Connection con = PessoaFacade.buildConnection();
        ArrayList<FreqCardiaca> freqCardiacas = new ArrayList<>();

        if (con == null) {
            System.out.println("Falha na conexão com o banco de dados.");
            return freqCardiacas;
        }

        String sql = "SELECT * FROM freqcardiaca Where pessoa_id = " + pessoaID;

        try (PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                FreqCardiaca freqCardiaca = new FreqCardiaca(rs.getInt("num"), Instant.stringToInstant(rs.getString("instante")));
                freqCardiacas.add(freqCardiaca);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar pessoas: " + e.getMessage());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }

        return freqCardiacas;
    }
}
