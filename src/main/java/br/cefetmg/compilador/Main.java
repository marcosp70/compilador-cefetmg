package br.cefetmg.compilador;

import br.cefetmg.compilador.lexer.LexicalException;
import br.cefetmg.compilador.parser.SyntaxException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
        if (args.length == 1 && (args[0].equals("--help") || args[0].equals("-h"))) {
            usage();
            return;
        }
        if (args.length != 1 || args[0].startsWith("-")) {
            usage();
            System.exit(2);
            return;
        }
        try {
            String source = Files.readString(Paths.get(args[0]), StandardCharsets.UTF_8);
            new SyntacticAnalyzer().analyze(source);
            System.out.println("Resultado: SUCESSO - análise léxica e sintática concluída sem erros.");
        } catch (LexicalException e) {
            System.out.println(e.format());
            System.out.println("Resultado: FALHA - erro léxico.");
            System.exit(1);
        } catch (SyntaxException e) {
            System.out.println(e.format());
            System.out.println("Resultado: FALHA - erro sintático.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Não foi possível ler o arquivo: " + args[0]);
            System.exit(2);
        }
    }

    private static void usage() {
        System.out.println("Uso: java -jar dist/compilador-etapa2.jar <arquivo-fonte>");
        System.out.println("Etapa 2: encerra no primeiro erro; não exibe tokens ou tabela de símbolos.");
    }
}
