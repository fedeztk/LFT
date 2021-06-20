import java.io.*;

public class Parser0 {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Parser0(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

    void move() {
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

    void error(String s) {
        throw new Error("near line " + lex.line + ": " + s);
    }

    void match(int t) {
        if (look.tag == t) {
            if (look.tag != Tag.EOF)
                move();
        } else
            error("syntax error");
    }

    public void start() {
        if (look.tag == '(' || look.tag == Tag.NUM) {
            expr();
            match(Tag.EOF);
        } else {
            error("Error in start");
        }
    }

    private void expr() {
        if (look.tag == '(' || look.tag == Tag.NUM) {
            term();
            exprp();
        } else {
            error("Errore in expr");
        }
    }

    private void exprp() {
        switch (look.tag) {
            case '+':
                match('+');
                term();
                exprp();
                break;
            case '-':
                match('-');
                term();
                exprp();
                break;
            case ')': //epsilon transizione
            case Tag.EOF:
                break;
            default:
                error("Error in exprp");
        }
    }

    private void term() { // fact e termp
        if (look.tag == '(' || look.tag == Tag.NUM) {
            fact();
            termp();
        } else {
            error("Error in term");
        }
    }

    private void termp() { // 
        switch (look.tag) {
            case '*':
                match('*');
                fact();
                termp();
                break;
            case '/':
                match('/');
                fact();
                termp();
                break;
            case '+':
            case '-':
            case ')':
            case Tag.EOF:
                break;
            default:
                error("Error in termp");
        }
    }

    private void fact() { // '(',numeri
        switch (look.tag) {
            case '(':
                match('(');
                expr();
                match(')');
                break;
            case Tag.NUM:
                match(Tag.NUM);
                break;
            default:
                error("Error in fact");
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = ""; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser0 parser = new Parser0(lex, br);
            parser.start();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}