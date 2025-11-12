# Projeto MLP

Compilador para a linguagem MLP desenvolvido em Java usando ANTLR4. Este projeto realiza a análise léxica, sintática e construção de AST (Abstract Syntax Tree) para programas escritos na linguagem MLP.

## 🎯 Funcionalidades

O compilador executa três fases principais:

1. **Análise Léxica**: Tokeniza o código fonte, identificando todos os tokens (palavras reservadas, identificadores, números, operadores, etc.) e reporta erros léxicos
2. **Análise Sintática**: Verifica se a sequência de tokens segue a gramática da linguagem MLP e reporta erros sintáticos
3. **Construção da AST**: Gera a Abstract Syntax Tree representando a estrutura do programa (apenas se não houver erros sintáticos)

O projeto também inclui um sistema completo de diagnóstico de erros, com códigos de erro específicos para cada tipo de problema encontrado.

## 📋 Requisitos

- **Java 17** ou superior
- **Maven 3.6+**

## Como Rodar

### Passo 1: Limpar e Compilar o Projeto

```bash
mvn clean
mvn compile
```

### Passo 2: Executar o Compilador

```bash
mvn exec:java -Dexec.args="programas/teste1.mlp"
```

## 📖 Explicação dos Comandos

### `mvn clean`
- **O que faz**: Remove todos os arquivos gerados anteriormente na pasta `target/`
- **Por que usar**: Garante que você está compilando do zero, sem arquivos antigos que possam causar problemas
- **Resultado**: A pasta `target/` é completamente removida

### `mvn compile`
- **O que faz**: 
  1. Compila o código Java em `src/main/java/`
  2. Gera os arquivos do parser ANTLR4 a partir da gramática `Mlp.g4` em `src/main/antlr4/`
  3. Compila os arquivos gerados
  4. Coloca todos os arquivos `.class` compilados em `target/classes/`
- **Por que usar**: Prepara o projeto para execução, gerando todos os arquivos necessários
- **Resultado**: O projeto está compilado e pronto para executar

### `mvn exec:java -Dexec.args="programas/teste1.mlp"`
- **O que faz**: 
  1. Executa a classe principal `br.com.mlp.App`
  2. Passa `programas/teste1.mlp` como argumento para o programa
  3. O programa executa as três fases de compilação:
     - **Fase Léxica**: Tokeniza o código e mostra todos os tokens encontrados
     - **Fase Sintática**: Verifica a estrutura sintática do programa
     - **Fase AST**: Constrói e exibe a Abstract Syntax Tree (apenas se não houver erros sintáticos)
  4. Ao final, consolida e exibe todos os erros encontrados (se houver)
- **Parâmetros**:
  - `exec:java`: Plugin do Maven que executa uma classe Java
  - `-Dexec.args="..."`: Define os argumentos que serão passados para o método `main()`
- **Resultado**: Executa o compilador mostrando as três fases e a consolidação de erros

## 📝 Exemplo de Saída Esperada

Ao executar o comando com o arquivo `programas/teste1.mlp`, você deve ver a seguinte saída:

```
== Léxico ==

Linha 1, Col 1 -> DOLLAR       '$'
Linha 2, Col 1 -> INTEIRO      'inteiro'  [reservada]
Linha 2, Col 9 -> IDENT        'a'
Linha 2, Col 10 -> COMMA        ','
Linha 2, Col 12 -> IDENT        'b'
Linha 2, Col 13 -> SEMI         ';'
Linha 3, Col 1 -> SE           'se'  [reservada]
Linha 3, Col 4 -> LPAREN       '('
Linha 3, Col 5 -> IDENT        'a'
Linha 3, Col 7 -> MAIOR        '>'
Linha 3, Col 9 -> NUM          '10'
Linha 3, Col 11 -> RPAREN       ')'
Linha 3, Col 13 -> ENTAO        'entao'  [reservada]
Linha 3, Col 19 -> IDENT        'b'
Linha 3, Col 21 -> ASSIGN       '='
Linha 3, Col 23 -> IDENT        'b'
Linha 3, Col 25 -> OP_SOMA      '+'
Linha 3, Col 27 -> NUM          '1'
Linha 3, Col 28 -> SEMI         ';'
Linha 4, Col 1 -> DOLLAR       '$'
Linha 4, Col 2 -> DOT          '.'

== Sintático ==

== AST ==

Program(
  Decls:
    Decl(INTEIRO a, b)
  Commands:
    If(Cond(Var(a) > Num(10)), then=Assign(b = Num(1)))
)

Sem erros léxicos/sintáticos nesta fase.
```

A saída mostra:
- **Fase Léxica**: Lista completa de todos os tokens encontrados, com linha, coluna, tipo do token e texto. Tokens reservados são marcados com `[reservada]`
- **Fase Sintática**: Indica que a análise sintática foi executada
- **Fase AST**: A estrutura da AST (Abstract Syntax Tree) construída a partir do código MLP
- **Consolidação**: Mensagem final indicando se foram encontrados erros ou não

## 📁 Estrutura do Projeto

```
comp-projeto/
├── src/
│   ├── main/
│   │   ├── antlr4/                    # Gramática ANTLR4 (Mlp.g4)
│   │   └── java/
│   │       └── br/com/mlp/
│   │           ├── App.java           # Classe principal
│   │           ├── compiler/
│   │           │   ├── ast/           # Nós da AST
│   │           │   └── parser/        # Parser gerado pelo ANTLR4
│   │           ├── lex/               # Sistema de análise léxica
│   │           │   ├── TokenScanner.java
│   │           │   └── TokenInfo.java
│   │           └── diagnostics/       # Sistema de diagnóstico de erros
│   │               ├── ErrorReporter.java
│   │               ├── Diagnostic.java
│   │               ├── ErrorCode.java
│   │               ├── ErrorType.java
│   │               └── MlpSyntaxErrorListener.java
│   └── test/                          # Testes
├── programas/                          # Arquivos .mlp para compilar
├── target/                            # Arquivos gerados (não versionado)
├── pom.xml                            # Configuração Maven
└── README.md                          # Este arquivo
```

### Componentes Principais

- **`App.java`**: Orquestra as três fases de compilação (léxica, sintática, AST)
- **`lex/`**: Módulo de análise léxica que tokeniza o código fonte
- **`diagnostics/`**: Sistema de diagnóstico que coleta e reporta erros de todas as fases
- **`compiler/ast/`**: Nós da Abstract Syntax Tree
- **`compiler/parser/`**: Parser gerado automaticamente pelo ANTLR4 a partir da gramática

## 🔍 Sistema de Diagnóstico de Erros

O compilador possui um sistema robusto de diagnóstico que identifica e reporta erros em todas as fases:

### Tipos de Erro

- **Léxico**: Símbolos não reconhecidos como tokens válidos
- **Sintático**: Estruturas que não seguem a gramática da linguagem
- **Semântico**: Erros de tipo, variáveis não declaradas, etc. (preparado para futuras implementações)

### Códigos de Erro

Cada erro possui um código numérico único:
- **001**: Símbolo não identificado como token (léxico)
- **100-101**: Erros sintáticos (token esperado, estrutura inválida)
- **200-203**: Erros semânticos (variável não declarada, tipo incompatível, etc.)

Quando erros são encontrados, eles são exibidos no final da execução com informações detalhadas incluindo linha, coluna e descrição do problema.
