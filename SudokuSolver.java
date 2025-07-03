import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SudokuSolver {
    private static final int SIZE = 9;
    private JFrame frame;
    private JTextField[][] cells = new JTextField[SIZE][SIZE];
    private int[][] board = new int[SIZE][SIZE];

    public SudokuSolver() {
        frame = new JFrame("Sudoku Solver");
        frame.setSize(500, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(SIZE, SIZE));

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                final int row = i;
                final int col = j;

                cells[row][col] = new JTextField();
                cells[row][col].setHorizontalAlignment(JTextField.CENTER);
                cells[row][col].setFont(new Font("Arial", Font.BOLD, 20));

                int top = (row == 0) ? 3 : (row % 3 == 0) ? 2 : 1;
                int left = (col == 0) ? 3 : (col % 3 == 0) ? 2 : 1;
                int bottom = (row == SIZE - 1) ? 3 : 1;
                int right = (col == SIZE - 1) ? 3 : 1;

                cells[row][col].setBorder(new MatteBorder(top, left, bottom, right, Color.BLACK));

                cells[row][col].addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        char c = e.getKeyChar();
                        if (!Character.isDigit(c) || c == '0' || cells[row][col].getText().length() >= 1) {
                            e.consume();
                        }
                    }
                });

                cells[row][col].addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        switch (e.getKeyCode()) {
                            case KeyEvent.VK_UP -> moveToCell(row - 1, col);
                            case KeyEvent.VK_DOWN -> moveToCell(row + 1, col);
                            case KeyEvent.VK_LEFT -> moveToCell(row, col - 1);
                            case KeyEvent.VK_RIGHT -> moveToCell(row, col + 1);
                        }
                    }
                });

                gridPanel.add(cells[row][col]);
            }
        }

        frame.add(gridPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));

        JButton solveButton = new JButton("Solve");
        solveButton.addActionListener(e -> solveSudoku());
        buttonPanel.add(solveButton);

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearBoard());
        buttonPanel.add(clearButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void moveToCell(int row, int col) {
        if (row >= 0 && row < SIZE && col >= 0 && col < SIZE) {
            cells[row][col].requestFocus();
        }
    }

    private boolean isValid(int row, int col, int num) {
        for (int i = 0; i < SIZE; i++) {
            if (board[row][i] == num || board[i][col] == num ||
                board[row - row % 3 + i / 3][col - col % 3 + i % 3] == num) {
                return false;
            }
        }
        return true;
    }

    private boolean solveWithTimeout(long startTime, long timeoutMillis) {
        if (System.currentTimeMillis() - startTime > timeoutMillis) {
            return false;
        }

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] == 0) {
                    for (int num = 1; num <= SIZE; num++) {
                        if (isValid(row, col, num)) {
                            board[row][col] = num;
                            if (solveWithTimeout(startTime, timeoutMillis)) return true;
                            board[row][col] = 0;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private void solveSudoku() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                try {
                    board[i][j] = Integer.parseInt(cells[i][j].getText());
                } catch (NumberFormatException e) {
                    board[i][j] = 0;
                }
            }
        }

        long startTime = System.currentTimeMillis();
        long timeoutMillis = 30000; // 30 seconds

        if (solveWithTimeout(startTime, timeoutMillis)) {
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    cells[i][j].setText(String.valueOf(board[i][j]));
                }
            }
        } else {
            JOptionPane.showMessageDialog(frame, "No solution exists or solving took longer than 30 seconds.");
        }
    }

    private void clearBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                cells[i][j].setText("");
                board[i][j] = 0;
            }
        }
    }

    public static void main(String[] args) {
        new SudokuSolver();
    }
}
