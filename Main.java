import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
//        Scanner scan = new Scanner(System.in);
        String mode = "", in = "", out = "", data = "", alg = "";
        int key = 0;
        for (int i = 0; i < args.length; i += 2) {
            switch (args[i].toUpperCase()) {
                case "-MODE":
                    mode = args[i + 1];
                    break;
                case "-DATA":
                    data = args[i + 1];
                    break;
                case "-IN":
                    in = args[i + 1];
                    break;
                case "-OUT":
                    out = args[i + 1];
                    break;
                case "-KEY":
                    key = Integer.parseInt(args[i + 1]);
                    break;
                case "-ALG":
                    alg = args[i + 1];
                    break;
                default:
                    break;
            }
        }

//        System.out.print("Mode: ");
//        mode = scan.nextLine();
//        System.out.print("Data: ");
//        data = scan.nextLine();
//        System.out.print("In: ");
//        in = scan.nextLine();
//        System.out.print("Out: ");
//        out = scan.nextLine();
//        System.out.print("Key: ");
//        key = Integer.parseInt(scan.nextLine());
//        System.out.print("Alg: ");
//        alg = scan.nextLine();
//        scan.close();

        if (!in.equals("")) {
            data = getTextFromFile(in);
        }

        AlgorithmSelector algorithm = new AlgorithmSelector(AlgFactory.createAlgorithm(mode + "-" + alg, data, key));
        String textToReturn = algorithm.Encrypt();

        if (!out.equals("")) {
            sendTextToFile(out, textToReturn);
        } else {
            System.out.println(textToReturn);
        }
    }

    public static String getTextFromFile(String fileName) {
        File file = new File(fileName);
        try {
            StringBuilder text = new StringBuilder();
            Scanner scan = new Scanner(file);
            while (scan.hasNextLine()) {
                text.append(scan.nextLine());
            }
            scan.close();
            return text.toString();
        } catch (FileNotFoundException e) {
            return "FILE NOT FOUND";
        }
    }

    public static void sendTextToFile(String fileName, String text) {
        File file = new File(fileName);
        try {
            PrintWriter writer = new PrintWriter(file);
            writer.write(text);
            writer.close();
        } catch (FileNotFoundException e) {
        }
    }
}

class AlgorithmSelector {
    private Algorithm algorithm;

    public AlgorithmSelector(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public String Encrypt() {
        return this.algorithm.Encrypt();
    }
}

class AlgFactory {

    public static Algorithm createAlgorithm(String type, String text, int shift) {
        Algorithm alg;
        switch (type.toUpperCase()) {
            case "ENC-UNICODE":
                alg = new EncryptionUnicodeAlg(text, shift);
                break;
            case "DEC-UNICODE":
                alg = new DecryptionUnicodeAlg(text, shift);
                break;
            case "DEC-SHIFT":
                alg = new DecryptionShiftAlg(text, shift);
                break;
            default:
                alg = new EncryptionShiftAlg(text, shift);
                break;
        }
        return alg;
    }
}

interface Algorithm {

    String Encrypt();

    int ALPHABET_LENGTH = 26;
    int UPPER_START = 'A';
    int UPPER_END = 'Z';
    int LOWER_START = 'a';
    int LOWER_END = 'z';

}

abstract class Data {
    private String text;
    private int shift;

    public Data(String text, int shift) {
        this.text = text;
        this.shift = shift;
    }

    public abstract char getNewChar(char ch, int shift);

    public String getText() {
        return this.text;
    }

    public int getShift() {
        return this.shift;
    }
}

class EncryptionShiftAlg extends Data implements Algorithm {

    public EncryptionShiftAlg(String text, int shift) {
        super(text, shift);
    }

    @Override
    public String Encrypt() {
        String text = this.getText();
        int shift = this.getShift();
        StringBuilder result = new StringBuilder();
        char ch;

        for (int i = 0; i < text.length(); i++) {
            ch = text.charAt(i);
            if ( (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') ) {
                result.append(getNewChar(ch, shift));
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }

    @Override
    public char getNewChar(char ch, int shift) {
        char result;
        if ( (ch >= UPPER_START && ch <= UPPER_END) ) {
            if (ch + shift > UPPER_END) {
                result = (char) ( ((ch + shift) % UPPER_END ) + UPPER_START - 1);
            } else {
                result = (char) (ch + shift);
            }
        } else {
            if (ch + shift > LOWER_END) {
                result = (char) ( ((ch + shift) % LOWER_END )+ LOWER_START - 1);
            } else {
                result = (char) (ch + shift);
            }
        }
        return result;
    }

}

class EncryptionUnicodeAlg extends Data implements Algorithm {

    public EncryptionUnicodeAlg(String text, int shift) {
        super(text, shift);
    }

    @Override
    public String Encrypt() {
        String text = this.getText();
        int shift = this.getShift();
        StringBuilder result = new StringBuilder();
        char ch;

        for (int i = 0; i < text.length(); i++) {
            ch = text.charAt(i);
            result.append(getNewChar(ch, shift));
        }
        return result.toString();
    }

    @Override
    public char getNewChar(char ch, int shift) {
        return (char)(ch + shift);
    }
}

class DecryptionShiftAlg extends Data implements Algorithm {

    public DecryptionShiftAlg(String text, int shift) {
        super(text, shift);
    }

    @Override
    public String Encrypt() {
        String text = this.getText();
        int shift = this.getShift();
        StringBuilder result = new StringBuilder();
        char ch;

        for (int i = 0; i < text.length(); i++) {
            ch = text.charAt(i);
            if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')) {
                result.append(getNewChar(ch, shift));
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }

    @Override
    public char getNewChar(char ch, int shift) {
        char result;
        if ( (ch >= UPPER_START && ch <= UPPER_END) ) {
            if (ch - shift < UPPER_START) {
                result = (char) ( (ch - shift) % UPPER_START + ALPHABET_LENGTH);
            } else {
                result = (char) (ch - shift);
            }
        } else {
            if (ch - shift < LOWER_START) {
                result = (char) ( (ch - shift) % LOWER_START + ALPHABET_LENGTH);
            } else {
                result = (char) (ch - shift);
            }
        }
        return result;
    }
}

class DecryptionUnicodeAlg extends Data implements Algorithm {

    public DecryptionUnicodeAlg(String text, int shift) {
        super(text, shift);
    }

    @Override
    public String Encrypt() {
        String text = this.getText();
        int shift = this.getShift();
        StringBuilder result = new StringBuilder();
        char ch;

        for (int i = 0; i < text.length(); i++) {
            ch = text.charAt(i);
            result.append(getNewChar(ch, shift));
        }
        return result.toString();
    }

    @Override
    public char getNewChar(char ch, int shift) {
        return (char) (ch - shift);
    }
}
