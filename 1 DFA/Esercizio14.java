public class Esercizio14 {
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
                } else if (ch == ' ') {
                    state = 0;
                } else {
                    state = -1;
                }
                break;

            case 1:
                if (ch >= 48 && ch <= 57) { // caso della matricola
                    if (ch % 2 != 0) {
                        state = 1;
                    } else {
                        state = 2;
                    }
                } else if ((ch >= 'L' && ch <= 'Z')) { // caso del nome
                    state = 3;
                } else if (ch == ' ') { // caso degli spazi
                    state = 5;
                } else {
                    state = -1;
                }
                break;

            case 2:
                if (ch >= 48 && ch <= 57) { // caso della matricola
                    if (ch % 2 != 0) {
                        state = 1;
                    } else {
                        state = 2;
                    }
                } else if ((ch >= 'A' && ch <= 'K')) { // caso del nome
                    state = 3;
                } else if (ch == ' ') { // caso dello spazio
                    state = 4;
                } else {
                    state = -1;
                }
                break;

            case 3:
                if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) { // stato finale
                    state = 3;
                } else if (ch == ' ') {
                    state = 6;
                } else {
                    state = -1;
                }
                break;

            case 4:
                if (ch == ' ') { // caso spazi dopo matricola pari
                    state = 4;
                } else if ((ch >= 'A' && ch <= 'K')) {
                    state = 3;
                } else {
                    state = -1;
                }
                break;

            case 5:
                if (ch == ' ') { // caso spazi dopo matricola dispari
                    state = 5;
                } else if ((ch >= 'L' && ch <= 'Z')) {
                    state = 3;
                } else {
                    state = -1;
                }
                break;

            case 6:
                if (ch >= 'A' && ch <= 'Z') { // caso di spazi per cognomi composti
                    state = 3;
                } else if (ch == ' ') {
                    state = 6;
                } else {
                    state = -1;
                }
                break;
            }
            i = i + 1;
        }
        return state == 3 || state == 6;
    }

    public static void main(String[] args) {
		System.out.println(scan(args[0]) ? "OK" : "NOPE");
    }
}