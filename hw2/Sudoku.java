import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Sudoku {

    public final static int SIZE = 9;       // Size of the Sudoku board
    int[][] board = new int[SIZE][SIZE];    // The Sudoku board

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Sudoku sudoku = new Sudoku();
        System.out.println("Enter 1 to enter a sudoku and solve, 2 to generate a sudoku: ");
        try {
            int choice = scanner.nextInt();
            if (choice == 1) {  // Solve a Sudoku
                sudoku.scan();
                if (sudoku.solve()) {
                    System.out.println("Solution:");
                    sudoku.print();
                } else {
                    System.out.println("No solution");
                }
            } else if (choice == 2) {   // Generate a Sudoku
                System.out.println("Enter number of prompts: ");
                int numPrompts = scanner.nextInt();
                if (numPrompts < 0 || numPrompts > 81) {
                    System.out.println("The number of prompts must be between 0 and 81");
                    scanner.close();
                    return;
                }
                sudoku.generate(numPrompts);
                sudoku.print();
            } else {
                System.out.println("Invalid input");
            }
        } catch (Exception e) {
            System.out.println("Invalid input");
        }
        scanner.close();
    }

    /**
     * Prints the Sudoku board.
     */
    void print() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                System.out.print(board[i][j] == 0 ? "." : Integer.toString(board[i][j]));
                System.out.print(' ');
            }
            System.out.println();
        }
    }

    /**
     * Scans the Sudoku board from the console.
     * @throws Exception If the input is invalid.
     */
    void scan() throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the sudoku: ");
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                String input = scanner.next();
                if (input.equals(".")) {    // Empty cell
                    board[i][j] = 0;
                } else {
                    board[i][j] = Integer.parseInt(input);
                    // Check if the input is valid
                    if (board[i][j] < 1 || board[i][j] > SIZE) {
                        scanner.close();
                        throw new Exception();
                    }
                }
            }
        }
        scanner.close();
    }

    /**
     * Generates a Sudoku board with the given number of prompts.
     * @param numPrompts The number of prompts to generate.
     */
    void generate(int numPrompts) {
        Random rand = new Random();

        // Generate a random solved Sudoku board
        solve();

        // Remove random entries from the board
        for (int i = 0; i < 81 - numPrompts; i++) {
            int row = rand.nextInt(SIZE);
            int col = rand.nextInt(SIZE);
            // Make sure the cell is not empty
            while (board[row][col] == 0) {
                row = rand.nextInt(SIZE);
                col = rand.nextInt(SIZE);
            }
            board[row][col] = 0;
        }
    }

    /**
     * Solves the Sudoku board.
     * @return True if the board is solved, false otherwise.
     */
    boolean solve() {
        // Generate a random order of numbers to try
        List<Integer> nums = new ArrayList<Integer>();
        for (int i = 1; i <= SIZE; i++) {
            nums.add(i);
        }
        Collections.shuffle(nums);

        // Find the next empty cell
        int nexti = 0, nextj = 0;
        boolean found = false;
        for (int i = 0; i < SIZE && !found; i++) {
            for (int j = 0; j < SIZE && !found; j++) {
                if (board[i][j] == 0) {
                    nexti = i;
                    nextj = j;
                    found = true;
                }
            }
        }
        if (!found) {   // No empty cells, check if the complete board is valid
            return check();
        }

        // Try each number in the cell
        for (int i = 0; i < SIZE; i++) {
            board[nexti][nextj] = nums.get(i);
            if (!checkRow(nexti) || !checkCol(nextj) || !checkSquare(nexti - nexti % 3, nextj - nextj % 3)) {
                // Number is invalid, try the next number
                continue;
            }
            if (solve()) {
                return true;
            }
        }
        
        // No solution found, backtrack
        board[nexti][nextj] = 0;
        return false;
    }

    /**
     * Checks if the Sudoku board is valid.
     * @return True if the board is valid, false otherwise.
     */
    boolean check() {
        // Check each row, column, and square
        for (int i = 0; i < SIZE; i++) {
            if (!checkRow(i) || !checkCol(i)) {
                return false;
            }
        }
        for (int i = 0; i < SIZE; i += 3) {
            for (int j = 0; j < SIZE; j += 3) {
                if (!checkSquare(i, j)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if the given row is valid.
     * @param row The row to check.
     * @return True if the row is valid, false otherwise.
     */
    boolean checkRow(int row) {
        boolean[] nums = new boolean[SIZE];
        for (int i = 0; i < SIZE; i++) {
            if (board[row][i] != 0) {
                if (nums[board[row][i] - 1]) {  // Number already exists in row
                    return false;
                }
                nums[board[row][i] - 1] = true;
            }
        }
        return true;
    }

    /**
     * Checks if the given column is valid.
     * @param col The column to check.
     * @return True if the column is valid, false otherwise.
     */
    boolean checkCol(int col) {
        boolean[] nums = new boolean[SIZE];
        for (int i = 0; i < SIZE; i++) {
            if (board[i][col] != 0) {
                if (nums[board[i][col] - 1]) {  // Number already exists in column
                    return false;
                }
                nums[board[i][col] - 1] = true;
            }
        }
        return true;
    }

    /**
     * Checks if the given square is valid.
     * @param row The row of the square to check.
     * @param col The column of the square to check.
     * @return True if the square is valid, false otherwise.
     */
    boolean checkSquare(int row, int col) {
        boolean[] nums = new boolean[SIZE];
        for (int i = row; i < row + 3; i++) {
            for (int j = col; j < col + 3; j++) {   // Check each cell in the square
                if (board[i][j] != 0) {
                    if (nums[board[i][j] - 1]) {
                        return false;
                    }
                    nums[board[i][j] - 1] = true;
                }
            }
        }
        return true;
    }

    /**
     * Clears the Sudoku board.
     */
    void clear() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = 0;
            }
        }
    }

}
