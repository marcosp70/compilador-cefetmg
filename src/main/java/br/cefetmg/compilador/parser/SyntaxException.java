package br.cefetmg.compilador.parser;

import br.cefetmg.compilador.lexer.Token;

// Transporta o token que impediu a continuação da análise.
public class SyntaxException extends RuntimeException {
    private final Token token;

    public SyntaxException(String expected, Token token) {
        super("esperado " + expected + "; encontrado "
                + (token.getLexeme().isEmpty() ? "fim de arquivo" : "'" + token.getLexeme() + "'"));
        this.token = token;
    }

    public int getLine() { return token.getLine(); }
    public int getColumn() { return token.getColumn(); }

    public String format() {
        return "Erro sintático na linha " + getLine() + ", coluna " + getColumn() + ": " + getMessage();
    }
}
