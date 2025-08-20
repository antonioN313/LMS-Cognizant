import java.io.*;
import java.nio.file.*;
import java.util.*;

public class DesafioChavePix {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Uso: java Main <tipoDaChave> <valorDaChave> <instituicao>");
            return;
        }

        String tipo = args[0];
        String valor = args[1];
        String instituicao = args[2];

        servicoChavePix service = new servicoChavePix();
        service.cadastrar(tipo, valor, instituicao);


    }
}

record chavePix (String tipo, String valor, String instituicao){
    @Override
    public String toString() {
        return tipo + ";" + valor + ";" + instituicao;
    }

    static chavePix fromString(String linha) {
        String[] argumentos = linha.split(";");
        return new chavePix (argumentos[0], argumentos[1], argumentos[2]);
    }
}

interface validarChavePix {
    boolean validar(String valor);
}

class validarCPF implements validarChavePix {
    @Override
    public boolean validar(String valor){
        if (!valor.matches("/[0-9]{3}\\.[\\d*]{3}\\.-?[0-9]{2}/")) return false;

        String cpf = valor.replaceAll("\\D", "");

        if (cpf.chars().distinct().count() == 1) return false;

        int dig1 = calcularDigito(cpf, 9, 10);
        int dig2 = calcularDigito(cpf, 10, 11);

        return dig1 == Character.getNumericValue(cpf.charAt(9)) &&
                dig2 == Character.getNumericValue(cpf.charAt(10));
    }

    private int calcularDigito(String cpf, int length, int pesoInicial) {
        int soma = 0;
        for (int i = 0; i < length; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (pesoInicial - i);
        }
        int resto = soma % 11;
        return (resto < 2) ? 0 : 11 - resto;
    }
}

class validarCNPJ implements validarChavePix {
    @Override
    public boolean validar(String valor){
        if (!valor.matches("^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$")) return false;

        String cnpj = valor.replaceAll("\\D", "");

        if (cnpj.chars().distinct().count() == 1) return false;

        int dig1 = calcularDigito(cnpj, 12, new int[]{5,4,3,2,9,8,7,6,5,4,3,2});
        int dig2 = calcularDigito(cnpj, 13, new int[]{6,5,4,3,2,9,8,7,6,5,4,3,2});

        return dig1 == Character.getNumericValue(cnpj.charAt(12)) &&
                dig2 == Character.getNumericValue(cnpj.charAt(13));
    }

    private int calcularDigito(String cnpj, int length, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < length; i++) {
            soma += Character.getNumericValue(cnpj.charAt(i)) * pesos[i];
        }
        int resto = soma % 11;
        return (resto < 2) ? 0 : 11 - resto;
    }
}

class validarTelefone implements validarChavePix{
    @Override
    public boolean validar(String valor) {
        return valor.matches("^\\+(?:[0-9] ?){6,14}[0-9]$");
    }
}

class validarEmail implements validarChavePix{
    public boolean validar(String valor) {
        return valor.matches("^[\\w!#$%&’*+/=?`{|}~^.-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^.-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");
    }
}

class repositorioChavePix {
    private static final Path FILE_PATH = Paths.get(".chaves");

    public List<chavePix> carregar() {
        if (!Files.exists(FILE_PATH)) return new ArrayList<>();
        try {
            return Files.readAllLines(FILE_PATH)
                    .stream()
                    .map(chavePix::fromString)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar chaves", e);
        }
    }

    public void salvar(chavePix key) {
        try (BufferedWriter writer = Files.newBufferedWriter(FILE_PATH,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(key.toString());
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar chave", e);
        }
    }
}

class servicoChavePix {
    private final repositorioChavePix repo = new repositorioChavePix();

    private final Map<String, validarChavePix> validadores = Map.of(
            "CPF", new validarCPF(),
            "CNPJ", new validarCNPJ(),
            "TELEFONE", new validarTelefone(),
            "EMAIL", new validarEmail()
    );

    public void cadastrar(String tipo, String valor, String instituicao){
        tipo = tipo.toUpperCase();
        if (!validadores.containsKey(tipo)) {
            System.out.println("Tipo de chave inválido.");
            return;
        }
        validarChavePix validador = validadores.get(tipo);
        if (!validador.validar(valor)) {
            System.out.println("Chave inválida.");
            return;
        }

        List<chavePix> existentes = repo.carregar();
        for (chavePix k : existentes) {
            if (k.valor().equals(valor)) {
                System.out.printf("Chave já existe na instituição: %s%n", k.instituicao());
                return;
            }
        }

        chavePix nova = new chavePix(tipo, valor, instituicao);
        repo.salvar(nova);
        System.out.println("Chave cadastrada com sucesso!");
    }
}


