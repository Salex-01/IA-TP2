import java.util.List;

public class SolverMain {

    public static void main(String[] args) {
        String filePath = "TP2/sudoku.txt";
        boolean debug = false;

        for (int i = 0; i < args.length; i += 2) {
            switch (args[i]) {
                case "file":
                    filePath = args[i + 1];
                    break;
                case "debug":
                    debug = args[i + 1].toLowerCase().startsWith("t");
                    break;
                default:
                    System.out.println("Unknown argument \"" + args[i] + "\"");
                    System.exit(-1);
                    break;
            }
        }
        // Crée une liste de sudoku avec le fichier passe en argument
        List<Sudoku> sudokuList = new SudokuUtils().createSudoku(filePath);
        for (Sudoku s : sudokuList) {
            // Affiche les sudoku suivis de leur solution trouvée par le solver
            SudokuUtils.printSudoku(s.grid);
            String s1 = " ".repeat((s.grid.length * (Integer.toString(s.grid.length).length() + 1) + 1) / 2);
            System.out.println(s1 + "V" + s1);
            try {
                SudokuUtils.printSudoku(s.solve(0, "", debug, false));
            } catch (IllegalStateException | NullPointerException e) {
                System.out.println("Failed to solve this grid");
            }
            System.out.println();
        }
        System.out.println("The end");
    }
}