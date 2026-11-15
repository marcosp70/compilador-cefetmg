package br.cefetmg.compilador;

import br.cefetmg.compilador.lexer.LexicalException;
import br.cefetmg.compilador.parser.SyntaxException;

// Testes de aceitação/rejeição da linguagem, incluindo suas fronteiras.
public class ParserSelfTest {
    private static int executed;

    public static void main(String[] args) {
        String[] valid = {
            "program p begin a=1 end.",
            "program p begin a=int; a=1 end.",
            "program p begin a,b=int; c=float; d=char; read(a); write(a); write(\"\") end.",
            "program p begin if a then write(a) end end.",
            "program p begin if a then a=1 else a=2 end end.",
            "program p begin while a do a=1 end end.",
            "program p begin repeat a=1 until a end.",
            "program p begin a=-1+2*3/4%5||6&&7; a=!b end.",
            "program p begin a=(b<c); write((b==c)) end.",
            "program p begin a=1; if a then repeat while a do read(a) end until a else write(a) end end.",
            "program p begin a=1; b=2.5; c='x' end.",
            "program p begin {* ignorado *} write(\"ok\") end.",
            "program p begin a=int; a=1; b=2 end.",
            "program p begin if a<b then a=1 end; if a>=b then a=2 end end.",
            "program p begin if a<=b then a=1 else if a!=b then a=2 end end end.",
            "program p begin if (a>b)&&(b<c) then a=1 end end.",
            "program p begin a=-(!b) end.",
            "program p begin repeat read(opc) until ((opc == 'N') || (opc == 'n')) end."
        };
        for (String source : valid) { succeeds(source); }
        String[] invalid = {
            "", "program p begin end.", "program begin a=1 end.",
            "program p a=int; begin a=1 end.",
            "program p begin a=int; end.", "program p begin a=int a=1 end.",
            "program p begin a=1; end.", "program p begin ;a=1 end.",
            "program p begin a=1;;a=2 end.", "program p begin a=1 b=2 end.",
            "program p begin a=1 end", "program p begin a=1 end. a=2",
            "program p begin a=1 end..", "program p begin a=1; b=int; b=2 end.",
            "program p begin a,b=inteiro; a=1 end.",
            "program p begin a,=int; a=1 end.", "program p begin a=+1 end.",
            "program p begin a=--1 end.", "program p begin a=!!b end.",
            "program p begin a=1<2 end.", "program p begin write(a<b) end.",
            "program p begin a=\"texto\" end.", "program p begin a=() end.",
            "program p begin a=(1+2 end.", "program p begin a=1+ end.",
            "program p begin if a<b<c then a=1 end end.",
            "program p begin if a a=1 end end.",
            "program p begin if a then end end.",
            "program p begin if a then a=1; else a=2 end end.",
            "program p begin if a then a=1 else end end.",
            "program p begin while a a=1 end end.",
            "program p begin while a do end end.",
            "program p begin repeat until a end.",
            "program p begin repeat a=1; until a end.",
            "program p begin read(1) end.", "program p begin read(a,b) end.",
            "program p begin write() end.", "program p begin write \"ok\" end.",
            "program p begin Read(a) end.",
            "program p begin repeat read(opc) until (opc == 'N' || opc == 'n') end."
        };
        for (String source : invalid) { fails(source); }
        try {
            new SyntacticAnalyzer().analyze("program p\nbegin\na=1;\nend.");
            throw new AssertionError("Era esperado erro na linha 4.");
        } catch (SyntaxException e) {
            check(e.getLine() == 4 && e.getColumn() == 1, "posição do erro");
            executed++;
        }
        try {
            new SyntacticAnalyzer().analyze("program p begin a : int end.");
            throw new AssertionError("Era esperado erro léxico.");
        } catch (LexicalException e) { executed++; }
        System.out.println("OK - " + executed + " testes sintáticos passaram.");
    }

    private static void succeeds(String source) {
        new SyntacticAnalyzer().analyze(source);
        executed++;
    }

    private static void fails(String source) {
        try {
            new SyntacticAnalyzer().analyze(source);
        } catch (SyntaxException e) {
            executed++;
            return;
        }
        throw new AssertionError("Fonte inválido foi aceito: " + source);
    }

    private static void check(boolean value, String description) {
        if (!value) { throw new AssertionError(description); }
    }
}
