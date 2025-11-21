# Projeto MLP

Compilador para a linguagem MLP desenvolvido em Java usando ANTLR4. Este projeto realiza análise léxica, sintática, construção de AST (Abstract Syntax Tree), análise semântica e geração de código intermediário (TAC) para programas escritos na linguagem MLP.

## 🎯 Funcionalidades

O compilador executa cinco fases principais:

1. **Análise Léxica**: Tokeniza o código fonte, identificando todos os tokens (palavras reservadas, identificadores, números, operadores, etc.) e reporta erros léxicos
2. **Análise Sintática**: Verifica se a sequência de tokens segue a gramática da linguagem MLP e reporta erros sintáticos
3. **Construção da AST**: Gera a Abstract Syntax Tree representando a estrutura do programa (apenas se não houver erros sintáticos)
4. **Análise Semântica**: Valida declarações, tipos, inicialização e uso de variáveis, construindo a Tabela de Símbolos e reportando erros semânticos
5. **Geração de Código Intermediário**: Gera código TAC (Three-Address Code) a partir da AST (apenas se não houver erros)

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
mvn exec:java -Dexec.args="programas/validos/teste_ok_1.mlp"
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
- **O que faz**: Executa o compilador MLP processando o arquivo especificado através de cinco fases sequenciais
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
  
  4. **Fase Semântica**:
     - Executa análise semântica validando declarações, tipos e uso de variáveis
     - Constrói a Tabela de Símbolos com todas as variáveis declaradas
     - Detecta erros semânticos (variáveis não declaradas, tipos incompatíveis, overflow, etc.)
     - **Saída**: Tabela de Símbolos e erros semânticos (se houver)
  
  5. **Fase de Geração de Código Intermediário (TAC)**:
     - Gera código intermediário em formato TAC (Three-Address Code) a partir da AST
     - Converte estruturas de alto nível (condicionais, loops) em sequências de instruções simples
     - Gera registradores temporários e labels automaticamente
     - **Saída**: Lista de instruções TAC representando o programa em código intermediário
     - Esta fase só é executada se não houver erros nas fases anteriores
  
  6. **Consolidação de Erros**:
     - Coleta todos os erros encontrados nas fases anteriores
     - Exibe um resumo consolidado com códigos de erro, localização (linha/coluna) e descrição
     - **Saída**: Lista de erros (se houver) ou mensagem de sucesso
- **Resultado**: O compilador executa todas as fases e exibe a saída formatada de cada uma, permitindo identificar problemas em qualquer etapa do processo de compilação

## 📝 Exemplo de Saída Esperada

Ao executar o comando `mvn exec:java -Dexec.args="programas/nome_teste.mlp"`, você verá a saída organizada em seções correspondentes às cinco fases de compilação:

### Estrutura da Saída

A saída é dividida em seis seções principais:

1. **== Léxico ==**: Lista de todos os tokens encontrados
2. **== Sintático ==**: Indicação da execução da análise sintática
3. **== AST ==**: Representação da Abstract Syntax Tree
4. **== Semântica ==**: Tabela de Símbolos e validações semânticas
5. **== Código Intermediário (TAC) ==**: Código intermediário gerado (apenas se não houver erros)
6. **Consolidação**: Resumo de erros ou mensagem de sucesso

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
Linha 4, Col 1 -> IDENT        'a'
Linha 4, Col 3 -> ASSIGN       '='
Linha 4, Col 5 -> NUM          '1'
Linha 4, Col 6 -> SEMI         ';'
Linha 5, Col 1 -> IDENT        'b'
Linha 5, Col 3 -> ASSIGN       '='
Linha 5, Col 5 -> NUM          '0'
Linha 5, Col 6 -> SEMI         ';'
Linha 7, Col 1 -> SE           'se'  [reservada]
Linha 7, Col 4 -> LPAREN       '('
Linha 7, Col 5 -> IDENT        'a'
Linha 7, Col 7 -> MAIOR        '>'
Linha 7, Col 9 -> NUM          '0'
Linha 7, Col 10 -> RPAREN       ')'
Linha 7, Col 12 -> ENTAO        'entao'  [reservada]
Linha 8, Col 5 -> IDENT        'b'
Linha 8, Col 7 -> ASSIGN       '='
Linha 8, Col 9 -> IDENT        'b'
Linha 8, Col 11 -> OP_SOMA      '+'
Linha 8, Col 13 -> NUM          '1'
Linha 8, Col 14 -> SEMI         ';'
Linha 10, Col 1 -> ENQUANTO     'enquanto'  [reservada]
Linha 10, Col 10 -> LPAREN       '('
Linha 10, Col 11 -> IDENT        'a'
Linha 10, Col 13 -> MAIOR        '>'
Linha 10, Col 15 -> NUM          '0'
Linha 10, Col 16 -> RPAREN       ')'
Linha 11, Col 5 -> IDENT        'a'
Linha 11, Col 7 -> ASSIGN       '='
Linha 11, Col 9 -> IDENT        'a'
Linha 11, Col 11 -> OP_SOMA      '+'
Linha 11, Col 13 -> NUM          '1'
Linha 11, Col 14 -> SEMI         ';'
Linha 13, Col 1 -> IDENT        'b'
Linha 13, Col 3 -> ASSIGN       '='
Linha 13, Col 5 -> IDENT        'b'
Linha 13, Col 7 -> OP_SOMA      '+'
Linha 13, Col 9 -> NUM          '1'
Linha 13, Col 10 -> SEMI         ';'
Linha 15, Col 1 -> DOLLAR       '$'
Linha 15, Col 2 -> DOT          '.'

== Sintático ==

== AST ==
Program(
  Decls:
    Decl(INTEIRO a, b)
  Commands:
    Assign(a = Num(1))
    Assign(b = Num(0))
    If(Cond(Var(a) > Num(0)), then=Assign(b = BinOp(Var(b) + Num(1))))
    While(Cond(Var(a) > Num(0)), body=Assign(a = BinOp(Var(a) + Num(1))))
    Assign(b = BinOp(Var(b) + Num(1)))
)

== Semântica ==
Tabela de Símbolos:
  - a : INTEIRO (linha 2, col 9)
  - b : INTEIRO (linha 2, col 12)

Sem erros léxicos/sintáticos/semânticos nesta fase.

== Código Intermediário (TAC) ==
LOADI R1, 1
STORE a, R1
LOADI R2, 0
STORE b, R2
LOAD R3, a
LOADI R4, 0
CMPGT R3, R4
JMPFALSE R3, L1
LOAD R5, b
LOADI R6, 1
ADD R7, R5, R6
STORE b, R7
JMP L2
LABEL L1
LABEL L2
LABEL L3
LOAD R8, a
LOADI R9, 0
CMPGT R8, R9
JMPFALSE R8, L4
LOAD R10, a
LOADI R11, 1
ADD R12, R10, R11
STORE a, R12
JMP L3
LABEL L4
LOAD R13, b
LOADI R14, 1
ADD R15, R13, R14
STORE b, R15
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

- **Fase Semântica (`== Semântica ==`)**:
  - Executa a análise semântica do programa
  - Valida declarações, tipos, inicialização e uso de variáveis
  - Constrói e exibe a Tabela de Símbolos com todas as variáveis declaradas
  - Detecta e reporta erros semânticos (variáveis não declaradas, tipos incompatíveis, etc.)
  - Esta fase só é executada se não houver erros sintáticos

- **Fase de Código Intermediário (`== Código Intermediário (TAC) ==`)**:
  - Gera código intermediário em formato TAC (Three-Address Code)
  - TAC é uma representação intermediária que facilita otimizações e geração de código final
  - Cada instrução TAC realiza no máximo uma operação e usa no máximo três endereços (destino e dois operandos)
  - **Registradores temporários**: O compilador gera automaticamente registradores temporários (R1, R2, R3, ...) para armazenar valores intermediários durante o cálculo de expressões
  - **Labels**: O compilador gera automaticamente labels (L1, L2, L3, ...) para controlar fluxo de controle em estruturas condicionais e loops
  - Esta fase só é executada se não houver erros léxicos, sintáticos ou semânticos

- **Consolidação de Erros**:
  - Se **não houver erros**: Exibe `"Sem erros léxicos/sintáticos/semânticos nesta fase."`
  - Se **houver erros**: Exibe uma seção `== Erros (consolidados) ==` com:
    - Código do erro (formato `COD.XXX`)
    - Tipo do erro (léxico, sintático ou semântico)
    - Localização (linha e coluna)
    - Descrição do problema
    - Símbolo que causou o erro


### Componentes Principais

- **`App.java`**: Orquestra todas as fases de compilação (léxica, sintática, AST, semântica e geração de código)
- **`lex/`**: Módulo de análise léxica que tokeniza o código fonte
- **`diagnostics/`**: Sistema de diagnóstico que coleta e reporta erros de todas as fases
- **`compiler/ast/`**: Nós da Abstract Syntax Tree
- **`compiler/parser/`**: Parser gerado automaticamente pelo ANTLR4 a partir da gramática
- **`compiler/semantics/`**: Analisador semântico que valida declarações, tipos e uso de variáveis
- **`compiler/codegen/`**: Gerador de código intermediário TAC (Three-Address Code)

## 🔍 Sistema de Diagnóstico de Erros

O compilador possui um sistema robusto de diagnóstico que identifica e reporta erros em todas as fases:

### Tipos de Erro

- **Léxico**: Símbolos não reconhecidos como tokens válidos
- **Sintático**: Estruturas que não seguem a gramática da linguagem
- **Semântico**: Erros de tipo, variáveis não declaradas, overflow numérico, divisão por zero, variáveis não inicializadas/utilizadas, auto-atribuição desnecessária, etc.

### Códigos de Erro

Cada erro possui um código numérico único organizado por categoria:

#### Erros Léxicos (001-099)
- **001**: Símbolo não identificado como token

#### Erros Sintáticos (100-199)
- **100**: Token esperado não encontrado
- **101**: Estrutura inválida na gramática

#### Erros Semânticos (200-299)

**Validação de Variáveis:**
- **200**: Variável não declarada
- **204**: Variável redeclarada
- **207**: Uso de variável não inicializada
- **208**: Variável declarada mas não utilizada
- **209**: Auto-atribuição desnecessária (ex: `x = x;`)

**Validação de Tipos e Operações:**
- **201**: Atribuição ou operação com tipos incompatíveis
- **205**: Divisão por zero (detectável em tempo de compilação)

**Validação de Identificadores:**
- **202**: Identificador excede 10 caracteres

**Validação de Estruturas:**
- **203**: Profundidade de comandos excede 10 níveis

**Validação Numérica:**
- **206**: Overflow numérico (literal fora do intervalo permitido)

Quando erros são encontrados, eles são exibidos no final da execução com informações detalhadas incluindo linha, coluna e descrição do problema.
