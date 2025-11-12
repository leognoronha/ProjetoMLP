# Projeto MLP

Compilador para a linguagem MLP desenvolvido em Java usando ANTLR4. Este projeto realiza a análise léxica, sintática e construção de AST (Abstract Syntax Tree) para programas escritos na linguagem MLP.

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
  3. O programa lê o arquivo MLP, faz análise léxica e sintática, constrói a AST e imprime o resultado
- **Parâmetros**:
  - `exec:java`: Plugin do Maven que executa uma classe Java
  - `-Dexec.args="..."`: Define os argumentos que serão passados para o método `main()`
- **Resultado**: Executa o compilador e mostra a AST construída

## 📝 Exemplo de Saída Esperada

Ao executar o comando com o arquivo `programas/teste1.mlp`, você deve ver a seguinte saída:

```
AST construída:

Program(
  Decls:
    Decl(INTEIRO a, b)
  Commands:
    If(Cond(Var(a) > Num(10)), then=Assign(b = Num(1)))
)
```

A saída mostra:
- A estrutura da AST (Abstract Syntax Tree) construída a partir do código MLP
- Informações de build do Maven indicando sucesso na execução

## 📁 Estrutura do Projeto

```
comp-projeto/
├── src/
│   ├── main/
│   │   ├── antlr4/          # Gramática ANTLR4 (Mlp.g4)
│   │   └── java/            # Código fonte Java
│   └── test/                # Testes
├── programas/               # Arquivos .mlp para compilar
├── target/                 # Arquivos gerados (não versionado)
├── pom.xml                 # Configuração Maven
└── README.md               # Este arquivo
```

