# Compilador — Etapa 2: Analisador Sintático

Trabalho de Compiladores do CEFET-MG, Engenharia de Computação, 2026/2.
Professora: Kecia Aline Marques Ferreira.

Integrantes: Marcos Paulo Santos da Silva, Arthur Bracarense e Caio Rangel.

Implementação manual em Java 17, sem geradores de analisadores e sem dependências Java externas.
Esta etapa parte do merge `d2c296a` da PR #1, preservando as correções da análise léxica.

## Executar

Requer Java 17 ou superior. O JAR desta etapa está em `dist/compilador-etapa2.jar`.

```powershell
java -jar .\dist\compilador-etapa2.jar .\testes\etapa2\adicionais\teste6a.lpp
.\scripts\run.ps1 .\testes\etapa2\teste5\passo06.lpp
```

No Prompt de Comando:

```bat
run.bat testes\etapa2\adicionais\teste6a.lpp
```

Saída de sucesso:

```text
Resultado: SUCESSO - análise léxica e sintática concluída sem erros.
```

Se houver erro, são informados o esperado, o encontrado, a linha e a coluna.
O processo encerra no primeiro erro. Nesta etapa não são impressos tokens nem tabela de símbolos.
A análise léxica completa precede a sintática. A opção `--all-errors` pertence somente à CLI da Etapa 1.

Códigos de saída: **0** sucesso; **1** erro léxico/sintático; **2** erro de argumentos ou leitura.

## Compilar e verificar

Requer JDK 17 ou superior no PATH e PowerShell.

```powershell
.\scripts\build.ps1
.\scripts\test.ps1
.\scripts\generate-results.ps1
```

`test.ps1` executa os **11 testes léxicos herdados e 60 testes sintáticos**.
`generate-results.ps1` executa e confere **33 cenários**, validando sucesso/erro,
ausência de erro léxico e ausência de listagem de tokens/símbolos.
As saídas locais são escritas em `resultados/etapa2/`.

## Gramática e comportamento

O parser usa descida recursiva e consulta até três tokens para distinguir declaração de atribuição.
A recursão à esquerda das expressões foi eliminada por repetições.
A gramática adaptada e a justificativa das transformações estão em
[docs/gramatica-etapa2.md](docs/gramatica-etapa2.md).
A análise da versão de partida está em [docs/analise-base.md](docs/analise-base.md).

Regras que merecem atenção:

- `begin` aparece **antes** das declarações.
- Declarações usam `=`, por exemplo `a, b = int;`.
- `;` separa comandos: não há comando vazio nem separador final antes de `end`, `else` ou `until`.
- Comentários são delimitados por `{*` e `*}`.
- Palavras reservadas são sensíveis a maiúsculas/minúsculas.
- A precedência é a do enunciado: `&&` no grupo multiplicativo e `||` no aditivo.
- Comparações combinadas devem ser agrupadas, como `(opc == 'N') || (opc == 'n')`.
- Somente um prefixo unário por fator: `!!a` é inválido; `!(!a)` é válido.
- Tokens após `end.` são rejeitados.

Declaração prévia, tipos e geração de código pertencem à Etapa 3.
Consequentemente, sucesso sintático não significa que um programa seja semanticamente válido.

## Testes do trabalho

Os arquivos da Etapa 1 foram preservados. Para a Etapa 2:

- `testes/etapa2/teste1/` a `teste5/`: `passo00.lpp` é o fonte sem erro léxico do merge; cada passo seguinte corrige o erro documentado.
- Versões finais: Teste 1 `passo06`, Teste 2 `passo02`, Teste 3 `passo05`, Teste 4 `passo07`, Teste 5 `passo06`.
- `testes/etapa2/adicionais/`: dois testes extras com blocos, comandos e expressões aninhados.
- `testes/etapa2/cenarios.json`: ordem, explicação de cada correção e resultado esperado.

## Estrutura

```text
src/main/java/       lexer, símbolos, parser e CLI
src/test/java/       testes sem dependências externas
testes/etapa2/       fontes e sequência de correções da Etapa 2
testes/originais/    fontes originais da Etapa 1
testes/corrigidos/   versões sem erro léxico da Etapa 1
docs/               gramática adaptada e análise da base
scripts/            build, execução, testes e geração de relatório
dist/               JAR da Etapa 2 e JAR histórico da Etapa 1
```

## Relatório

O pacote de entrega inclui `relatorio-etapa2/main.tex` e `Relatorio_Etapa2.md`.
O LaTeX é autossuficiente, contém cada fonte e sua saída real e pode ser importado no Overleaf
com compilador pdfLaTeX. A imagem `logoCefet.png` é opcional.
Relatórios e resultados locais não são versionados.

Para reconstruí-los, execute a geração de resultados e depois (Python 3.9 ou superior):

```powershell
python .\scripts\generate-report.py
```
