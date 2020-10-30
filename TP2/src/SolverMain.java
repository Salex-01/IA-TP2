import java.util.List;

public class SolverMain {

    public static void main(String[] args) {
        String sudokupath;
        if (args.length < 1) {
            sudokupath = "TP2/sudoku.txt";
        } else {
            sudokupath = args[0];
        }
        SudokuUtils sudokuUtils = new SudokuUtils(sudokupath);
        List<Sudoku> sudokuList = sudokuUtils.createSudoku();
        for (Sudoku s : sudokuList) {
            SudokuUtils.printSudoku(s.grid);
            try {
                SudokuUtils.printSudoku(s.solve(0,""));
            } catch (IllegalStateException | NullPointerException e) {
                System.out.println("Failed to solve this grid");
            }
        }
        System.out.println("The end");
    }
}

