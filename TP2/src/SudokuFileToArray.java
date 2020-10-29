import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.Integer.parseInt;
import static java.lang.System.exit;

public class SudokuFileToArray {

    File sudokuFile;
    ArrayList<Sudoku> sudokuList;

    SudokuFileToArray(String path) {
        sudokuFile = new File(path);
        if (sudokuFile == null) {
            System.out.println("error file not found");
            exit(-1);
        }
        sudokuList = new ArrayList<Sudoku>();
    }

    ArrayList<Sudoku> creatSudoku() {
        try {
            Scanner s = new Scanner(sudokuFile);
            while (s.hasNextLine()) {
                String data = s.nextLine();
                String[] firstline = data.split(" ");

                int nbLines = parseInt(firstline[0]);
                boolean activateSqares;
                if (firstline[1] == "t") {
                    activateSqares = true;
                }
                else {
                    activateSqares = false;
                }
                int[][] newSudoku = new int[nbLines][nbLines];
                for (int i = 0; i < nbLines; i++) {
                    data = s.nextLine();
                    String[] charNumber = data.split(" ");
                    for (int j = 0; j < nbLines; j++) {
                        switch (charNumber[j]) {
                            case "-":
                                newSudoku[i][j] = 0;
                                break;
                            default:
                                int number = parseInt(charNumber[j]);
                                if (number > nbLines || number <= 0) {
                                    System.out.println("error invalid number in the sudoku");
                                    return sudokuList;
                                }
                                newSudoku[i][j] = number;
                        }
                    }
                }
                sudokuList.add(new Sudoku(newSudoku,activateSqares));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return sudokuList;
    }

    void printSudoku(int[][] sudoku) {
        for (int i = 0; i < sudoku.length; i++) {
            System.out.print("| ");
            for (int j = 0; j < sudoku.length; j++) {
                System.out.print(sudoku[i][j] + " | ");
            }
            System.out.println("\n");
        }
    }

}

