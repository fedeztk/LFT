import java.io.*;

public class Parser {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Parser(Lexer l, BufferedReader br) {
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

    public void prog() {
        if (look.tag == '(') {
            stat();
            match(Tag.EOF);
        } else {
            error("Error in prog");
        }
    }

    private void statlist() {
        if (look.tag == '(') {
            stat();
            statlistp();
        } else {
            error("Error in statlist");
        }
    }

    private void statlistp() {
        switch (look.tag) {
        case '(':
            stat(); 
            statlistp();
            break;
        case ')':
            break;
        default:
            error("Error in statlistp");
        }
    }

    private void stat() {
        if (look.tag == '(') {
            match('(');
            statp();
            match(')');
        } else {
            error("Error in stat");
        }
    }

    private void statp() {
        switch (look.tag) {
        case '=':
            match('=');
            match(Tag.ID);
            expr();
            break;
        case Tag.COND:
            match(Tag.COND);
            bexpr();
            stat();
            elseopt();
            break;
        case Tag.WHILE:
            match(Tag.WHILE);
            bexpr();
            stat();
            break;
        case Tag.DO:
            match(Tag.DO);
            statlist();
            break;
        case Tag.PRINT:
            match(Tag.PRINT);
            exprlist();
            break;
        case Tag.READ:
            match(Tag.READ);
            match(Tag.ID);
            break;
        }
    }

    private void elseopt() { 
        switch (look.tag) {
            case '(':
                match('(');
                match(Tag.ELSE);
                stat();
                match(')');
                break;
            case ')':
                break;
            default:
                error("Error in elseopt");
        }
    }

    private void bexpr() {
        if (look.tag == '(') {
            match('(');
            bexprp();
            match(')');
        } else {
            error("Error in bexpr");
        }
    }

    private void bexprp() {
        if (look.tag == Tag.RELOP) {
            match(Tag.RELOP);
            expr();
            expr();
        } else {
            error("Error in bexprp");
        }
    }

    private void expr() {
        switch (look.tag) {
        case Tag.NUM:
            match(Tag.NUM);
            break;
        case Tag.ID:
            match(Tag.ID);
            break;
        case '(':
            match('(');
            exprp();
            match(')');
            break;
        default:
            error("Error in expr");
        }
    }

    private void exprp() { 
        switch (look.tag) {
        case '+':
            match('+');
            exprlist();
            break;
        case '-':
            match('-');
            expr();
            expr();
            break;
        case '*':
            match('*');
            exprlist();
            break;
        case '/':
            match('/');
            expr();
            expr();
            break;
        default:
            error("Error in exprp");
        }
    }

    private void exprlist() {
        switch (look.tag) {
            case '(':
            case Tag.ID:
            case Tag.NUM:
                expr();
                exprlistp();
                break;
            default:
                error("Error in exprlist");
        }
    }

    private void exprlistp() { 
        switch (look.tag) { 
        case Tag.NUM:
        case Tag.ID:
        case '(':
            expr();
            exprlistp();
            break;
        case ')':
            break;
        default:
            error("Error in exprlistp");
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = ""; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser parser = new Parser(lex, br);
            parser.prog();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}