public class Esercizio19 {
    public static boolean scan(String s) {
        int state = 0;
        int i = 0;
        while (state >= 0 && i < s.length()) {
            final char ch = s.charAt(i++);
            switch (state) {
                case 0:
                    if (ch == 'P') {
                        state = 1;
                    } else if (ch >= 32 && ch <= 126) {
                        state = 11;
                    } else {
                        state = -1;
                    }
                    break;

                case 1:
                    if (ch == 'a') {
                        state = 2;
                    } else if (ch >= 32 && ch <= 126) {
                        state = 22;
                    } else {
                        state = -1;
                    }
                    break;

                case 2:
                    if (ch == 'o') {
                        state = 3;
                    } else if (ch >= 32 && ch <= 126) {
                        state = 33;
                    } else {
                        state = -1;
                    }
                    break;

                case 3:
                    if (ch == 'l') {
                        state = 4;
                    } else if (ch >= 32 && ch <= 126) {
                        state = 44;
                    } else {
                        state = -1;
                    }
                    break;

                case 4:
                    if (ch >= 32 && ch <= 126) {
                        state = 5;
                    } else {
                        state = -1;
                    }
                    break;

                case 11:
                    if (ch == 'a') {
                        state = 22;
                    } else {
                        state = -1;
                    }
                    break;

                case 22:
                    if (ch == 'o') {
                        state = 33;
                    } else {
                        state = -1;
                    }
                    break;

                case 33:
                    if (ch == 'l') {
                        state = 44;
                    } else {
                        state = -1;
                    }
                    break;

                case 44:
                    if (ch == 'o') {
                        state = 5;
                    } else {
                        state = -1;
                    }
                    break;

                case 5:
                    state = -1;
                    break;
            }
        }
        return state == 5;
    }

    public static void main(String[] args) {
        System.out.println(scan(args[0]) ? "OK" : "NOPE");
    }
}
