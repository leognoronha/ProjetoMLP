grammar Mlp;

// ---------------- PARSER RULES ----------------

programa
  : DOLLAR tipo* comando* DOLLAR DOT EOF
  ;

// Cada comando termina com ';'
comando
  : comandoSimples SEMI
  ;

// Comando sem o ';' (para ser usado dentro de se/enquanto)
comandoSimples
  : condicional
  | iterativo
  | atribuicao
  ;

// ----------- Declarações -----------

tipo
  : INTEIRO listaIdent SEMI
  | REAL    listaIdent SEMI
  | CARACTER listaIdent SEMI
  ;

listaIdent
  : IDENT (COMMA IDENT)*
  ;

// ----------- Comandos -----------

// Ex: se (a > 10) entao b = b + 1;
condicional
  : SE condicao ENTAO comandoSimples (SENAO comandoSimples)?
  ;

// Ex: enquanto (a < b) comando;
iterativo
  : ENQUANTO condicao comandoSimples
  ;

// Ex: a = b + 1;
atribuicao
  : IDENT ASSIGN (expressao | IDENT) (operador (expressao | IDENT))*
  ;

// ----------- Condições e Expressões -----------

condicao
  : LPAREN compSimples RPAREN ( (E | OR) LPAREN compSimples RPAREN )*
  | LPAREN IDENT NOT LPAREN compSimples ( (E | OR) compSimples )* RPAREN RPAREN
  ;

compSimples
  : IDENT logico (IDENT | NUM)
  ;

logico
  : MAIOR
  | MENOR
  | IGUALD
  | MENORIGUAL
  | MAIORIGUAL
  | DIFERENTE
  ;

expressao
  : numero
  | IDENT
  | LPAREN expressao operador expressao RPAREN
  ;

operador
  : OP_SOMA
  | OP_MULT
  | OP_DIV
  | RESTO
  ;

numero
  : NUM
  ;

// ---------------- LEXER RULES ----------------

// Palavras reservadas
INTEIRO   : 'inteiro';
REAL      : 'real';
CARACTER  : 'caracter';
SE        : 'se';
ENTAO     : 'entao';
SENAO     : 'senao';
ENQUANTO  : 'enquanto';
E         : 'E';
OR        : 'OR';
NOT       : 'NOT';
RESTO     : 'RESTO';

// Símbolos especiais
DOLLAR    : '$';
DOT       : '.';
COMMA     : ',';
SEMI      : ';';
LPAREN    : '(';
RPAREN    : ')';

// Operadores
ASSIGN      : '=';
IGUALD      : '==';
DIFERENTE   : '!=';
MAIORIGUAL  : '>=';
MENORIGUAL  : '<=';
MAIOR       : '>';
MENOR       : '<';

OP_SOMA   : '+';
OP_MULT   : '*';
OP_DIV    : '/';

// Identificador (vamos validar o máximo de 10 chars depois, na semântica)
fragment LETTER : [A-Za-z];
fragment DIGIT  : [0-9];

IDENT
  : LETTER (LETTER | DIGIT)*
  ;

// Número
NUM
  : DIGIT+ (DOT DIGIT+)?     // digito+ ou digito+.digito+
  | DOT DIGIT+               // .digito+
  ;

// Espaços em branco ignorados
WS
  : [ \t\r\n]+ -> skip
  ;

// Qualquer outro símbolo vira erro léxico (vamos tratar depois, no item 7)
ERROR_CHAR
  : .
  ;
