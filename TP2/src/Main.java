import java.util.ArrayList;

public class Main {


    public static void main(String[] args) {
        String sudokupath = "TP2/sudoku.txt";
        SudokuFileToArray sudoku = new SudokuFileToArray(sudokupath);
        ArrayList<Sudoku> sudokuList = sudoku.creatSudoku();
        int[][] sudoku1 = sudokuList.get(0).grid;
        int[][] sudoku2 = sudokuList.get(6).grid;
        sudoku.printSudoku(sudoku1);
        sudoku.printSudoku(sudoku2);

    }
}

