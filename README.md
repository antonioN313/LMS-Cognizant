# LMS-Cognizant — Projetos de Nivelamento

> Repositório com os exercícios práticos feitos no curso de nivelamento **LMS-Cognizant**. Este README descreve os arquivos presentes na branch `master`, como executar cada desafio, a estrutura do código, decisões de implementação e recomendações para testes e melhorias.

---

## Sumário

- [Visão geral do repositório](#visão-geral-do-repositório)
- [Pré-requisitos](#pré-requisitos)
- [Como compilar e executar](#como-compilar-e-executar)
- [Descrição dos desafios e arquivos](#descrição-dos-desafios-e-arquivos)
  - [Cadastro de chave PIX (`DesafioChavePix.java`)](#cadastro-de-chave-pix-desafiochavepixjava)
  - [Password Manager (`DesafioSenhaGerenciar.java`)](#password-manager-desafiosenhagerenciarjava)
  - [Validador de Senha (`DesafioValidarSenha.java`)](#validador-de-senha-desafiovalidarsenhajava)
  - [Exercício introdutório (`PrimeiroPrograma.java`)](#exercício-introdutório-primeiroprogramajava)
- [Formato dos arquivos de persistência](#formato-dos-arquivos-de-persistência)
- [Regras de validação implementadas](#regras-de-validação-implementadas)
- [Segurança e observações importantes](#segurança-e-observações-importantes)

---

## Visão geral do repositório

Este repositório contém exercícios em **Java** realizados como parte do curso de nivelamento. O objetivo foi praticar programação orientada a objetos, CLI (linha de comando), leitura/escrita em arquivo e implementações de validadores (CPF, CNPJ, telefone, email e regras de senha).

**Branch atual:** `master` (contém os arquivos citados abaixo).

**Observação:** Todos os desafios estão armazenados em um único arquivo, ao invés das boa praticas como a separação em arquivos com suas propriedades distintas e pacotes para cada atividade de dasafio, entre outros. Por essa razão, os arquivos apresentam o inicio `desafio<nome>.java`  

---

## Pré-requisitos

- Java 17 LTS ou superior (O código utiliza `record`, `Streams` na atividade [Cadastro de Chave PIX](#cadastro-de-chave-pix-desafiochavepixjava)).
- Terminal / linha de comando (Windows PowerShell/Prompt, macOS Terminal, Linux shell).

---

## Como compilar e executar

1. Abra um terminal na pasta do repositório (onde os arquivos `.java` estão localizados).

2. Compile todos os arquivos .java:

```bash
javac *.java
```

3. Execute o programa desejado. Exemplos abaixo.

---

## Descrição dos desafios e arquivos

### Cadastro de chave PIX (`DesafioChavePix.java`)

**O que faz**

Programa de linha de comando que valida uma chave PIX de acordo com o tipo informado (CPF, CNPJ, TELEFONE, EMAIL) e, opcionalmente, persiste a chave em um arquivo `.chaves` caso seja nova.

**Assinatura / execução**

```bash
java DesafioChavePix <TIPO> <VALOR> <INSTITUICAO>
# Exemplo:
java DesafioChavePix CPF 123.456.789-09 BancoX
```

**Implementação — pontos principais**

- Usa `record` para representar uma `chavePix` com campos: `tipo`, `valor`, `instituicao`.
- Implementa validadores específicos (implementando uma interface):
  - `validarCPF` — remove caracteres não numéricos e aplica o cálculo dos dígitos verificadores do CPF; rejeita sequências com todos dígitos iguais.
  - `validarCNPJ` — valida CNPJ com cálculo de dígitos verificadores.
  - `validarTelefone` — valida formato internacional (E.164) com regex (ex.: `+5511999998888`).
  - `validarEmail` — valida com regex prática para formatos comuns.
- `repositorioChavePix` — carrega e salva registros no arquivo local `.chaves` (formato `TIPO;VALOR;INSTITUICAO`).
- `servicoChavePix` — coordena validação e persistência; evita duplicidade (informa a instituição se a chave já existir).

**Observações**

- Regex para email/telefone cobre casos comuns mas não é à prova de todos os formatos possíveis. Para produção, usar bibliotecas consolidadas.

---

### Password Manager (`DesafioSenhaGerenciar.java`)

**O que faz**

Gerencia senhas por nome de "rede". Busca uma senha no arquivo `.passwords`. Se não existir, gera uma senha forte e salva. Disponibiliza operações básicas (buscar/gerar, listar, atualizar, deletar).

**Exemplos de uso**

```bash
# Buscar/gerar senha (padrão 16 chars)
java DesafioSenhaGerenciar facebook

# Gerar com tamanho customizado
java DesafioSenhaGerenciar facebook --length 12

# Listar todas as entradas
java DesafioSenhaGerenciar --list

# Atualizar/regenerar senha
java DesafioSenhaGerenciar --update facebook

# Deletar entrada
java DesafioSenhaGerenciar --delete facebook
```

**Formato do arquivo**

- Arquivo: `.passwords`
- Cada linha: `nomeDaRede:senha`

**Geração de senha**

- Usa `SecureRandom` + `Base64.getUrlEncoder().withoutPadding()` e trunca para o tamanho desejado.
- Padrão didático — o arquivo `.passwords` é texto simples (ver seção de segurança).

---

### Validador de Senha (`DesafioValidarSenha.java`)

**O que faz**

Valida se uma senha informada atende aos critérios mínimos de segurança:

- Mínimo de 8 caracteres
- Pelo menos 1 dígito
- Pelo menos 1 letra maiúscula
- Pelo menos 1 letra minúscula
- Pelo menos 1 caractere especial (não letra/número)

**Execução**

```bash
java DesafioValidarSenha "Minha$enha123"
# Saída: "Senha Valida" ou "Senha Invalida"
```

---

### Exercício introdutório (`PrimeiroPrograma.java`)

Programa simples que recebe argumentos e imprime uma saudação e o comprimento do nome.

---

## Formato dos arquivos de persistência

- `.chaves` — cada linha representa uma chave PIX no formato:

```
TIPO;VALOR;INSTITUICAO
# Ex.: CPF;12345678909;BancoX
```

- `.passwords` — cada linha representa uma entrada de senha:

```
rede:senha
# Ex.: facebook:Xk3$9aLpQ2z8w1Vb
```

> Observação: O repositório **não** deve conter os arquivos `.chaves` e `.passwords` com dados reais. Esses arquivos são gerados em tempo de execução e, se existir sensibilidade de dados, remova-os antes de tornar o repositório público.

---

## Regras de validação implementadas

**CPF** — algoritmo clássico de dígitos verificadores após remoção de caracteres não numéricos; CPFs com todos dígitos iguais são considerados inválidos.

**CNPJ** — validação por pesos e dígitos verificadores.

**Telefone** — validação baseada em E.164 (string iniciada por `+` seguido do código do país e número; total 7–15 dígitos).

**Email** — regex prática para capturar formatos comuns (`local@dominio.tld`).

**Senha** — ver secção do validador.

---

## Segurança e observações importantes

- **Armazenamento:** Os arquivos `.chaves` e `.passwords` são armazenados em texto claro. Em ambientes reais, **nunca** armazene senhas em texto legível — utilize criptografia forte (AES com chave derivada via PBKDF2/Argon2) ou um cofre de segredos.

- **Threads / concorrência:** A implementação usa leitura/escrita simples de arquivo. Em cenários com múltiplos acessos concorrentes, adicionar sincronização e bloqueio de arquivo.

- **Validação de email/telefone:** Para garantir conformidade ampla, use bibliotecas maduras (`Apache Commons Validator`, `Google libphonenumber`).

- **Dados sensíveis no repositório público:** Remova qualquer arquivo que contenha dados reais antes de publicar (use `.gitignore` para ignorar `.chaves` e `.passwords`).

---

