import java.util.Random;

public class GeneratorMain {
    public static void main(String[] args) {
        // Récupère les arguments
        // Le premier correspond à la taille du sudoku à générer
        // Le deuxième correspond à l'attribut activateSquares
        // Si activateSquares est vrai, size doit être un carré parfait
        int size = 9;
        boolean squares = true;
        boolean debug = false;
        for (int i = 0; i < args.length; i += 2) {
            switch (args[i]) {
                case "size":
                    size = Integer.parseInt(args[i + 1]);
                    break;
                case "sqr":
                    squares = args[i + 1].toLowerCase().startsWith("t");
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
        if ((!squares) || (Math.sqrt(size) == (int) Math.sqrt(size))) {
            Random r = new Random();
            int root = (int) Math.sqrt(size);
            int[][] grid = new int[size][size];
            grid[r.nextInt(size)][r.nextInt(size)] = r.nextInt(size) + 1;
            Sudoku s = new Sudoku(grid, squares);
            grid = s.solve(0, "", false, debug);
            int limit = (int) (size * Math.sqrt(size));
            int a;
            int b;
            int c;
            boolean mode;
            if (squares) {
                for (int i = 0; i < size; i++) {
                    do {
                        a = r.nextInt(root);
                        b = r.nextInt(root);
                        mode = r.nextBoolean();
                    } while (a == b);
                    for (int j = 0; j < root; j++) {
                        swap(size, root, grid, a, j, j, mode, b * root);
                    }
                }
                for (int i = 0; i < size * size; i++) {
                    do {
                        a = r.nextInt(root);
                        b = r.nextInt(root);
                        c = r.nextInt(root);
                        mode = r.nextBoolean();
                    } while (b == c);
                    swap(size, root, grid, a, b, c, mode, a * root);
                }
            } else {
                for (int i = 0; i < size * size; i++) {
                    do {
                        a = r.nextInt(size);
                        b = r.nextInt(size);
                        mode = r.nextBoolean();
                    } while (a == b);
                    if (mode) {
                        int[] tmp = grid[a];
                        grid[a] = grid[b];
                        grid[b] = tmp;
                    } else {
                        int tmp;
                        for (int j = 0; j < size; j++) {
                            tmp = grid[j][a];
                            grid[j][a] = grid[j][b];
                            grid[j][b] = tmp;
                        }
                    }
                }
            }
            SudokuUtils.printSudoku(grid);
            System.out.println();
            int x;
            int y;
            for (int j = 0; j < limit; j++) {
                do {
                    x = r.nextInt(size);
                    y = r.nextInt(size);
                } while (grid[x][y] < 0);
                grid[x][y] *= -1;
            }
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    if (grid[j][k] < 0) {
                        grid[j][k] *= -1;
                    } else {
                        grid[j][k] = 0;
                    }
                }
            }
            SudokuUtils.printSudoku(grid);
        } else {
            System.out.println("Illegal Argument : size must be a square number");
        }

    }

    private static void swap(int size, int root, int[][] grid, int a, int b, int c, boolean mode, int i2) {
        if (mode) {
            int[] tmp = grid[a * root + b];
            grid[a * root + b] = grid[i2 + c];
            grid[i2 + c] = tmp;
        } else {
            int tmp;
            for (int j = 0; j < size; j++) {
                tmp = grid[j][a * root + b];
                grid[j][a * root + b] = grid[j][i2 + c];
                grid[j][i2 + c] = tmp;
            }
        }
    }
}