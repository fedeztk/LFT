public class Esercizio18 {
    public static boolean scan(String s) {
        int i, state = 0;
        for (i = 0; i < s.length() && state >= 0; i += 1) {
            char ch = s.charAt(i);
            switch (state) {
            case 0:
                if (ch == 'a') {
                    state = 1;
                } else if (ch == 'b') {
                    state = 0;
                } else {
                    state = -1;
                }
                break;

            case 1:
                if (ch == 'a') {
                    state = 1;
                } else if (ch == 'b') {
                    state = 2;
                } else {
                    state = -1;
                }
                break;

            case 2:
                if (ch == 'a') {
                    state = 1;
                } else if (ch == 'b') {
                    state = 3;
                } else {
                    state = -1;
                }
                break;

            case 3:
                if (ch == 'a') {
                    state = 1;
                } else if (ch == 'b') {
                    state = 0;
                } else {
                    state = -1;
                }
                break;
            }
        }
        return state > 0;
    }

    public static void main(String[] args) {
		System.out.println(scan(args[0]) ? "OK" : "NOPE");
    }
}