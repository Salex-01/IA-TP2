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
                String data = s.nextLine();
                while (data.trim().contentEquals("") && s.hasNextLine()) {
                    data = s.nextLine().trim();
                    if (!data.contentEquals("")) {
                        break;
                    }
                }
                List<String> tmp = Arrays.asList(data.split(" "));
                tmp.removeIf(s1 -> s1.contentEquals(""));

                int size = parseInt(tmp.get(0));
                boolean activateSqares = tmp.get(1).equals("t");
                int[][] newSudoku = new int[size][size];
                for (int i = 0; i < size; i++) {
                    data = s.nextLine().trim();
                    String[] charNumber = data.split(" ");
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
                sudokuList.add(new Sudoku(newSudoku, activateSqares));
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
                System.out.print((ints[j] != 0 ? ints[j] : ".") + "|");
            }
            System.out.println();
        }
    }

}

