public class Esercizio12 {
    public static boolean scan(String s) {
        int state = 0;
        int i = 0;
        while (state >= 0 && i < s.length()) {
            char ch = s.charAt(i);
            switch (state) {
            case 0:
                if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')) {
                    state = 2;
                } else if(ch=='_'){
                    state = 1;
                } else {
                    state = -1;
                }
                break;

            case 1:
                if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= 48 && ch <= 57)) {
                    state = 2;
                } else if(ch == '_' ){
                    state = 1;
                } else {
                    state=-1;
                }
                break;

            case 2:
                if((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= 48 && ch <= 57)|| (ch=='_')){
                    state=2;
                } else {
                    state=-1;
                }
            }
            i = i + 1;
        }
        return state == 2;
    }

    public static void main(String[] args) {
		System.out.println(scan(args[0]) ? "OK" : "NOPE");
    }
}