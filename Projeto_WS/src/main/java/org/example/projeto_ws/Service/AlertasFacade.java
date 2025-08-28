package org.example.projeto_ws.Service;

import org.example.projeto_ws.Model.Alertas;
import org.example.projeto_ws.Model.FreqCardiaca;
import org.example.projeto_ws.Model.Instant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AlertasFacade {
    public static void addAlertasSQL(Alertas alertas, int pessoaID) {
        Connection con = PessoaFacade.buildConnection();

        if (con == null) {
            System.out.println("Falha na conexão com o banco de dados.");
            return;
        }

        // Verifica se pessoa_id existe na tabela Pessoa antes de inserir
        String checkPessoaSQL = "SELECT COUNT(*) FROM pessoa WHERE id = ?";
        String insertSQL = "INSERT INTO alertas (pessoa_id, tipo, descricao, valor, instante) VALUES (?, ?, ?, ?, ?)";

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
            insertStmt.setString(2, alertas.getTipo());
            insertStmt.setString(3, alertas.getDescricao());
            insertStmt.setInt(4, alertas.getValor());
            insertStmt.setString(5, alertas.getInstant().toString());

            int rowsInserted = insertStmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Alerta adicionada com sucesso!");
            }

            con.commit(); // Confirma a transação

        } catch (SQLException e) {
            System.out.println("Erro ao adicionar alerta: " + e.getMessage());
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

    public static ArrayList<Alertas> getAlertasSQL(int pessoaID) {
        Connection con = PessoaFacade.buildConnection();
        ArrayList<Alertas> alertas = new ArrayList<>();

        if (con == null) {
            System.out.println("Falha na conexão com o banco de dados.");
            return alertas;
        }

        String sql = "SELECT * FROM alertas Where pessoa_id = " + pessoaID;

        try (PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Alertas alertas1 = new Alertas(rs.getString("tipo"), rs.getString ("descricao"), rs.getInt ("valor"),Instant.stringToInstant(rs.getString("instante")));
                alertas.add(alertas1);
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
        return alertas;
    }

    public static void deleteAlertasSQL(int pessoaID) {
        Connection con = PessoaFacade.buildConnection();

        if (con == null) {
            System.out.println("Falha na conexão com o banco de dados.");
            return;
        }

        String sql = "DELETE FROM alertas WHERE pessoa_id = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, pessoaID);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Alertas removidos com sucesso.");
            } else {
                System.out.println("Nenhum alerta encontrado para remover.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao deletar alertas: " + e.getMessage());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }
}
