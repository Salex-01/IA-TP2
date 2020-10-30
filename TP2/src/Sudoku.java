import java.util.LinkedList;

public class Sudoku {
    int[][] grid;
    boolean[][][] possibilities;    // Indique pour chaque case quelles valeurs sont possibles
    int squareSize;                 // Taille des carrés internes (3x3 pour un sudoku 9x9)
    boolean activateSqares;         // Permet de désactiver les contraintes de carrés internes
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
        this(g, s, null, 0, 0, 0);
    }

    public Sudoku(int[][] g, boolean s, boolean[][][] poss, int minC, int a, int b) {
        grid = g;
        activateSqares = s;
        squareSize = (int) Math.sqrt(g.length);
        if (poss == null) { // Si la grille des possibilités n'existe pas encore
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
        AC_3();
        // Calcul du score de contrainte utilisé par LCV pour choisir l'ordre d'exploration des enfants
        minCst = computeMaxConstraints();
        constraints = minC - minCst;
    }

    private void AC_3() {
        // Les contraintes sont générées automatiquement
        LinkedList<Constraint> cstr = new LinkedList<>();
        // Contraintes sur les lignes et les colonnes
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
        // Contraintes sur les carrés
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
        // Algorithme AC-3 proprement dit
        while (!cstr.isEmpty()) {
            c = cstr.remove(0);
            boolean tem1 = false;
            // Pour chaque valeur de la source
            for (int si = 0; si < grid.length; si++) {
                if (possibilities[c.sourceI][c.sourceJ][si]) {
                    boolean tem2 = false;
                    for (int ti = 0; ti < grid.length; ti++) {
                        // Si on trouve une valeur de la cible qui satisfait la contrainte
                        if ((si != ti) && (possibilities[c.targetI][c.targetJ][ti])) {
                            tem2 = true;
                            break;
                        }
                    }
                    // Si on a trouvé aucune valeur satisfaisante
                    if (!tem2) {
                        // On supprime la valeur pour la source
                        possibilities[c.sourceI][c.sourceJ][si] = false;
                        tem1 = true;
                    }
                }
            }
            // Si on a supprimé une valeur
            if (tem1) {
                int baseI = (c.sourceI / squareSize) * squareSize;
                int baseJ = (c.sourceJ / squareSize) * squareSize;
                for (int i = 0; i < grid.length; i++) {
                    // Contraintes sur les lignes
                    if (i != c.sourceI) {
                        checkAndAdd(cstr, new Constraint(i, c.sourceJ, c.sourceI, c.sourceJ));
                    }
                    // Contraintes sur les colonnes
                    if (i != c.sourceJ) {
                        checkAndAdd(cstr, new Constraint(c.sourceI, i, c.sourceI, c.sourceJ));
                    }
                    // Contraintes sur les carrés
                    if (activateSqares) {
                        int ni = i % squareSize;
                        int nj = i / squareSize;
                        if ((baseI + ni != c.sourceI) || (baseJ + nj != c.sourceJ)) {
                            checkAndAdd(cstr, new Constraint(baseI + ni, baseJ + nj, c.sourceI, c.sourceJ));
                        }
                    }
                }
            }
        }
    }

    // Ajoute la contrainte à condition qu'elle n'existe pas encore et que la source et la cible soient différentes
    private void checkAndAdd(LinkedList<Constraint> list, Constraint c2) {
        if ((c2.sourceI == c2.targetI) && (c2.sourceJ == c2.targetJ)) {
            return;
        }
        for (Constraint c : list) {
            if ((c.sourceI == c2.sourceI) && (c.sourceJ == c2.sourceJ) && (c.targetI == c2.targetI) && (c.targetJ == c2.targetJ)) {
                return;
            }
        }
        list.add(c2);
    }

    // Retourne le nombre de valeur possibles pour la case en ayant le moins
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

    public int[][] solve(int n, String m, boolean debug, boolean depth) {
        // Contraintes : toutes les cases d'une même ligne/colonne doivent être différentes
        // Contraintes : si activateSquares, toutes les cases d'un même carré doivent être différentes
        // Contraintes : toutes les cases doivent être comprises dans [1;n]

        if (check()) {
            return grid;
        }

        // Choisit la case à assigner selon la méthode MRV+degree heuristic et génère les enfants correspondants
        LinkedList<Sudoku> children = generateChildren();

        // Rangement des enfants dans l'ordre de préférence LCV
        children.sort((o1, o2) -> o2.constraints - o1.constraints);

        int[][] solution;
        int a = 0;
        while (!children.isEmpty()) {
            if (debug) {
                a++;
                System.out.println("D" + n + (n >= 100 ? "\tT" : "\t\tT") + m + a);
            } else if (depth) {
                System.out.println("D" + n);
            }

            // Appel récursif à solve sur les enfants
            solution = children.remove(0).solve(n + 1, m + a, debug, depth);

            // Si on a une solution
            if (solution != null) {
                return solution;
            }
        }
        return null;
    }

    // Vérifie si la grille est complète et si ses valeurs sont cohérentes
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
            if ((sumH != sumV) || sumH != (grid.length + 1) * grid.length / 2) {    // Théoriquement impossible
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
                    if (sumS != (grid.length + 1) * grid.length / 2) {  // Théoriquement impossible
                        throw new IllegalStateException();
                    }
                }
            }
        }
        return true;
    }

    // Génère les enfants produits par l'assignation de la case choisie par MRV et DH
    private LinkedList<Sudoku> generateChildren() {
        // Nombre de valeurs possibles pour chaque case, ou taille+1 pour les cases déjà affectées
        int[][] RV = new int[grid.length][grid.length];
        // Nombre de contraintes vers des cases pas encore affectées
        int[][] DH = new int[grid.length][grid.length];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                if (grid[i][j] != 0) {
                    RV[i][j] = grid.length + 1;
                } else {
                    RV[i][j] = 0;
                    DH[i][j] = 0;
                    int baseI = (i / squareSize) * squareSize;
                    int baseJ = (j / squareSize) * squareSize;
                    for (int k = 0; k < grid.length; k++) {
                        if (possibilities[i][j][k]) {
                            RV[i][j]++;
                        }
                        if ((k != j) && (grid[i][k] == 0)) {
                            DH[i][j]++;
                        }
                        if ((k != i) && (grid[k][j] == 0)) {
                            DH[i][j]++;
                        }
                        if (activateSqares) {
                            int l = k % squareSize;
                            int m = k / squareSize;
                            if (((baseI + l != i) || (baseJ + m != j)) && (grid[baseI + l][baseJ + m] == 0)) {
                                DH[i][j]++;
                            }
                        }
                    }

                }
            }
        }
        // Choix de la case à affecter en fonction de son nombre de valeurs possibles
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
        // Création des enfants
        LinkedList<Sudoku> res = new LinkedList<>();
        for (int k = 0; k < grid.length; k++) {
            if (possibilities[bestI][bestJ][k]) {
                int[][] nGrid = deepClone(grid);
                nGrid[bestI][bestJ] = k + 1;
                res.add(new Sudoku(nGrid, activateSqares, deepClone(possibilities), minCst, bestI, bestJ));
            }
        }
        return res;
    }
}