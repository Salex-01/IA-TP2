import java.util.Random;

public class GeneratorMain {
    public static void main(String[] args) {
        int size = Integer.parseInt(args[0]);
        boolean squares = args[1].toLowerCase().startsWith("t");
        if (Math.sqrt(size) == (int) Math.sqrt(size)) {
            int root = (int) Math.sqrt(size);
            int[][] grid = new int[size][size];
            Sudoku s = new Sudoku(grid, squares);
            grid = s.solve(0, "");
            int limit = (int) (size * Math.sqrt(size));
            System.out.println(limit);
            Random r = new Random();
            int a;
            int b;
            int c;
            boolean mode;
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
            SudokuUtils.printSudoku(grid);
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
