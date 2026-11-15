"""Gera relatório Markdown e LaTeX com fontes e saídas reais, sem dependências externas."""
from pathlib import Path
import json
import re
import textwrap

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "relatorio-etapa2"

def read(path):
    return (ROOT / path).read_text(encoding="utf-8").replace("\r\n", "\n").rstrip()

def escape(text):
    chars = {"\\": r"\textbackslash{}", "&": r"\&", "%": r"\%", "$": r"\$",
             "#": r"\#", "_": r"\_", "{": r"\{", "}": r"\}", "~": r"\textasciitilde{}",
             "^": r"\textasciicircum{}"}
    return "".join(chars.get(c, c) for c in text)

def latex(markdown):
    output = []
    in_code = False
    for line in markdown.splitlines():
        if line.startswith("```"):
            output.append(r"{\scriptsize\begin{verbatim}" if not in_code else "\\end{verbatim}}")
            in_code = not in_code
        elif in_code:
            # Apenas quebra visual; os resultados originais permanecem em resultados/etapa2.
            output.extend(textwrap.wrap(line, width=100, subsequent_indent="    ",
                                        replace_whitespace=False, drop_whitespace=False) or [""])
        elif line.startswith("#"):
            marker, title = line.split(" ", 1)
            level = {1:"section",2:"subsection",3:"subsubsection"}.get(len(marker),"paragraph")
            output.append("\\" + level + "{" + escape(title) + "}")
        elif not line:
            output.append("")
        else:
            pieces = re.split(r"(`[^`]*`)", line.replace("**", ""))
            output.append("".join(r"\texttt{" + escape(x[1:-1]) + "}" if x.startswith("`") else escape(x) for x in pieces))
    if in_code:
        raise ValueError("Bloco de código não fechado")
    return "\n".join(output)

def main():
    scenarios = json.loads(read("testes/etapa2/cenarios.json"))
    sections = ["""# Introdução

Este relatório apresenta a Etapa 2: Analisador Sintático do trabalho de Compiladores do CEFET-MG, 2026/2. O ponto de partida foi o merge d2c296a da PR #1 do repositório marcosp70/compilador-cefetmg. Foram preservados o lexer, a tabela de símbolos, os delimitadores de comentário {* ... *} e os programas léxicamente corrigidos da versão remota.

A implementação utiliza Java 17, sem geradores de analisadores ou bibliotecas externas. O reconhecimento é feito por descida recursiva manual. Nesta etapa a saída contém somente sucesso ou diagnóstico de erro com linha e coluna, sem imprimir tokens e tabela de símbolos.

# Forma de uso

É necessário JDK 17 ou superior para compilar; o JAR pronto pode ser executado com Java 17 ou superior. Os scripts fornecidos utilizam PowerShell.

```text
java -jar dist/compilador-etapa2.jar testes/etapa2/adicionais/teste6a.lpp
.\\scripts\\build.ps1
.\\scripts\\test.ps1
.\\scripts\\generate-results.ps1
```

A CLI aceita um arquivo UTF-8 por execução. Código de saída 0 indica sucesso, 1 indica erro léxico ou sintático e 2 indica argumentos ou leitura inválidos. O analisador para no primeiro erro. A opção --all-errors da CLI anterior não faz parte da Etapa 2.

Para gerar novamente o relatório após atualizar resultados, execute python scripts/generate-report.py. O arquivo main.tex é autossuficiente e pode ser enviado ao Overleaf, usando pdfLaTeX.

# Abordagem e classes

Main: interpreta o argumento de entrada, lê UTF-8, imprime o resultado e define o código de saída.

SyntacticAnalyzer: executa a verificação léxica e encaminha ao Parser a lista terminada em EOF. Se houver erro léxico, o parser não é executado. A análise léxica completa precede a sintática; por isso um erro léxico posterior no arquivo pode ser relatado antes de um erro sintático anterior.

Parser: mantém uma posição no fluxo de tokens e implementa métodos para programa, declaração, lista de comandos, comando e expressões. As produções repetitivas utilizam laços; blocos aninhados e parênteses utilizam chamadas recursivas.

SyntaxException: representa um erro sintático com token encontrado, descrição do esperado, linha e coluna. Não há recuperação sintática.

Lexer, Token, TokenType, LexicalAnalyzer, AnalysisResult, Symbol e SymbolTable: componentes da Etapa 1 reutilizados sem alterações, responsáveis pelo reconhecimento léxico e pelos símbolos.

ParserSelfTest: verifica reconhecimento e rejeição de construções da gramática. LexerSelfTest preserva os testes de regressão léxicos.

A varredura léxica e o parser percorrem a entrada sem retrocesso. Para uma entrada de n caracteres e t tokens, o custo é linear, com memória para o fonte, a lista de tokens e a tabela de símbolos. A profundidade da pilha depende do aninhamento de blocos e parênteses; não há promessa de aceitar profundidade ilimitada.

# Modificações na gramática

""" + read("docs/gramatica-etapa2.md").replace("# Gramática adaptada para a Etapa 2", "").replace("## ", "### "),
"""# Metodologia e resultados dos testes

Cada passo00 dos Testes 1 a 5 reproduz a versão sem erros léxicos do merge da Etapa 1, normalizando somente os finais de linha. Cada execução subsequente apresenta uma correção indicada, o novo programa completo e a saída real da CLI. Os arquivos originais da Etapa 1 permanecem preservados.

A ausência de nome no cabeçalho do Teste 3 é inicialmente percebida quando a vírgula impede completar o cabeçalho: a palavra a tem forma de identificador e é consumida como possível nome do programa. A correção reconstrói esse cabeçalho. Algumas correções estruturais, como mover begin ou encerrar blocos ausentes no fim de arquivo, envolvem reposicionar ou adicionar mais de um delimitador relacionado.

As sete entradas finais (cinco testes fornecidos e dois adicionais) terminam com sucesso. As outras 26 execuções param no primeiro erro sintático. Nenhuma dessas 33 entradas contém erro léxico. Os números de linha referem-se a cada fonte mostrado, incluindo suas linhas em branco.

Não são aplicadas correções semânticas. O Teste 2 ainda utiliza nomes sem declaração; o Teste 3 mantém atribuição entre tipos distintos; o Teste 5 preserva read(outro) com condição sobre opc. Esses problemas pertencem à Etapa 3 e não impedem sucesso sintático.

Nas saídas longas, o LaTeX utiliza apenas quebra visual de linha; o conteúdo original está nos arquivos .txt de resultados.
"""]
    current = None
    for item in scenarios:
        if item["test"] != current:
            current = item["test"]
            sections.append("## " + current)
        sections.append("### Passo " + str(item["step"]) + (" - sucesso" if item["success"] else " - erro"))
        sections.append(item["change"])
        sections.append("Arquivo: `" + item["source"] + "`.")
        sections.append("```text\n" + read(item["source"]) + "\n```")
        name = item["source"].removeprefix("testes/etapa2/").replace("/", "_") + ".txt"
        result = read("resultados/etapa2/" + name)
        if ("SUCESSO" in result) != item["success"]:
            raise ValueError("Saída desatualizada: " + name)
        sections.append("Saída do compilador:")
        sections.append("```text\n" + result + "\n```")
    sections.append("""# Verificação

Foram aprovados os 11 testes léxicos herdados e 60 testes sintáticos automatizados. As 33 execuções documentais foram verificadas quanto ao código de saída, ausência de erro léxico e ausência de listagem de tokens ou símbolos.

A suíte cobre comandos e blocos aninhados, declarações opcionais, constantes, operadores, delimitadores, listas vazias, separador excedente ou ausente, múltiplas relações no mesmo nível, prefixos unários encadeados e tokens após o ponto final. Também verifica a posição de erro e a propagação de erro léxico.

# Conclusão

A Etapa 2 reconhece a estrutura da linguagem por implementação manual e informa o primeiro erro com linha e coluna. A gramática foi adaptada sem alterar o conjunto de programas válidos. O fluxo de tokens e a tabela de símbolos permanecem disponíveis para as verificações semânticas e a geração de código da próxima etapa.

# Referências

CEFET-MG. Enunciado do trabalho Implementação de um Compilador. Disciplina de Compiladores, Engenharia de Computação, professora Kecia Aline Marques Ferreira, 2026/2.

Repositório marcosp70/compilador-cefetmg. Versão d2c296a: merge da PR #1 (corrige-comentarios-e-testes). Base da implementação da Etapa 2.
""")
    markdown = "\n\n".join(sections)
    OUT.mkdir(exist_ok=True)
    (OUT / "Relatorio_Etapa2.md").write_text(markdown + "\n", encoding="utf-8")
    preamble = r"""\documentclass[12pt,a4paper]{article}
\usepackage[utf8]{inputenc}
\usepackage[T1]{fontenc}
\usepackage[brazilian]{babel}
\usepackage{geometry}
\usepackage{graphicx}
\geometry{margin=2.5cm}
\setlength{\parindent}{0pt}
\setlength{\parskip}{0.4em}
\begin{document}
\begin{titlepage}
\begin{center}
\IfFileExists{logoCefet.png}{\includegraphics[width=0.18\linewidth]{logoCefet.png}}{}
\vspace*{1cm}

\textbf{\Large CENTRO FEDERAL DE EDUCAÇÃO TECNOLÓGICA DE MINAS GERAIS}

\vspace{0.5cm}
Campus II -- DECOM\\
Curso de Engenharia da Computação

\vspace{2cm}
\textbf{\Large Compiladores}

\vspace{0.5cm}
Professora: Kecia Aline Marques Ferreira

\vspace{2cm}
\textbf{\Large IMPLEMENTAÇÃO DE UM COMPILADOR}\\[0.4cm]
\textbf{\large Etapa 2 -- Analisador Sintático}

\vspace{2cm}
\textbf{Marcos Paulo Santos da Silva}\\[0.2cm]
\textbf{Arthur Bracarense}\\[0.2cm]
\textbf{Caio Rangel}

\vfill
Belo Horizonte\\
2026
\end{center}
\end{titlepage}
\tableofcontents
\newpage
"""
    (OUT / "main.tex").write_text(preamble + latex(markdown) + "\n\\end{document}\n", encoding="utf-8")
    print("Relatório Markdown e LaTeX gerados:", OUT)

if __name__ == "__main__":
    main()
