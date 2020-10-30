import java.util.List;

public class SolverMain {

    public static void main(String[] args) {
        String sudokupath;
        if (args.length < 1) {
            sudokupath = "TP2/sudoku.txt";
        } else {
            sudokupath = args[0];
        }
        // cree une liste de sudoku avec le fichier passe en argument
        SudokuUtils sudokuUtils = new SudokuUtils(sudokupath);
        List<Sudoku> sudokuList = sudokuUtils.createSudoku();
        for (Sudoku s : sudokuList) {
            // affiche les sudokus suivient de leur solution trouvé par le solver
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

