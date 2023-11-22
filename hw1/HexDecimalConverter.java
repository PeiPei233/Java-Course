import java.util.Scanner;

public class HexDecimalConverter {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Please input a number (starts with '0x' means hex):");
        String input = scanner.nextLine().trim();
        
        try {
            if (input.toLowerCase().startsWith("0x")) {
                System.out.println("The decimal of " + input + " is " + hexToDecimal(input.substring(2)));
            } else {
                System.out.println("The hex of " + input +  " is 0x" + decimalToHex(input));
            }
        } catch (Exception e) {
            System.out.println("Invalid Input.");
        }

        scanner.close();
    }

    public static String hexToDecimal(String hex) {
        return Integer.toString(Integer.parseInt(hex, 16));
    }

    public static String decimalToHex(String decimal) {
        return Integer.toHexString(Integer.parseInt(decimal));
    }

}