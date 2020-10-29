public class Main {


    public static void main(String[] args) {
        String sudokupath = "TP2/sudoku.txt";
        SudokuFileToArray sudoku = new SudokuFileToArray(sudokupath);
        sudoku.creatSudoku();
        int[][] sudoku1 = sudoku.sudokuList.get(0);
        int[][] sudoku2 = sudoku.sudokuList.get(1);
        sudoku.printSudoku(sudoku1);
        sudoku.printSudoku(sudoku2);

    }
}

