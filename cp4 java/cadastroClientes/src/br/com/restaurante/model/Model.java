package br.com.restaurante.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Model {
	private static final String URL = "jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL";
    private static final String USER = "rm553117";
    private static final String PASSWORD = "160102";

    private static final String INSERT = "INSERT INTO USUARIO(id_usuario, nome_usuario, email_usuario, senha_usuario, telefone_usuario, documento_usuario) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String LOGIN = "SELECT nome_usuario FROM USUARIO WHERE email_usuario = ? AND senha_usuario = ?";


    public Connection conectar() {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            Connection conexao = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexão bem-sucedida!");
            return conexao;
        } catch (ClassNotFoundException e) {
            System.out.println("Driver JDBC não encontrado!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Erro ao conectar ao banco!");
            e.printStackTrace();
        }
        return null;
    }

    public void fecharConexao(Connection conexao) {
        try {
            if (conexao != null && !conexao.isClosed()) {
                conexao.close();
                System.out.println("Conexão fechada com sucesso.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao fechar conexão!");
            e.printStackTrace();
        }
    }

    public void inserirUsuario(String id, String nome, String email, String senha, String telefone, String documento) {
        Connection conexao = conectar();
        if (conexao == null) {
            System.out.println("Erro ao conectar. Inserção abortada.");
            return;
        }

        try (PreparedStatement stmt = conexao.prepareStatement(INSERT)) {
            stmt.setString(1, id);
            stmt.setString(2, nome);
            stmt.setString(3, email);
            stmt.setString(4, senha);
            
            long telefoneNum = Long.parseLong(telefone.replaceAll("\\D", ""));
            long documentoNum = Long.parseLong(documento.replaceAll("\\D", ""));

            stmt.setLong(5, telefoneNum);
            stmt.setLong(6, documentoNum);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Usuário inserido com sucesso!");
            } else {
                System.out.println("Erro ao inserir usuário.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao inserir usuário no banco de dados.");
            e.printStackTrace();
        } finally {
            fecharConexao(conexao);
        }
    }

    public boolean loginUsuario(String email, String senha) {
        Connection conexao = conectar();
        if (conexao == null) {
            System.out.println("Erro ao conectar. Login abortado.");
            return false;
        }

        try (PreparedStatement stmt = conexao.prepareStatement(LOGIN)) {
            stmt.setString(1, email);
            stmt.setString(2, senha);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                System.out.println("Login bem-sucedido! Bem-vindo, " + rs.getString("nome_usuario") + "!");
                return true;
            } else {
                System.out.println("Falha no login. Verifique suas credenciais.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao efetuar login.");
            e.printStackTrace();
            return false;
        } finally {
            fecharConexao(conexao);
        }
    }
    public boolean verificarSenha(String email, String senha) {
        Connection conexao = conectar();
        if (conexao == null) {
            System.out.println("Erro ao conectar. Verificação de senha abortada.");
            return false;
        }

        String select = "SELECT senha_usuario FROM USUARIO WHERE email_usuario = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(select)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String senhaBD = rs.getString("senha_usuario");
                return senhaBD.equals(senha);
            } else {
                System.out.println("Usuário não encontrado.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao verificar a senha.");
            e.printStackTrace();
            return false;
        } finally {
            fecharConexao(conexao);
        }
    };

    // Método para deletar o usuário
    public boolean deletarUsuario(String email, String senha) {
        // Primeiro, verifica a senha fornecida pelo usuário
        boolean senhaCorreta = verificarSenha(email, senha);

        if (!senhaCorreta) {
            System.out.println("Senha incorreta! Conta não pode ser deletada.");
            return false;
        }

        // Se a senha estiver correta, exclui o usuário do banco de dados
        String delete = "DELETE FROM USUARIO WHERE email_usuario = ?";

        try (Connection conexao = conectar(); 
             PreparedStatement stmt = conexao.prepareStatement(delete)) {

            stmt.setString(1, email);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Conta deletada com sucesso!");
                return true;
            } else {
                System.out.println("Erro ao deletar a conta! Usuário não encontrado.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao deletar a conta.");
            e.printStackTrace();
            return false;
        }
    }
}
