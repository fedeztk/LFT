import java.io.*;

public class Valutatore {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Valutatore(Lexer l, BufferedReader br) {
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
            error("Syntax error");
    }

    public void start() {
        if (look.tag == '(' || look.tag == Tag.NUM) {
            int expr_val;
            expr_val = expr();
            match(Tag.EOF);
            System.out.println("Result="+expr_val);
        } else {
            error("Error in start");
        }
    }

    private int expr() {
        if (look.tag == '(' || look.tag == Tag.NUM) {
            int term_val, exprp_val;
            term_val = term();
            exprp_val = exprp(term_val);
            return exprp_val;
        } else {
            error("Error in expr");
            return -1;
        }
    }

    private int exprp(int exprp_i) {
        int term_val, exprp_val;
        switch (look.tag) {
            case '+':
                match('+');
                term_val = term();
                exprp_val = exprp(exprp_i + term_val);
                return exprp_val;
            case '-':
                match('-');
                term_val = term();
                exprp_val = exprp(exprp_i - term_val);
                return exprp_val;
            case ')':
            case Tag.EOF:
                return exprp_i;
            default:
                error("Error in exprp");
                return -1;
        }
    }

    private int term() {
        if (look.tag == '(' || look.tag == Tag.NUM) {
            int termp_i, term_val;
            termp_i = fact();
            term_val = termp(termp_i);
            return term_val;
        } else {
            error("Error in term");
            return -1;
        }
    }

    private int termp(int termp_i) {
        int fact_val, termp_val;
        switch (look.tag) {
            case '*':
                match('*');
                fact_val = fact();
                termp_val = termp(termp_i * fact_val);
                return termp_val;
            case '/':
                match('/');
                fact_val = fact();
                termp_val = termp(termp_i / fact_val);
                return termp_val;
            case Tag.EOF:
            case ')':
            case '+':
            case '-':
                return termp_i;
            default:
                error("Error in termp");
                return -1;
        }
    }

    private int fact() {
        int fact_val;
        switch (look.tag) {
            case '(':
                match('(');
                fact_val = expr();
                match(')');
                return fact_val;
            case Tag.NUM:
                fact_val = (((NumberTok) look).lexeme);
                match(Tag.NUM);
                return fact_val;
            default:
                error("Error in fact");
                return -1; 
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = ""; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Valutatore valutatore = new Valutatore(lex, br);
            valutatore.start();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}