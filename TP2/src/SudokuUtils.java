import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static java.lang.Integer.parseInt;

public class SudokuUtils {

    File sudokuFile;
    List<Sudoku> sudokuList;

    SudokuUtils(String path) {
        sudokuFile = new File(path);
        sudokuList = new LinkedList<>();
    }

    List<Sudoku> createSudoku() {
        try {
            Scanner s = new Scanner(sudokuFile);
            while (s.hasNextLine()) {
                String tmp = s.nextLine();
                while (tmp.trim().contentEquals("") && s.hasNextLine()) {
                    tmp = s.nextLine().trim();
                    if (!tmp.contentEquals("")) {
                        break;
                    }
                }
                List<String> data = Arrays.asList(tmp.split(" "));
                data.removeIf(s1 -> s1.contentEquals(""));
                boolean add = data.size() < 3;
                int size = parseInt(data.get(0));
                boolean activateSqares = data.get(1).equals("t");
                int[][] newSudoku = new int[size][size];
                for (int i = 0; i < size; i++) {
                    tmp = s.nextLine().trim();
                    String[] charNumber = tmp.split(" ");
                    for (int j = 0; j < size; j++) {
                        if ("-".equals(charNumber[j])) {
                            newSudoku[i][j] = 0;
                        } else {
                            int number = parseInt(charNumber[j]);
                            try {
                                if (number > size || number <= 0) {
                                    System.out.println("error invalid number in the sudoku");
                                    throw new IllegalArgumentException();
                                }
                            } catch (IllegalArgumentException e) {
                                while (s.hasNextLine()) {
                                    if (s.nextLine().trim().contentEquals("")) {
                                        break;
                                    }
                                }
                            }
                            newSudoku[i][j] = number;
                        }
                    }
                }
                if (add) {
                    sudokuList.add(new Sudoku(newSudoku, activateSqares));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return sudokuList;
    }

    static void printSudoku(int[][] sudoku) {
        for (int[] ints : sudoku) {
            System.out.print("|");
            for (int j = 0; j < sudoku.length; j++) {
                if (ints[j] == 0) {
                    System.out.print("." + " ".repeat(Integer.toString(sudoku.length).length() - 1) + "|");
                } else {
                    System.out.print(ints[j] + " ".repeat(Integer.toString(sudoku.length).length() - Integer.toString(ints[j]).length()) + "|");
                }
            }
            System.out.println();
        }
    }

}