# Análise da base da Etapa 2

Repositório: https://github.com/marcosp70/compilador-cefetmg
Base: d2c296a (merge da PR #1, corrige-comentarios-e-testes).
Branch local: etapa2-analisador-sintatico.

## Escopo encontrado

- Lexer manual, tabela de símbolos e CLI exclusivos da Etapa 1.
- Palavras-chave case-sensitive; quinze reservadas; identificadores ASCII.
- Comentários delimitados por {* e *}; ":" não é token da linguagem.
- JAR Java 17 e scripts PowerShell sem dependências Java externas.
- Onze testes de regressão léxicos.
- Cinco fontes originais, suas correções léxicas e dois testes adicionais.
- CLI anterior imprimia tokens/tabela e oferecia --all-errors apenas para erros léxicos.

## Alterações do merge mantidas

d99b11a corrigiu os delimitadores de comentário e os testes 1 e 5.
8bf364f simplificou a implementação para recursos básicos de Java.
cbbf4d8 alinhou os programas fornecidos ao enunciado.
Nenhuma classe do lexer ou da tabela de símbolos foi alterada para a Etapa 2.

## Escopo implementado

Parser manual por descida recursiva; diagnóstico com esperado/encontrado, linha e coluna;
CLI da Etapa 2 silenciosa quanto a tokens/tabela; JAR separado;
gramática adaptada documentada; testes negativos/positivos e correções incrementais.
Os passo00 dos testes da Etapa 2 reproduzem os corrigidos da base, normalizando apenas fins de linha.

Os casos semanticamente incorretos são mantidos se sintaticamente válidos.
Exemplos: uso de outro/val sem declaração, atribuição float para int, e read(outro)
quando a condição usa opc. Corrigir isso nesta etapa mascararia os testes semânticos futuros.
