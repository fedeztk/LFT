public class Esercizio17 {
    public static boolean scan(String s) {
        int i = 0, state = 0;
        while (i < s.length() && state >= 0) {
            char ch = s.charAt(i);
            switch (state) {
                case 0:
                    if (ch == 'a') {
                        state = 3;
                    } else if (ch == 'b') {
                        state = 1;
                    } else {
                        state = -1;
                    }
                    break;

                case 1:
                    if (ch == 'a') {
                        state = 3;
                    } else if (ch == 'b') {
                        state = 2;
                    } else {
                        state = -1;
                    }
                    break;

                case 2:
                    if (ch == 'a') {
                        state = 3;
                    } else {
                        state = -1;
                    }
                    break;

                case 3:
                    if (ch == 'a' || ch == 'b') {
                        state = 3;
                    } else {
                        state =-1;
                    }
                    break;
            }
            i += 1;
        }
        return (state == 3);
    }

    public static void main(String[] args) {
        System.out.println(scan(args[0]) ? "OK" : "NOPE");
    }
}