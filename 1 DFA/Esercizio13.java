public class Esercizio13 {
    public static boolean scan(String s) {
        int state = 0;
        int i = 0;
        while (state >= 0 && i < s.length()) {
            char ch = s.charAt(i);
            switch (state) {
            case 0:
                if (ch >= 48 && ch <= 57) {
                    if (ch % 2 != 0) {
                        state = 1;
                    } else {
                        state = 2;
                    }
                } else {
                    state = -1;
                }
                break;

            case 1:
                if (ch >= 48 && ch <= 57) {
                    if (ch % 2 != 0) {
                        state = 1;
                    } else {
                        state = 2;
                    }
                } else if (ch >= 'L' && ch <= 'Z') {
                    state = 3;
                } else {
                    state = -1;
                }
                break;

            case 2:
                if (ch >= 48 && ch <= 57) {
                    if (ch % 2 != 0) {
                        state = 1;
                    } else {
                        state = 2;
                    }
                } else if (ch >= 'A' && ch <= 'K') {
                    state = 3;
                } else {
                    state = -1;
                }
                break;

            case 3:
                if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
                    state = 3;
                } else {
                    state = -1;
                }
                break;
            }
            i = i + 1;
        }
        return state == 3;
    }

    public static void main(String[] args) {
		System.out.println(scan(args[0]) ? "OK" : "NOPE");
    }
}