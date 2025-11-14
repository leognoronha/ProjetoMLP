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

### `mvn exec:java -Dexec.args="programas/nome_teste.mlp"`
- **O que faz**: Executa o compilador MLP processando o arquivo especificado através de três fases sequenciais
- **Parâmetros**:
  - `exec:java`: Plugin do Maven que executa uma classe Java
  - `-Dexec.args="programas/nome_teste.mlp"`: Define o caminho do arquivo `.mlp` que será passado como argumento para o método `main()` da classe `br.com.mlp.App`

- **Fases de Execução**:
  1. **Fase Léxica (Tokenização)**:
     - Lê o arquivo fonte e divide o código em tokens (palavras reservadas, identificadores, números, operadores, etc.)
     - Identifica a posição (linha e coluna) de cada token
     - Marca tokens reservados (palavras-chave da linguagem)
     - Detecta e reporta erros léxicos (símbolos não reconhecidos)
     - **Saída**: Lista completa de todos os tokens encontrados com suas informações
  
  2. **Fase Sintática (Parsing)**:
     - Verifica se a sequência de tokens segue a gramática da linguagem MLP
     - Constrói uma árvore de parsing (ParseTree) se a estrutura estiver correta
     - Detecta e reporta erros sintáticos (estruturas inválidas, tokens inesperados)
     - **Saída**: Indicação de que a análise sintática foi executada
  
  3. **Fase AST (Abstract Syntax Tree)**:
     - Constrói a Abstract Syntax Tree a partir da ParseTree (apenas se não houver erros sintáticos)
     - A AST representa a estrutura semântica do programa de forma hierárquica
     - **Saída**: Representação textual da AST mostrando declarações e comandos do programa
  
  4. **Consolidação de Erros**:
     - Coleta todos os erros encontrados nas fases anteriores
     - Exibe um resumo consolidado com códigos de erro, localização (linha/coluna) e descrição
     - **Saída**: Lista de erros (se houver) ou mensagem de sucesso
- **Resultado**: O compilador executa todas as fases e exibe a saída formatada de cada uma, permitindo identificar problemas em qualquer etapa do processo de compilação

## 📝 Exemplo de Saída Esperada

Ao executar o comando `mvn exec:java -Dexec.args="programas/nome_teste.mlp"`, você verá a saída organizada em seções correspondentes às três fases de compilação:

### Estrutura da Saída

A saída é dividida em quatro seções principais:

1. **== Léxico ==**: Lista de todos os tokens encontrados
2. **== Sintático ==**: Indicação da execução da análise sintática
3. **== AST ==**: Representação da Abstract Syntax Tree
4. **Consolidação**: Resumo de erros ou mensagem de sucesso

### Exemplo Completo

Para um arquivo válido (ex: `programas/teste1.mlp`), a saída será:

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

== Semântica ==
Tabela de Símbolos:
  - a : INTEIRO (linha 2, col 9)
  - b : INTEIRO (linha 2, col 12)

Sem erros léxicos/sintáticos/semânticos nesta fase.
```

### Interpretação da Saída

Cada seção da saída fornece informações específicas:

- **Fase Léxica (`== Léxico ==`)**:
  - Cada linha mostra um token encontrado no formato: `Linha X, Col Y -> TIPO_TOKEN 'texto' [reservada]`
  - `TIPO_TOKEN`: Nome simbólico do token (ex: `IDENT`, `NUM`, `SE`, `INTEIRO`)
  - `'texto'`: O texto literal do token no código fonte
  - `[reservada]`: Aparece apenas para palavras-chave da linguagem (ex: `inteiro`, `se`, `entao`)
  - Se houver erros léxicos, uma mensagem de aviso será exibida após a lista de tokens

- **Fase Sintática (`== Sintático ==`)**:
  - Esta seção indica que a análise sintática foi executada
  - Se houver erros sintáticos, uma mensagem de aviso será exibida e a fase AST será pulada
  - Caso contrário, a fase AST será executada

- **Fase AST (`== AST ==`)**:
  - Mostra a estrutura hierárquica do programa em formato textual
  - `Program`: Nó raiz contendo declarações e comandos
  - `Decls`: Lista de declarações de variáveis com seus tipos
  - `Commands`: Lista de comandos (atribuições, condicionais, loops, etc.)
  - A representação mostra a estrutura aninhada do programa de forma legível

- **Consolidação de Erros**:
  - Se **não houver erros**: Exibe `"Sem erros léxicos/sintáticos nesta fase."`
  - Se **houver erros**: Exibe uma seção `== Erros (consolidados) ==` com:
    - Código do erro (formato `COD.XXX`)
    - Tipo do erro (léxico, sintático ou semântico)
    - Localização (linha e coluna)
    - Descrição do problema
    - Símbolo que causou o erro

**Nota**: A saída pode variar dependendo do conteúdo do arquivo `.mlp` processado. Arquivos com erros mostrarão mensagens de diagnóstico detalhadas, enquanto arquivos válidos mostrarão a AST completa.

## 📁 Estrutura do Projeto

```
src
├── main
│   ├── antlr4
│   │   └── br
│   │       └── com
│   │           └── mlp
│   │               └── compiler
│   │                   └── parser
│   │                       └── Mlp.g4
│   ├── java
│   │   └── br
│   │       └── com
│   │           └── mlp
│   │               ├── compiler
│   │               │   ├── ast
│   │               │   │   ├── AssignNode.java
│   │               │   │   ├── AstBuilder.java
│   │               │   │   ├── AstNode.java
│   │               │   │   ├── BinaryExprNode.java
│   │               │   │   ├── CommandNode.java
│   │               │   │   ├── ConditionNode.java
│   │               │   │   ├── DeclNode.java
│   │               │   │   ├── ExpressionNode.java
│   │               │   │   ├── IfNode.java
│   │               │   │   ├── NumLiteralNode.java
│   │               │   │   ├── ProgramNode.java
│   │               │   │   ├── Type.java
│   │               │   │   ├── VarRefNode.java
│   │               │   │   └── WhileNode.java
│   │               │   ├── codegen
│   │               │   │   ├── CodeGenerator.java
│   │               │   │   ├── Opcode.java
│   │               │   │   └── TacInstruction.java
│   │               │   └── semantics
│   │               │       ├── SemanticAnalyzer.java
│   │               │       └── SymbolTable.java
│   │               ├── diagnostics
│   │               │   ├── Diagnostic.java
│   │               │   ├── ErrorCode.java
│   │               │   ├── ErrorReporter.java
│   │               │   ├── ErrorType.java
│   │               │   └── MlpSyntaxErrorListener.java
│   │               ├── lex
│   │               │   ├── TokenInfo.java
│   │               │   └── TokenScanner.java
│   │               └── App.java
│   └── resources
│       └── grammars
└── test
    └── java
        └── br
            └── com
                └── mlp
                    └── AppTest.java
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
