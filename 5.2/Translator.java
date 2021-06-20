import java.io.*;

public class Translator {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    SymbolTable st = new SymbolTable();
    CodeGenerator code = new CodeGenerator();
    int count = 0;

    public Translator(Lexer l, BufferedReader br) {
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
            error("syntax error, " + t + " expected tag, " + look.tag + " tag found instead");
    }

    public void prog() {
        if (look.tag == '(') { // prog>>> stat EOF
            int lnext_prog = code.newLabel();
            stat(lnext_prog);
            code.emitLabel(lnext_prog);
            match(Tag.EOF);
            try {
                code.toJasmin(); // output
            } catch (java.io.IOException e) {
                System.out.println("IO error\n");
            }
        } else {
            error("Error in prog");
        }
    }

    public void statlist(int lnext_prog) {
        if (look.tag == '(') { // statlist>>> stat statlistp
            int slnext = code.newLabel();
            stat(slnext);
            code.emitLabel(slnext);
            statlistp(lnext_prog);
        } else {
            error("Error in statlist");
        }
    }

    public void statlistp(int lnext_prog) {
        switch (look.tag) {
            case '(': // statlistp>>> stat statlistp
                int s_next = code.newLabel();
                stat(s_next);
                code.emitLabel(s_next);
                statlistp(lnext_prog);
                break;
            case ')': // epsilon transizioni
                break;
            default:
                error("Error in statlistp");
        }
    }

    public void stat(int lnext_prog) {
        if (look.tag == '(') { // stat>>> (statp)
            match('(');
            statp(lnext_prog);
            match(')');
        } else {
            error("Error in stat");
        }
    }

    public void statp(int lnext) {
        int l_true, l_false;
        switch (look.tag) {
            case '=': // statp>>> = ID expr
                match('=');
                int assign_id_addr = st.lookupAddress(((Word) look).lexeme); // cerca l'indirizzo del lessema
                if (look.tag == Tag.ID && assign_id_addr == -1) {// var non ancora inserita
                    assign_id_addr = count;
                    st.insert(((Word) look).lexeme, count++);
                }
                match(Tag.ID);
                expr();
                code.emit(OpCode.istore, assign_id_addr);
                break;
            case Tag.COND: // statp>>> COND bexpr stat elseopt
                match(Tag.COND);
                l_true = code.newLabel();
                l_false = code.newLabel();
                bexpr(l_true, l_false);
                code.emitLabel(l_true);
                stat(lnext);
                code.emit(OpCode.GOto, lnext);
                code.emitLabel(l_false);
                elseopt(lnext);
                break;
            case Tag.WHILE: // statp>>> WHILE bexpr stat
                match(Tag.WHILE);
                int begin = code.newLabel();
                code.emitLabel(begin);
                l_true = code.newLabel();
                l_false = lnext;
                bexpr(l_true, l_false);
                code.emitLabel(l_true);
                stat(begin);
                code.emit(OpCode.GOto, begin);
                break;
            case Tag.DO: // statp>>> DO statlist
                match(Tag.DO);
                statlist(lnext);
                break;
            case Tag.PRINT: // PRINT exprlist
                match(Tag.PRINT);
                exprlist(1);
                break;
            case Tag.READ: // READ id
                match(Tag.READ);
                if (look.tag == Tag.ID) {
                    int read_id_addr = st.lookupAddress(((Word) look).lexeme);
                    if (read_id_addr == -1) {
                        read_id_addr = count;
                        st.insert(((Word) look).lexeme, count++);
                    }
                    match(Tag.ID);
                    code.emit(OpCode.invokestatic, 0);
                    code.emit(OpCode.istore, read_id_addr);
                } else
                    error("Error in grammar (statp) after read with " + look);
                break;
            default:
                error("Error in statp");
        }
    }

    public void elseopt(int lnext) {
        switch (look.tag) { // elseopt>>> (ELSE stat)
            case '(':
                match('(');
                match(Tag.ELSE);
                stat(lnext);
                match(')');
                break;
            case ')': // epsilon transizione
                break;
            default:
                error("Error in elseopt");
        }
    }

    public void bexpr(int l_true, int l_false) {
        if (look.tag == '(') { // bexpr>>> (B)
            match('(');
            B(l_true, l_false);
            match(')');
        } else {
            error("Error in bexpr");
        }
    }

    public void B(int l_true, int l_false) {
        switch (look.tag) {
            case Tag.RELOP: // B>>> bexprp
                bexprp(l_true, l_false);
                break;
            case Tag.AND: // B>>> &&(B)(B)
                match(Tag.AND);
                match('(');
                int b1t = code.newLabel();
                B(b1t, l_false); // B1
                match(')');
                code.emitLabel(b1t); // emit dell'etichetta che ha il codice per B1=true
                match('(');
                B(l_true, l_false); // B2
                match(')');
                break;
            case Tag.OR: // B>>> ||(B)(B)
                match(Tag.OR);
                match('(');
                int b1f = code.newLabel(); // emit dell'etichetta che ha il codice per b1=false
                B(l_true, b1f); // B1
                match(')');
                code.emitLabel(b1f); // emit della label false di bexprp1
                match('(');
                B(l_true, l_false); // B2
                match(')');
                break;
            default: // B>>> !(B)
                match('!');
                match('(');
                B(l_false, l_true);
                match(')');
                break;
        }
    }

    public void bexprp(int l_true, int l_false) {
        if (look.tag == Tag.RELOP) {
            switch (((Word) look).lexeme) {
                case "==": // == (=)
                    match(Tag.RELOP);
                    expr();
                    expr();
                    code.emit(OpCode.if_icmpeq, l_true);
                    code.emit(OpCode.GOto, l_false);
                    break;
                case ">": // >
                    match(Tag.RELOP);
                    expr();
                    expr();
                    code.emit(OpCode.if_icmpgt, l_true);
                    code.emit(OpCode.GOto, l_false);
                    break;
                case "<": // <
                    match(Tag.RELOP);
                    expr();
                    expr();
                    code.emit(OpCode.if_icmplt, l_true);
                    code.emit(OpCode.GOto, l_false);
                    break;
                case ">=": // >=
                    match(Tag.RELOP);
                    expr();
                    expr();
                    code.emit(OpCode.if_icmpge, l_true);
                    code.emit(OpCode.GOto, l_false);
                    break;
                case "<=": // <=
                    match(Tag.RELOP);
                    expr();
                    expr();
                    code.emit(OpCode.if_icmple, l_true);
                    code.emit(OpCode.GOto, l_false);
                    break;
                case "<>": // <> (!=)
                    match(Tag.RELOP);
                    expr();
                    expr();
                    code.emit(OpCode.if_icmpne, l_true);
                    code.emit(OpCode.GOto, l_false);
                    break;
                default:
                    error("Error in bexprp");
            }
        }
    }

    public void expr() {
        switch (look.tag) {
            case Tag.NUM:
                code.emit(OpCode.ldc, (((NumberTok) look).lexeme));
                match(Tag.NUM);
                break;
            case Tag.ID:
                int read_id_addr = st.lookupAddress(((Word) look).lexeme);
                if (read_id_addr == -1) {
                    error("Error in expr, undeclared variable " + ((Word) look).lexeme);
                }
                code.emit(OpCode.iload, read_id_addr);
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

    public void exprp() {
        switch (look.tag) {
            case '+':
                match('+');
                exprlist(2);
                break;
            case '-':
                match('-');
                expr();
                expr();
                code.emit(OpCode.isub);
                break;
            case '*':
                match('*');
                exprlist(3);
                break;
            case '/':
                match('/');
                expr();
                expr();
                code.emit(OpCode.idiv);
                break;
            default:
                error("Error in exprp");
        }
    }

    public void exprlist(int op) {
        if (look.tag == Tag.NUM || look.tag == Tag.ID || look.tag == '(') {
            expr();
            if (op == 1) { // exprlist per print di 1 o più elementi
                code.emit(OpCode.invokestatic, 1);
            }
            exprlistp(op);
        } else {
            error("Error in exprlist");
        }
    }

    public void exprlistp(int op) {
        switch (look.tag) {
            case Tag.NUM: // exprlistp>>> expr exprlistp
            case Tag.ID:
            case '(':
                expr();
                if (op == 1) { // exprlistp per print di 1 o più elementi
                    code.emit(OpCode.invokestatic, 1);
                } else if (op == 2) { // exprlistp per addizione di 1 o più elementi
                    code.emit(OpCode.iadd);
                } else if (op == 3) { // exprlistp per moltiplicazione di 1 o più elementi
                    code.emit(OpCode.imul);
                } else {
                    error("Error in exprlistp, not a valid inherited attribute");
                }
                exprlistp(op);
                break;
            case ')': // epsilon transizione
                break;
            default:
                error("Error in exprlistp");
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "Prova.lft"; // percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Translator tr = new Translator(lex, br);
            tr.prog();
            System.out.println("\nFile Output.j generato!");
            System.out.println(
                    "Digita 'java -jar jasmin.jar Output.j' per il file Output.class e 'java Output' per eseguirlo.\n");
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
