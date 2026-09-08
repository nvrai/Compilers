/**
 * COSC 4400 - Project #1
 * Scans MiniJava source into tokens and reports lexical errors.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public final class Scanner {
    private enum Kind {
    	AND, OR, BWAND, BWOR, XOR, COMP,
    	PLUS, MINUS, STAR, FORWARDSLASH, LESSTHAN, GREATERTHAN,
    	EQUAL, NOTEQUAL, BANG, ASSIGN,
    	LPAREN, RPAREN, LSQUARE, RSQUARE, LBRACE, RBRACE,
    	COMMA, PERIOD, SEMICOLON,
    	CLASS, PUBLIC, STATIC, VOID, MAIN, STRING, EXTENDS,
    	RETURN, INT, BOOLEAN, IF, ELSE, WHILE, LENGTH,
    	TRUE, FALSE, THIS, NEW, SYSTEM_OUT_PRINTLN,
    	PRINT, PRINTLN, PRINTINT, READINT,
    	ID, INTEGER_LITERAL, OCTAL_LITERAL, HEXADECIMAL_LITERAL,
    	STRING_LITERAL, EOF
    }


    private static final Map<String, Kind> KEYWORDS = new HashMap<String, Kind>();
    private static final Map<String, Kind> SYMBOLS = new HashMap<String, Kind>();
    private static final String[] DOTTED_WORDS = {
        "System.out.println", "Xinu.println", "Xinu.printint",
        "Xinu.readint", "Xinu.print"
    };
    private static final Kind[] DOTTED_KINDS = {
    Kind.SYSTEM_OUT_PRINTLN, Kind.PRINTLN, Kind.PRINTINT,
    Kind.READINT, Kind.PRINT
    };
    static {
        String[] words = {
            "class", "public", "static", "void", "main", "String", "extends",
            "return", "int", "boolean", "if", "else", "while", "length",
            "true", "false", "this", "new"
        };
        Kind[] kinds = {
            Kind.CLASS, Kind.PUBLIC, Kind.STATIC, Kind.VOID, Kind.MAIN,
            Kind.STRING, Kind.EXTENDS, Kind.RETURN, Kind.INT, Kind.BOOLEAN,
            Kind.IF, Kind.ELSE, Kind.WHILE, Kind.LENGTH, Kind.TRUE,
            Kind.FALSE, Kind.THIS, Kind.NEW
        };
        for (int i = 0; i < words.length; i++) {
            KEYWORDS.put(words[i], kinds[i]);
        }
        String[] symbols = {
            "&&", "||", "&", "|", "^", "~", "+", "-", "*", "/", "<", ">",
            "==", "!=", "!", "=", "(", ")", "[", "]", "{", "}", ",", ".", ";"
        };
	Kind[] symbolKinds = {
    		Kind.AND, Kind.OR, Kind.BWAND, Kind.BWOR, Kind.XOR,
    		Kind.COMP, Kind.PLUS, Kind.MINUS, Kind.STAR, Kind.FORWARDSLASH,
    		Kind.LESSTHAN, Kind.GREATERTHAN, Kind.EQUAL, Kind.NOTEQUAL,
    		Kind.BANG, Kind.ASSIGN, Kind.LPAREN, Kind.RPAREN, Kind.LSQUARE,
    		Kind.RSQUARE, Kind.LBRACE, Kind.RBRACE, Kind.COMMA, Kind.PERIOD,
    		Kind.SEMICOLON
	};
        for (int i = 0; i < symbols.length; i++) {
            SYMBOLS.put(symbols[i], symbolKinds[i]);
        }
    }

    private static final class Token {
        final Kind kind;
        final String text;

        Token(Kind kind, String text) {
            this.kind = kind;
            this.text = text;
        }
    }

    private static final class LexicalException extends Exception {
        private static final long serialVersionUID = 1L;

        LexicalException(String message) {
            super(message);
        }
    }

    private final String source;
    private int position;

    private Scanner(String source) {
        this.source = source;
    }

    private int peek(int offset) {
        int index = position + offset;
        return index < source.length() ? source.charAt(index) : -1;
    }

    private static boolean isLetter(int c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private static boolean isDigit(int c) {
        return c >= '0' && c <= '9';
    }

    private static boolean isIdentifierPart(int c) {
        return isLetter(c) || isDigit(c) || c == '_';
    }

    private static boolean isWhitespace(int c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\r'
            || c == '\f' || c == '\u000b';
    }

    private void skipTrivia() throws LexicalException {
        while (true) {
            if (isWhitespace(peek(0))) {
                position++;
            } else if (peek(0) == '/' && peek(1) == '/') {
                position += 2;
                while (peek(0) != -1 && peek(0) != '\n' && peek(0) != '\r') {
                    position++;
                }
            } else if (peek(0) == '/' && peek(1) == '*') {
                position += 2;

                while (!(peek(0) == '*' && peek(1) == '/')) {
                    if (peek(0) == -1) {
                        throw new LexicalException("Comment not terminated at end of input.");
                    }
                    position++;
                }
                position += 2;
            } else {
                return;
            }
        }
    }

    private Token scanWord() {
        for (int i = 0; i < DOTTED_WORDS.length; i++) {
            String word = DOTTED_WORDS[i];
            if (source.startsWith(word, position)
                    && !isIdentifierPart(peek(word.length()))) {
                position += word.length();
                return new Token(DOTTED_KINDS[i], word);
            }
        }
        int start = position++;
        while (isIdentifierPart(peek(0))) {
            position++;
        }
        String word = source.substring(start, position);
        Kind kind = KEYWORDS.get(word);
	return new Token(kind == null ? Kind.ID : kind, word);
    }

    private static boolean isDigitInBase(int c, int base) {
        if (isDigit(c)) {
            return c - '0' < base;
        }
        return base == 16 && ((c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F'));
    }

private Token scanNumber() throws LexicalException {
    int start = position;
    int base = 10;
    Kind kind = Kind.INTEGER_LITERAL;
    String error = "Invalid character in number.";

    if (peek(0) == '0' && isDigit(peek(1))) {
        base = 8;
        kind = Kind.OCTAL_LITERAL;
        error = "Invalid character in octal number.";
    } else if (peek(0) == '0'
            && (peek(1) == 'x' || peek(1) == 'X')) {
        base = 16;
        kind = Kind.HEXADECIMAL_LITERAL;
        error = "Invalid character in hex number.";
        position += 2;
    }

    int digitsStart = position;

    while (isDigitInBase(peek(0), base)) {
        position++;
    }

    if (position != digitsStart && !isIdentifierPart(peek(0))) {
        return new Token(kind, source.substring(start, position));
    }

    if (position == digitsStart
            && kind == Kind.HEXADECIMAL_LITERAL
            && !isIdentifierPart(peek(0))) {
        return new Token(kind, source.substring(start, position));
    }

    if (isIdentifierPart(peek(0))) {
        while (isIdentifierPart(peek(0))) {
            position++;
        }
        throw new LexicalException(error);
    }

    return new Token(kind, source.substring(start, position));
}
    private Token scanString() throws LexicalException {
        position++;
        int start = position;
        while (peek(0) != '"') {
            if (peek(0) == -1 || peek(0) == '\n' || peek(0) == '\r') {
                throw new LexicalException("String not terminated at end of line.");
            }
            position++;
        }
        String value = source.substring(start, position);
        position++;
        return new Token(Kind.STRING_LITERAL, value);
    }

    private Token nextToken() throws LexicalException {
        skipTrivia();
        int c = peek(0);
        if (c == -1) {
            return new Token(Kind.EOF, "");
        }
        if (isLetter(c)) {
            return scanWord();
        }
        if (isDigit(c)) {
            return scanNumber();
        }
        if (c == '"') {
            return scanString();
        }
        if (peek(1) != -1) {
            String pair = source.substring(position, position + 2);
            Kind kind = SYMBOLS.get(pair);
            if (kind != null) {
                position += 2;
                return new Token(kind, pair);
            }
        }
        String symbol = Character.toString((char) c);
        Kind kind = SYMBOLS.get(symbol);
        if (kind == null) {
	    position++;
            throw new LexicalException("Illegal token.");
        }
        position++;
        return new Token(kind, symbol);
    }

private static String format(Token token) {
    	if (token.kind == Kind.ID
            	|| token.kind == Kind.INTEGER_LITERAL
            	|| token.kind == Kind.OCTAL_LITERAL
            	|| token.kind == Kind.HEXADECIMAL_LITERAL
            	|| token.kind == Kind.STRING_LITERAL) {
        	return token.kind.name() + "(" + token.text + ")";
    	}	

    		return token.kind.name();
	}
    private static String readAll(Reader reader) throws IOException {
        StringBuilder text = new StringBuilder();
        char[] buffer = new char[8192];
        int count;
        while ((count = reader.read(buffer)) != -1) {
            text.append(buffer, 0, count);
        }
        return text.toString();
    }

public static void main(String[] args) {
    if (args.length > 1) {
        System.err.println("Usage: java Scanner [program.java]");
        System.exit(1);
    }

    try (Reader reader = args.length == 0
            ? new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8))
            : Files.newBufferedReader(
                Paths.get(args[0]), StandardCharsets.UTF_8)) {

        Scanner scanner = new Scanner(readAll(reader));

        while (true) {
            try {
                Token token = scanner.nextToken();
                System.out.println(format(token));

                if (token.kind == Kind.EOF) {
                    break;
                }
            } catch (LexicalException error) {
                System.out.println(error.getMessage());
            }
        }
    } catch (IOException error) {
        System.err.println("I/O error: " + error.getMessage());
        System.exit(1);
    }
}
}
