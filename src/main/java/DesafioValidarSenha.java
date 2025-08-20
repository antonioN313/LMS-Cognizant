class DesafioValidarSenha {
  public static void main(String[] args) {

    String senha = args.length > 0 ? args[0] : " ";

    if (senha.length() >=8 && senha.matches(".*\\d.*") && senha.matches(".*[A-Z].*") 
        && senha.matches(".*[a-z].*") && senha.matches(".*[^a-zA-Z0-9].*")) 
    {
      System.out.println("Senha Valida");
    } else{
      System.out.println("Senha Invalida");
    }
    
  }
  
}
