package org.example.projeto_ws.Service;

import org.example.projeto_ws.Model.Pessoa;
import org.example.projeto_ws.Model.Data;
import java.sql.*;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PessoaFacade {

    private static final Logger logger = LoggerFactory.getLogger(PessoaFacade.class);

    public static Connection buildConnection() {
        String url = "jdbc:mysql://localhost:3306/projetows";
        String user = "root";
        String password = "";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            logger.error("Erro ao conectar ao banco de dados: {}", e.getMessage());
            return null;
        }
    }

    public static boolean addPessoaSQL(Pessoa p) {
        if (p.getNome() == null || p.getNome().isEmpty() || p.getEmail() == null || p.getEmail().isEmpty()) {
            logger.error("Nome e Email são obrigatórios.");
            return false;
        }

        String sql = "INSERT INTO pessoa (nome, password, nascimento, email, telemovel, contactoEmergencia) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = buildConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, p.getNome());
            stmt.setString(2, p.getPassword());
            stmt.setString(3, p.getNascimento().toString());
            stmt.setString(4, p.getEmail());
            stmt.setInt(5, p.getTelemovel());
            stmt.setInt(6, p.getContactoEmergencia());

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                logger.info("Pessoa adicionada com sucesso!");
                return true;
            }
        } catch (SQLException e) {
            logger.error("Erro ao adicionar pessoa: {}", e.getMessage());
        }
        return false;
    }

    public static boolean updatePessoaSQL(int id, Pessoa p) {
        if (p.getNome() == null || p.getNome().isEmpty() || p.getEmail() == null || p.getEmail().isEmpty()) {
            logger.error("Nome e Email são obrigatórios.");
            return false;
        }

        String sql = "UPDATE pessoa SET nome = ?, password = ?, nascimento = ?, email = ?, telemovel = ?, contactoEmergencia = ? WHERE id = ?";

        try (Connection con = buildConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, p.getNome());
            stmt.setString(2, p.getPassword());
            stmt.setString(3, p.getNascimento().toString());
            stmt.setString(4, p.getEmail());
            stmt.setInt(5, p.getTelemovel());
            stmt.setInt(6, p.getContactoEmergencia());
            stmt.setInt(7, id);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                logger.info("Pessoa atualizada com sucesso!");
                return true;
            } else {
                logger.warn("Nenhuma pessoa encontrada com o ID informado.");
            }
        } catch (SQLException e) {
            logger.error("Erro ao atualizar pessoa: {}", e.getMessage());
        }
        return false;
    }

    public static boolean deletePessoaSQL(int id) {
        String sql = "DELETE FROM pessoa WHERE id = ?";

        try (Connection con = buildConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id);

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                logger.info("Pessoa deletada com sucesso!");
                return true;
            } else {
                logger.warn("Nenhuma pessoa encontrada com o ID informado.");
            }
        } catch (SQLException e) {
            logger.error("Erro ao deletar pessoa: {}", e.getMessage());
        }
        return false;
    }

    public static Pessoa getPessoaSQL(int id) {
        String sql = "SELECT * FROM pessoa WHERE id = ?";
        Pessoa p = null;

        try (Connection con = buildConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                p = new Pessoa();
                p.setId(rs.getInt("id"));
                p.setNome(rs.getString("nome"));
                p.setPassword(rs.getString("password"));
                p.setNascimento(Data.stringToData(rs.getString("nascimento")));
                p.setEmail(rs.getString("email"));
                p.setTelemovel(rs.getInt("telemovel"));
                p.setContactoEmergencia(rs.getInt("contactoEmergencia"));
            } else {
                logger.warn("Nenhuma pessoa encontrada com o ID informado.");
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar pessoa: {}", e.getMessage());
        }
        return p;
    }

    public static ArrayList<Pessoa> getPessoasSQL() {
        ArrayList<Pessoa> pessoas = new ArrayList<>();
        String sql = "SELECT * FROM pessoa";

        try (Connection con = buildConnection(); PreparedStatement stmt = con.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Pessoa p = new Pessoa();
                p.setId(rs.getInt("id"));
                p.setNome(rs.getString("nome"));
                p.setPassword(rs.getString("password"));
                p.setNascimento(Data.stringToData(rs.getString("nascimento")));
                p.setEmail(rs.getString("email"));
                p.setTelemovel(rs.getInt("telemovel"));
                p.setContactoEmergencia(rs.getInt("contactoEmergencia"));
                pessoas.add(p);
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar pessoas: {}", e.getMessage());
        }
        return pessoas;
    }

    public static Integer loginPessoaSQL(String email, String password) {
        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            logger.error("Email e senha são obrigatórios.");
            return null;
        }

        String checkEmailSQL = "SELECT id FROM pessoa WHERE email = ?";
        String checkPasswordSQL = "SELECT id FROM pessoa WHERE email = ? AND password = ?";

        try (Connection con = buildConnection();
             PreparedStatement checkEmailStmt = con.prepareStatement(checkEmailSQL);
             PreparedStatement checkPasswordStmt = con.prepareStatement(checkPasswordSQL)) {

            checkEmailStmt.setString(1, email);
            ResultSet rsEmail = checkEmailStmt.executeQuery();

            if (!rsEmail.next()) {
                logger.error("Erro: Email não encontrado.");
                return null;
            }

            checkPasswordStmt.setString(1, email);
            checkPasswordStmt.setString(2, password);
            ResultSet rsPassword = checkPasswordStmt.executeQuery();

            if (rsPassword.next()) {
                return rsPassword.getInt("id");
            } else {
                logger.error("Erro: Senha incorreta.");
                return null;
            }
        } catch (SQLException e) {
            logger.error("Erro ao fazer login: {}", e.getMessage());
            return null;
        }
    }
}
