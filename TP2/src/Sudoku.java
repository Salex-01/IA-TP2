import java.util.LinkedList;

public class Sudoku {
    int[][] grid;
    int[][] solvedGrid;
    boolean[][][] possibilities;
    int squareSize;
    boolean activateSqares;
    int constraints;
    int minCst;

    static int[][] deepClone(int[][] array) {
        int[][] res = new int[array.length][array[0].length];
        for (int i = 0; i < array.length; i++) {
            System.arraycopy(array[i], 0, res[i], 0, array[0].length);
        }
        return res;
    }

    static boolean[][][] deepClone(boolean[][][] array) {
        boolean[][][] res = new boolean[array.length][array[0].length][array[0][0].length];
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                System.arraycopy(array[i][j], 0, res[i][j], 0, array[0][0].length);
            }
        }
        return res;
    }

    public Sudoku(int[][] g, boolean s) {
        this(g, s, null, 0, true, 0, 0);
    }

    public Sudoku(int[][] g, boolean s, boolean[][][] poss, int minC, boolean computeConstraints, int a, int b) {
        grid = g;
        solvedGrid = new int[grid.length][grid.length];
        activateSqares = s;
        squareSize = (int) Math.sqrt(g.length);
        if (poss == null) {
            possibilities = new boolean[grid.length][grid.length][grid.length];
            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid.length; j++) {
                    for (int k = 0; k < grid.length; k++) {
                        possibilities[i][j][k] = ((grid[i][j] == 0) || (grid[i][j] == k + 1));
                    }
                }
            }
        } else {
            possibilities = poss;
            for (int i = 0; i < grid.length; i++) {
                possibilities[a][b][i] = (grid[a][b] == i + 1);
            }
        }
        AC3();
        if (computeConstraints) {
            minCst = computeMaxConstraints();
            constraints = minC - minCst;
        }
    }

    private void AC3() {
        LinkedList<Constraint> cstr = new LinkedList<>();
        for (int j = 0; j < grid.length; j++) {
            for (int i = 0; i < grid.length - 1; i++) {
                for (int k = i + 1; k < grid.length; k++) {
                    cstr.add(new Constraint(i, j, k, j));
                    cstr.add(new Constraint(k, j, i, j));
                    cstr.add(new Constraint(j, i, j, k));
                    cstr.add(new Constraint(j, k, j, i));
                }
            }
        }
        if (activateSqares) {
            for (int baseI = 0; baseI < grid.length; baseI += squareSize) {
                for (int baseJ = 0; baseJ < grid.length; baseJ += squareSize) {
                    for (int k = 0; k < grid.length - 1; k++) {
                        for (int l = 1; l < grid.length; l++) {
                            int i1 = k % squareSize;
                            int j1 = k / squareSize;
                            int i2 = l % squareSize;
                            int j2 = l / squareSize;
                            checkAndAdd(cstr, new Constraint(baseI + i1, baseJ + j1, baseI + i2, baseJ + j2));
                            checkAndAdd(cstr, new Constraint(baseI + i2, baseJ + j2, baseI + i1, baseJ + j1));
                        }
                    }
                }
            }
        }
        Constraint c;
        while (!cstr.isEmpty()) {
            c = cstr.remove(0);
            boolean tem1 = false;
            for (int si = 0; si < grid.length; si++) {
                if (possibilities[c.i1][c.j1][si]) {
                    boolean tem2 = false;
                    for (int ti = 0; ti < grid.length; ti++) {
                        if ((si != ti) && (possibilities[c.i2][c.j2][ti])) {
                            tem2 = true;
                            break;
                        }
                    }
                    if (!tem2) {
                        possibilities[c.i1][c.j1][si] = false;
                        tem1 = true;
                    }
                }
            }
            if (tem1) {
                LinkedList<Constraint> newcstr = new LinkedList<>();
                int baseI = (c.i1 / squareSize) * squareSize;
                int baseJ = (c.j1 / squareSize) * squareSize;
                for (int i = 0; i < grid.length; i++) {
                    if (i != c.i1) {
                        checkAndAdd(newcstr, new Constraint(i, c.j1, c.i1, c.j1));
                    }
                    if (i != c.j1) {
                        checkAndAdd(newcstr, new Constraint(c.i1, i, c.i1, c.j1));
                    }
                    int ni = i % squareSize;
                    int nj = i / squareSize;
                    if ((baseI + ni != c.i1) || (baseJ + nj != c.j1)) {
                        checkAndAdd(newcstr, new Constraint(baseI + ni, baseJ + nj, c.i1, c.j1));
                    }
                }
            }
        }
    }

    private void checkAndAdd(LinkedList<Constraint> list, Constraint c2) {
        if ((c2.i1 == c2.i2) && (c2.j1 == c2.j2)) {
            return;
        }
        for (Constraint c : list) {
            if ((c.i1 == c2.i1) && (c.j1 == c2.j1) && (c.i2 == c2.i2) && (c.j2 == c2.j2)) {
                return;
            }
        }
        list.add(c2);
    }

    private int computeMaxConstraints() {
        int result = grid.length;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                int tmp = 0;
                for (int k = 0; k < grid.length; k++) {
                    if (possibilities[i][j][k]) {
                        tmp++;
                    }
                }
                if (tmp < result) {
                    result = tmp;
                }
            }
        }
        return result;
    }

    public int[][] solve(int n, String m) {
        // Contraintes : les cases ayant une valeur dans grid doivent avoir la même valeur dans la solution -> grid[x][y]^2 == grid[x][y]*solvedGrid[x][y]
        // Contraintes : toutes les cases d'une même ligne/colonne ou d'un même carré doivent être différentes
        // Contraintes : toutes les cases doivent être comprises dans [1;n]
        // Backtracking -> MRV + DH + LCV -> AC3

        if (check()) {
            return grid;
        }

        LinkedList<Sudoku> children = generateChildren();

        // Rangement des enfants dans l'ordre de préférence LCV
        children.sort((o1, o2) -> o2.constraints - o1.constraints);
        int[][] solution;
        int a = 0;
        while (!children.isEmpty()) {
            System.out.println("D" + n + (n >= 100 ? "\tT" : "\t\tT") + m + a);
            solution = children.remove(0).solve(n + 1, m + a);
            if (solution != null) {
                solvedGrid = solution;
                return solution;
            }
            a++;
        }
        return null;
    }

    private boolean check() {
        for (int[] ints : grid) {
            for (int j = 0; j < grid.length; j++) {
                if (ints[j] == 0) {
                    return false;
                }
            }
        }
        for (int i = 0; i < grid.length; i++) {
            int sumH = 0;
            int sumV = 0;
            for (int j = 0; j < grid.length; j++) {
                sumH += grid[i][j];
                sumV += grid[j][i];
            }
            if ((sumH != sumV) || sumH != (grid.length + 1) * grid.length / 2) {
                throw new IllegalStateException();
            }
        }
        if (activateSqares) {
            for (int baseI = 0; baseI < grid.length; baseI += squareSize) {
                for (int baseJ = 0; baseJ < grid.length; baseJ += squareSize) {
                    int sumS = 0;
                    for (int k = 0; k < grid.length; k++) {
                        sumS += grid[baseI + (k % squareSize)][baseJ + (k / squareSize)];
                    }
                    if (sumS != (grid.length + 1) * grid.length / 2) {
                        throw new IllegalStateException();
                    }
                }
            }
        }
        return true;
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
                            for (int l = baseJ; l < baseJ + squareSize; l++) {
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
                res.add(new Sudoku(nGrid, activateSqares, deepClone(possibilities), minCst, true, bestI, bestJ));
            }
        }
        return res;
    }

    public Sudoku reset() {
        solvedGrid = new int[grid.length][grid.length];
        return this;
    }
}
