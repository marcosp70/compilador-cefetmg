package br.cefetmg.compilador;

import br.cefetmg.compilador.parser.Parser;

// A verificação léxica precede o parser; nenhum token ou símbolo é impresso na Etapa 2.
public class SyntacticAnalyzer {
    public void analyze(String source) {
        AnalysisResult lexical = new LexicalAnalyzer().analyze(source, false);
        if (!lexical.isSuccess()) {
            throw lexical.getErrors().get(0);
        }
        new Parser(lexical.getTokens()).parse();
    }
}
