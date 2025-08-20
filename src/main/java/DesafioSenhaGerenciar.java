import java.io.*;
import java.nio.file.*;
import java.security.SecureRandom;
import java.util.*;

import java.util.Base64;

public class DesafioSenhaGerenciar {

  private static final String FILE_NAME = ".passwords";
  private static final int PASSWORD_LENGTH_MAX = 16;
  private static final SecureRandom random = new SecureRandom();

  public static void main(String[] args) {
    if (args.length == 0) {
      mostrarAjuda();
      return;
    }

    Path filePath = Paths.get(FILE_NAME);
    Map<String, String> senhas = carregarSenhas(filePath);

    switch (args[0]) {
      case "--list":
        listarSenhas(senhas);
        break;

      case "--update":
        if (args.length < 2) {
          System.out.println("Uso: java DesafioSenhaGerenciar --update <nome rede>");
          return;
        }

        atualizarSenha(filePath, senhas, args[1]);
        break;

      case "--delete":
        if (args.length < 2) {
          System.out.println("Uso: java DesafioSenhaGerenciar --delete <nome rede>");
          return;
        }

        apagarSenha(filePath, senhas, args[1]);
        break;

      case "--help":
        mostrarAjuda();
        break;

      default:

        String redeSocial = args[0];
        int length = PASSWORD_LENGTH_MAX;

        if (args.length == 3 && args[1].equals("--length")) {

          try {
            length = Integer.parseInt(args[2]);
          } catch (NumberFormatException e) {

            System.out.println("Tamanho Invalido, usando padrao (" + PASSWORD_LENGTH_MAX + ")");
            
          }
          
        }

        buscarOuGerarSenha(filePath, senhas, redeSocial, length);
        break;
    }
    
  }

  private static Map<String, String> carregarSenhas(Path filePath) {

    Map<String, String> senhas = new HashMap<>();

    try {
      if (!Files.exists(filePath)) {
        Files.createFile(filePath);
      }

      List<String> linhas = Files.readAllLines(filePath);

      for (String linha : linhas) {

        String[] partes = linha.split(":", 2);

        if (partes.length == 2) {

          senhas.put(partes[0], partes[1]);
          
        }
        
      }
    } catch (IOException e) {
      System.out.println("Erro ao Carregar Senhas: " + e.getMessage());
    }
    return senhas;
  }

  private static void salvarSenhas(Path filePath, Map<String, String> senhas) {

    try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
      for (Map.Entry <String, String> entry : senhas.entrySet()) {
        writer.write(entry.getKey() + ":" + entry.getValue());
        writer.newLine();
      }
    } catch (IOException e) {
      System.out.println("Erro ao salvar senhas: " + e.getMessage());
    }
  }
  
  private static void buscarOuGerarSenha(Path filePath, Map<String, String> senhas, String rede, int length) {
    if (senhas.containsKey(rede)) {

      System.out.println("Senha já existente para "+ rede + ": " + senhas.get(rede));
    } else {

      String senha = gerarSenhaForte(length);
      senhas.put(rede, senha);
      salvarSenhas(filePath, senhas);
      System.out.println("Nova senha gerada para " + rede + ": " + senha);
    }
  }

  private static void atualizarSenha(Path filePath, Map<String, String> senhas, String rede) {
    if (senhas.containsKey(rede)) {
      String senha = gerarSenhaForte(DesafioSenhaGerenciar.PASSWORD_LENGTH_MAX);
      senhas.put(rede, senha);
      salvarSenhas(filePath, senhas);
      System.out.println("Senha atualizada para "+ rede + ": "+ senha);
    } else {
      System.out.println("Rede social não encontrada: " + rede);
    }
  }

  private static void apagarSenha(Path filePath, Map<String, String> senhas, String rede) {
    if (senhas.remove(rede) != null) {
      salvarSenhas(filePath, senhas);
      System.out.println("Senha da rede " + rede + " removida com sucesso.");
    } else {
      System.out.println("Rede social não encontrada: " + rede);
    }
  }

  private static void listarSenhas(Map<String, String> senhas) {
    if (senhas.isEmpty()) {
      System.out.println("Nenhuma senha cadastrada.");
    } else {
      System.out.println("Senhas salvas:");
      senhas.forEach((rede, senha) -> System.out.println(rede + ": " + senha));
    }
  }

  private static void mostrarAjuda() {
    System.out.println("Uso: java DesafioSenhaGerenciar <opcao> [argumentos]");
    System.out.println("\nOpcoes:");
    System.out.println("\t<rede> [--length N]\tBusca ou gera senha para a rede(padrão 16 caracteres)");
    System.out.println("\t--list\tLista todas as senhas");
    System.out.println("\t--update <rede>\tRemove a senha de uma rede existente");
    System.out.println("\t--delete <rede>\tRemove a senha de uma rede");
    System.out.println("\t--help\tMostra este manual");
  }

  private static String gerarSenhaForte(int length) {
    byte[] bytes = new byte[length];
    random.nextBytes(bytes);
    String senha = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    return senha.length() > length ? senha.substring(0, length) : senha;
  }
}
