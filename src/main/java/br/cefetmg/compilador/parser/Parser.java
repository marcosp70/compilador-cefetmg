package br.cefetmg.compilador.parser;

import br.cefetmg.compilador.lexer.Token;
import br.cefetmg.compilador.lexer.TokenType;
import java.util.List;
import static br.cefetmg.compilador.lexer.TokenType.*;

// Descida recursiva manual. A lista recebida deve terminar em EOF e não conter erros léxicos.
public class Parser {
    private final List<Token> tokens;
    private int position;

    public Parser(List<Token> tokens) {
        if (tokens == null || tokens.isEmpty() || tokens.get(tokens.size() - 1).getType() != EOF) {
            throw new IllegalArgumentException("A entrada do parser deve terminar em EOF.");
        }
        this.tokens = tokens;
    }

    public void parse() {
        expect(PROGRAM, "'program'");
        expect(IDENTIFIER, "identificador do programa");
        expect(BEGIN, "'begin' antes das declarações e comandos");
        while (startsDeclaration()) {
            declaration();
            expect(SEMICOLON, "';' após declaração");
        }
        statementList();
        expect(END, "'end' do programa");
        expect(DOT, "'.' após 'end'");
        expect(EOF, "fim de arquivo após '.'");
    }

    // identifier '=' é comum a declaração e atribuição. O tipo é visto no terceiro token.
    // A vírgula já identifica uma lista de declaração; não existe vírgula em atribuições.
    private boolean startsDeclaration() {
        return at(IDENTIFIER) && (look(1) == COMMA
                || (look(1) == ASSIGN && isType(look(2))));
    }

    private void declaration() {
        expect(IDENTIFIER, "identificador na declaração");
        while (accept(COMMA)) {
            expect(IDENTIFIER, "identificador após ','");
        }
        expect(ASSIGN, "'=' na declaração");
        if (!isType(look(0))) {
            throw error("tipo 'int', 'float' ou 'char'");
        }
        advance();
    }

    // Lista não vazia; ';' separa comandos e nunca representa comando vazio.
    private void statementList() {
        statement();
        while (accept(SEMICOLON)) {
            statement();
        }
    }

    private void statement() {
        if (accept(IDENTIFIER)) {
            expect(ASSIGN, "'=' na atribuição");
            simpleExpression();
        } else if (accept(IF)) {
            expression();
            expect(THEN, "'then'");
            statementList();
            if (accept(ELSE)) {
                statementList();
            }
            expect(END, "'end' do if");
        } else if (accept(WHILE)) {
            expression();
            expect(DO, "'do'");
            statementList();
            expect(END, "'end' do while");
        } else if (accept(REPEAT)) {
            statementList();
            expect(UNTIL, "'until'");
            expression();
        } else if (accept(READ)) {
            expect(LEFT_PAREN, "'(' após 'read'");
            expect(IDENTIFIER, "identificador em read");
            expect(RIGHT_PAREN, "')' após identificador");
        } else if (accept(WRITE)) {
            expect(LEFT_PAREN, "'(' após 'write'");
            if (!accept(LITERAL)) {
                simpleExpression();
            }
            expect(RIGHT_PAREN, "')' após argumento de write");
        } else {
            throw error("comando (atribuição, if, while, repeat, read ou write)");
        }
    }

    private void expression() {
        simpleExpression();
        if (isRelational(look(0))) {
            advance();
            simpleExpression();
        }
    }

    private void simpleExpression() {
        term();
        while (at(PLUS) || at(MINUS) || at(OR)) {
            advance();
            term();
        }
    }

    private void term() {
        factorA();
        while (at(MULTIPLY) || at(DIVIDE) || at(MODULO) || at(AND)) {
            advance();
            factorA();
        }
    }

    private void factorA() {
        if (at(NOT) || at(MINUS)) {
            advance();
        }
        factor();
    }

    private void factor() {
        if (accept(IDENTIFIER) || accept(INTEGER_CONST) || accept(FLOAT_CONST) || accept(CHAR_CONST)) {
            return;
        }
        if (accept(LEFT_PAREN)) {
            expression();
            expect(RIGHT_PAREN, "')' na expressão");
            return;
        }
        throw error("identificador, constante ou '('");
    }

    private boolean isType(TokenType type) {
        return type == INT || type == FLOAT || type == CHAR;
    }

    private boolean isRelational(TokenType type) {
        return type == EQUAL || type == GREATER || type == GREATER_EQUAL
                || type == LESS || type == LESS_EQUAL || type == NOT_EQUAL;
    }

    private TokenType look(int offset) {
        return tokens.get(Math.min(position + offset, tokens.size() - 1)).getType();
    }

    private boolean at(TokenType type) { return look(0) == type; }
    private void advance() { if (position < tokens.size() - 1) { position++; } }
    private boolean accept(TokenType type) {
        if (!at(type)) { return false; }
        advance();
        return true;
    }
    private void expect(TokenType type, String expected) {
        if (!accept(type)) { throw error(expected); }
    }
    private SyntaxException error(String expected) {
        return new SyntaxException(expected, tokens.get(position));
    }
}
