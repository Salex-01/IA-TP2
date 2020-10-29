import java.util.Comparator;
import java.util.LinkedList;

public class Sudoku {
    int[][] grid;
    int[][] solvedGrid;
    int squareSize;
    boolean activateSqares;

    static int[][] deepClone(int[][] array) {
        int[][] res = new int[array.length][array[0].length];
        for (int i = 0; i < array.length; i++) {
            System.arraycopy(array[i], 0, res[i], 0, array[0].length);
        }
        return res;
    }

    public Sudoku(int[][] g, boolean s) {
        grid = g;
        solvedGrid = new int[grid.length][grid.length];
        squareSize = (int) Math.sqrt(g.length);
        activateSqares = s;
    }

    public Sudoku(int[][] g) {
        new Sudoku(g, true);
    }

    public int[][] solve() {
        // Contraintes : les cases ayant une valeur dans grid doivent avoir la même valeur dans la solution -> grid[x][y]^2 == grid[x][y]*solvedGrid[x][y]
        // Contraintes : toutes les cases d'une même ligne/colonne ou d'un même carré doivent être différentes
        // Contraintes : toutes les cases doivent être comprises dans [1;n]
        // Backtracking -> MRV + DH + LCV -> AC3

        LinkedList<Sudoku> children = generateChildren();

        // Rangement des enfants dans l'ordre de préférence LCV
        children.sort((Comparator<Sudoku>) (o1, o2) -> {

        });
        int[][] solution;
        while (!children.isEmpty()) {
            solution = children.remove(0).solve();
            if (solution != null) {
                return solution;
            }
        }
        return null;
    }

    // Génère les enfants produits par l'assignation de la case choisie par MRV et DH
    private LinkedList<Sudoku> generateChildren() {
        int[][] RV = new int[grid.length][grid.length];
        int[][] DH = new int[grid.length][grid.length];
        boolean[][][] values = new boolean[grid.length][grid.length][grid.length];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                for (int k = 0; k < grid.length; k++) {
                    values[i][j][k] = true;
                }
            }
        }
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                if (grid[i][j] != 0) {
                    RV[i][j] = grid.length + 1;
                } else {
                    int dh = 0;
                    for (int k = 0; k < grid.length; k++) {
                        if (k != j) {
                            if (grid[i][k] == 0) {
                                dh++;
                            } else {
                                values[i][j][grid[i][k] - 1] = false;
                            }
                        }
                        if ((k != i)) {
                            if (grid[k][j] == 0) {
                                dh++;
                            } else {
                                values[i][j][grid[k][j] - 1] = false;
                            }
                        }
                    }
                    if (activateSqares) {
                        int baseI = (i / squareSize) * squareSize;
                        int baseJ = (j / squareSize) * squareSize;
                        for (int k = baseI; k < baseI + squareSize; k++) {
                            for (int l = baseJ; j < baseJ + squareSize; l++) {
                                if ((k != i) || (l != j)) {
                                    if (grid[k][l] == 0) {
                                        dh++;
                                    } else {
                                        values[i][j][grid[k][l] - 1] = false;
                                    }
                                }
                            }
                        }
                    }
                    int rv = 0;
                    for (int k = 0; k < grid.length; k++) {
                        if (values[i][j][k]) {
                            rv++;
                        }
                    }
                    RV[i][j] = rv;
                    DH[i][j] = dh;
                }
            }
        }
        int bestI = 0;
        int bestJ = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                if ((RV[i][j] < RV[bestI][bestJ]) || ((RV[i][j] == RV[bestI][bestJ]) && (DH[i][j] > DH[bestI][bestJ]))) {
                    bestI = i;
                    bestJ = j;
                }
            }
        }
        LinkedList<Sudoku> res = new LinkedList<>();
        for (int k = 0; k < grid.length; k++) {
            if (values[bestI][bestJ][k]) {
                int[][] nGrid = deepClone(grid);
                nGrid[bestI][bestJ] = k + 1;
                res.add(new Sudoku(nGrid, activateSqares));
            }
        }
        return res;
    }

    public void reset() {
        solvedGrid = new int[grid.length][grid.length];
    }
}
