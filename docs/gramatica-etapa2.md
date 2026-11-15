# Gramática adaptada para a Etapa 2

A notação EBNF usa `[...]` para opcional e `{...}` para repetição (zero ou mais).
Os demais delimitadores representam terminais. `EOF` exige consumir toda a entrada.

```text
program        ::= "program" identifier "begin" [decl-list] stmt-list "end" "." EOF
decl-list      ::= decl ";" {decl ";"}
decl           ::= ident-list "=" type
ident-list     ::= identifier {"," identifier}
type           ::= "int" | "float" | "char"
stmt-list      ::= stmt {";" stmt}
stmt           ::= assign-stmt | if-stmt | while-stmt | repeat-stmt
                 | read-stmt | write-stmt
assign-stmt    ::= identifier "=" simple-expr
if-stmt        ::= "if" condition "then" stmt-list ["else" stmt-list] "end"
while-stmt     ::= "while" condition "do" stmt-list "end"
repeat-stmt    ::= "repeat" stmt-list "until" condition
read-stmt      ::= "read" "(" identifier ")"
write-stmt     ::= "write" "(" writable ")"
writable       ::= simple-expr | literal
condition      ::= expression
expression     ::= simple-expr [relop simple-expr]
simple-expr    ::= term {addop term}
term           ::= factor-a {mulop factor-a}
factor-a       ::= ["!" | "-"] factor
factor         ::= identifier | constant | "(" expression ")"
constant       ::= integer_const | float_const | char_const
relop          ::= "==" | ">" | ">=" | "<" | "<=" | "!="
addop          ::= "+" | "-" | "||"
mulop          ::= "*" | "/" | "%" | "&&"
```

## Transformações e equivalência

1. A recursão à esquerda de simple-expr e term foi substituída por repetições.
   Exemplo: simple-expr -> term simple-expr-tail;
   simple-expr-tail -> addop term simple-expr-tail | vazio.
   O mesmo vale para term-tail -> mulop factor-a term-tail | vazio.
   O conjunto de sequências aceitas é preservado. Os laços percorrem os operadores da esquerda para a direita.
2. As alternativas com prefixos comuns de if-stmt e expression foram fatoradas.
3. stmt-prefix/stmt-suffix foram incorporados a while-stmt/repeat-stmt, sem alterar terminais.
4. Foram uniformizados simple_expr/simple-expr e fator-a/factor-a.
5. EOF é uma condição de aceitação, para rejeitar qualquer token após o ponto.
6. Não foram adicionados comandos vazios, listas vazias, ponto e vírgula opcional final ou operadores novos.

## Decisões do reconhecimento

Declarações e atribuições compartilham identifier "=". Portanto não se afirma que
essa gramática inteira seja LL(1). No início do corpo, o parser consulta até três tokens:
identifier seguido por vírgula inicia declaração; identifier "=" seguido de int/float/char
também inicia declaração. Esses tipos reservados não podem iniciar uma expressão.
Quando começa a lista de comandos, não se aceitam declarações posteriores.

FIRST(stmt) = {identifier, if, while, repeat, read, write}.
Os delimitadores de retorno de stmt-list são end, else ou until, de acordo com o chamador.
As listas são não vazias. Depois de consumir ";", outro stmt é obrigatório.

A precedência original é mantida: multiplicativos e &&; depois aditivos e ||;
por último o relacional opcional. Cada factor-a admite apenas um prefixo ! ou -.
"!!a" é inválido, mas "!(!a)" é válido. "a < b < c" é inválido.
"opc == 'N' || opc == 'n'" exige agrupar as comparações:
"(opc == 'N') || (opc == 'n')". Os parênteses permitem expression dentro de factor.

## Limite da etapa

O parser verifica estrutura, sem verificar declaração prévia, redeclaração, tipos ou valores.
Não constrói AST nem gera código nesta etapa. As regras semânticas ficam para a Etapa 3.
O lexer do merge é preservado, inclusive comentários delimitados por "{*" e "*}".
