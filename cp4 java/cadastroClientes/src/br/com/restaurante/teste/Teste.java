package br.com.restaurante.teste;

import br.com.restaurante.model.Model;

import java.util.Scanner;
import java.util.UUID;

public class Teste{
    private static final Scanner scanner = new Scanner(System.in);
    private static final Model model = new Model();
    private static String usuarioLogado = null;  // Armazena o email do usuário logado

    public static void main(String[] args) {
        int opcao = 0;

        while (opcao != 4) {
            exibirMenu();
            opcao = scanner.nextInt();
            scanner.nextLine();  // Limpar o buffer de entrada

            switch (opcao) {
                case 1:
                    cadastrarUsuario();
                    break;
                case 2:
                    loginUsuario();
                    break;
                case 3:
                    deletarConta();
                    break;
                case 4:
                    System.out.println("Saindo... Até logo!");
                    break;
                default:
                    System.out.println("Opção inválida! Tente novamente.");
            }
        }
    }

    // Exibe o menu para o usuário
    private static void exibirMenu() {
        System.out.println("\n----- MENU -----");
        System.out.println("1. Cadastrar nova conta");
        System.out.println("2. Fazer login");
        System.out.println("3. Deletar conta");
        System.out.println("4. Sair");
        System.out.print("Escolha uma opção: ");
    }

    // Função para cadastrar um novo usuário
    private static void cadastrarUsuario() {
        System.out.println("\n--- Cadastro de Usuário ---");

        // Gerando um ID semi-aleatório com UUID
        String id = gerarIdSemiAleatorio();

        System.out.println("ID gerado para o usuário: " + id);

        System.out.print("Digite o nome do usuário: ");
        String nome = scanner.nextLine();

        System.out.print("Digite o email do usuário: ");
        String email = scanner.nextLine();

        System.out.print("Digite a senha do usuário: ");
        String senha = scanner.nextLine();

        System.out.print("Digite o telefone do usuário (somente números): ");
        String telefone = scanner.nextLine();

        System.out.print("Digite o documento do usuário (somente números): ");
        String documento = scanner.nextLine();

        model.inserirUsuario(id, nome, email, senha, telefone, documento);
    }

    private static String gerarIdSemiAleatorio() {
        // Gera um UUID e pega os primeiros 10 caracteres para garantir que o ID não seja muito longo
        UUID uuid = UUID.randomUUID();
        return uuid.toString().substring(0, 10); // Pega apenas os primeiros 10 caracteres
    }

    // Função para realizar o login do usuário
    private static void loginUsuario() {
        System.out.println("\n--- Login de Usuário ---");

        System.out.print("Digite o email: ");
        String email = scanner.nextLine();

        System.out.print("Digite a senha: ");
        String senha = scanner.nextLine();

        boolean loginSucesso = model.loginUsuario(email, senha);

        if (loginSucesso) {
            System.out.println("Acesso permitido!");
            usuarioLogado = email;  // Armazena o email do usuário logado
        } else {
            System.out.println("Acesso negado! Credenciais incorretas.");
        }
    }

    // Função para deletar a conta do usuário
    private static void deletarConta() {
        if (usuarioLogado == null) {
            System.out.println("Você precisa estar logado para deletar a conta!");
            return;
        }

        System.out.println("\n--- Deletar Conta ---");

        System.out.print("Digite a senha para confirmar a exclusão da conta: ");
        String senha = scanner.nextLine();

        // Verificar se a senha está correta para o usuário logado
        boolean senhaCorreta = model.verificarSenha(usuarioLogado, senha);

        if (senhaCorreta) {
            boolean sucesso = model.deletarUsuario(usuarioLogado, senha);
            if (sucesso) {
                System.out.println("Conta deletada com sucesso!");
                usuarioLogado = null;  // Limpa o usuário logado após exclusão
            } else {
                System.out.println("Erro ao deletar a conta! ID não encontrado.");
            }
        } else {
            System.out.println("Senha incorreta! Conta não foi deletada.");
        }
    }
}
