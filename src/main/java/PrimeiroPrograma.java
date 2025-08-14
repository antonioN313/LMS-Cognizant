public class PrimeiroPrograma {
    public static void main(String[] args){
        int quantidadeParametros = args.length;

        if (quantidadeParametros < 1){
            System.out.println("Informe pelo menos um nome");
            return;
        }

        String nome = "";

        for(String s : args){
            nome = nome + s + " ";
        }

        nome = nome.trim();

        System.out.println("Olá " + nome + "!");
        System.out.println("Seu nome tem " + nome.length() + " letras");
    }
}
