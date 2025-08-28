package org.example.projeto_ws.Service;
import org.example.projeto_ws.Model.Instant;
import org.example.projeto_ws.Model.SatOx;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SatOxFacade {
    public static void addSatOxSQL(SatOx satOx, int pessoaID) {
        Connection con = PessoaFacade.buildConnection();

        if (con == null) {
            System.out.println("Falha na conexão com o banco de dados.");
            return;
        }

        // Verifica se pessoa_id existe na tabela Pessoa antes de inserir
        String checkPessoaSQL = "SELECT COUNT(*) FROM pessoa WHERE id = ?";
        String insertSQL = "INSERT INTO satox (pessoa_id, valor, instante) VALUES (?, ?, ?)";

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
            insertStmt.setInt(2, satOx.getValor());
            insertStmt.setString(3, satOx.getInstant().toString());

            int rowsInserted = insertStmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("SpO2 adicionada com sucesso!");
            }

            con.commit(); // Confirma a transação

        } catch (SQLException e) {
            System.out.println("Erro ao adicionar SpO2: " + e.getMessage());
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

    public static ArrayList<SatOx> getSatOxSQL(int pessoaID) {
        Connection con = PessoaFacade.buildConnection();
        ArrayList<SatOx> satOxes = new ArrayList<>();

        if (con == null) {
            System.out.println("Falha na conexão com o banco de dados.");
            return satOxes;
        }

        String sql = "SELECT * FROM satox Where pessoa_id = " + pessoaID;

        try (PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                SatOx satOx = new SatOx(rs.getInt("valor"), Instant.stringToInstant(rs.getString("instante")));
                satOxes.add(satOx);
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

        return satOxes;
    }
}
