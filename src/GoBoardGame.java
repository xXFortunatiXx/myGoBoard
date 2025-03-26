/*
 * Thomas Fortunati
 * 3/25/2025
 */

import java.util.Scanner;

public class GoBoardGame {
    private static final int boardSize = 9;
    private static char[][] gameBoard = new char[boardSize][boardSize];
    private static int playerPassCount = 0;
    private static int playerOneScore = 0;
    private static int playerTwoScore = 7;

    public static void main(String[] args) {
        Scanner userInput = new Scanner(System.in);
        resetOrInitializeBoard();
        gameLoop(userInput, 1);
        userInput.close();
    }

    private static void gameLoop(Scanner userInput, int currentPlayer) {
        screenSpaceClear();
        displayIntro();
        printBoard();

        while (true) {
            System.out.println("Player " + (currentPlayer == 1 ? "X" : "O") + ", enter your move (x y) or -1 to pass: ");
            Integer moveX = getValidIntegerInput(userInput);
            if (moveX == null) continue;

            if (moveX == -1) {
                playerPassCount++;
                System.out.println("Player " + (currentPlayer == 1 ? "X" : "O") + " has passed.");

                if (playerPassCount == 2) {
                    boolean gameEnd = true;
                    checkForWin(gameEnd);
                    break;
                }
                currentPlayer = (currentPlayer == 1) ? 2 : 1;
                continue;
            }

            Integer moveY = getValidIntegerInput(userInput);
            if (moveY == null) continue;

            moveX--;
            moveY--;

            if (validateMove(moveX, moveY, currentPlayer)) {
                applyMove(moveX, moveY, currentPlayer);
                
                handleCaptures(moveX, moveY, currentPlayer);
                
                playerPassCount = 0;
                currentPlayer = (currentPlayer == 1) ? 2 : 1;
                screenSpaceClear();
                displayIntro();

                printBoard();
                displayScore();
                
                
            }
        }
    }

    private static void handleCaptures(int x, int y, int currentPlayer) {
        //check for enemy
        char opponent = (currentPlayer == 1) ? 'O' : 'X';
        //check all 4 directions
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        for (int[] dir : directions) {
            int nx = x + dir[0];
            int ny = y + dir[1];

            if (nx >= 0 && nx < boardSize && ny >= 0 && ny < boardSize && gameBoard[ny][nx] == opponent) {

                boolean[][] visited = new boolean[boardSize][boardSize];
                if (!canBreathe(nx, ny, opponent, visited)) {

                    //debug check
                    System.out.println("Attempting to remove at: " + nx + "," + ny);
                    System.out.println("Board value there: " + gameBoard[ny][nx] + " | Expected: " + opponent);
                    //removeGroup & checkForScore need own array because canBreath alters visited
                    boolean[][] removalVisited = new boolean[boardSize][boardSize];
                    removeGroup(nx, ny, opponent, removalVisited);
                    checkForScore(nx, ny, opponent, removalVisited);
                    
                    
                }
            }
        }
    }

    private static void removeGroup(int x, int y, char playerChar, boolean[][] visited) {
        if (x < 0 || x >= boardSize || y < 0 || y >= boardSize) return;
        if (visited[y][x]) return;
        if (gameBoard[y][x] != playerChar) return;
        

        visited[y][x] = true;
        
        gameBoard[y][x] = '+';
        System.out.println("Removing: " + x + "," + y);
    
        removeGroup(x + 1, y, playerChar, visited);
        removeGroup(x - 1, y, playerChar, visited);
        removeGroup(x, y + 1, playerChar, visited);
        removeGroup(x, y - 1, playerChar, visited);
    }
    

    private static boolean canBreathe(int x, int y, char playerChar, boolean[][] visited) {
        if (x < 0 || x >= boardSize || y < 0 || y >= boardSize) return false;
        if (visited[y][x]) return false;

        visited[y][x] = true;

        if (gameBoard[y][x] == '+') return true;
        if (gameBoard[y][x] != playerChar) return false;
        
        return
            canBreathe(x + 1, y, playerChar, visited) ||
            canBreathe(x - 1, y, playerChar, visited) ||
            canBreathe(x, y + 1, playerChar, visited) ||
            canBreathe(x, y - 1, playerChar, visited);

    }

    private static Integer getValidIntegerInput(Scanner input) {
        
        if (!input.hasNextInt()) {
            System.out.println("Invalid input! Please enter an integer.");
            input.next();
            return null;
        }
        return input.nextInt();
    }

    private static void screenSpaceClear() {

        for (int i = 0; i < 20; i++) {
            System.out.println();
        }
    }

    private static void displayIntro() {
        System.out.println(
            "==================================================================\n" +
            "                    Welcome to the Go Board Game!                 \n" +
            "                     Player 1: X    Player 2: O                   \n" +
            "       To make a move, enter the x and y coordinates (1-9).       \n" +
            "         The player with the most pieces on the board wins!       \n" +
            "                       =To pass, enter -1 for x                   \n" +
            "         If both players pass consecutively, the game ends!       \n" +
            "------------------------------ v0.14 DEBUG ------------------------\n" +
            "==================================================================\n"
        );
    }

    private static void resetOrInitializeBoard() {

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                gameBoard[row][col] = '+';
            }
        }
    }

    private static void printBoard() {

        System.out.print("   ");
        for (int col = 0; col < boardSize; col++) {
            System.out.printf("%2d ", col + 1);
        }
        System.out.println();

        for (int row = 0; row < boardSize; row++) {
            System.out.printf("%2d ", row + 1);
            for (int col = 0; col < boardSize; col++) {
                System.out.print(" " + gameBoard[row][col] + " ");
            }
            System.out.println();
        }
    }

    private static char determinePlayer(int currentPlayer) {

        return (currentPlayer == 1) ? 'X' : 'O';
    }

    private static boolean validateMove(int x, int y, int currentPlayer) {

        if ((x < 0 || x >= boardSize) || (y < 0 || y >= boardSize)) {
            System.out.println("Invalid move! Coordinates must be between 1 and " + boardSize + ".");
            return false;
        }

        if (gameBoard[y][x] != '+') {
            System.out.println("Invalid move! That spot is already taken.");
            return false;
        }

        boolean hasLiberty = false;
        char playerChar = determinePlayer(currentPlayer);

        if (x > 0 && (gameBoard[y][x - 1] == '+' || gameBoard[y][x - 1] == playerChar)) hasLiberty = true;
        if (x < boardSize - 1 && (gameBoard[y][x + 1] == '+' || gameBoard[y][x + 1] == playerChar)) hasLiberty = true;
        if (y > 0 && (gameBoard[y - 1][x] == '+' || gameBoard[y - 1][x] == playerChar)) hasLiberty = true;
        if (y < boardSize - 1 && (gameBoard[y + 1][x] == '+' || gameBoard[y + 1][x] == playerChar)) hasLiberty = true;

        if (!hasLiberty) {
            System.out.println("Invalid move! No liberties.");
            return false;
        }

        return true;
    }

    private static void applyMove(int x, int y, int currentPlayer) {

        gameBoard[y][x] = determinePlayer(currentPlayer);
    }

    private static void displayScore() {
        System.out.println("Current Score: ");
        System.out.println("Player 1 Score: " + playerOneScore);
        System.out.println("Player 2 Score: " + playerTwoScore);
    }

    private static void checkForScore(int x, int y, char opponent, boolean[][] visited) {
        System.out.println("Captured a group!");
        int capturedPieces = 0;

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (visited[row][col]) {
                    capturedPieces++;
                }
            }
        }

        if (opponent == 'X') {
            playerTwoScore += capturedPieces;
            playerOneScore -= capturedPieces;
        } else {
            playerOneScore += capturedPieces;
            playerTwoScore -= capturedPieces;
        }
        if (playerOneScore < 0) playerOneScore = 0;
        if (playerTwoScore < 0) playerTwoScore = 0;

    }

    private static boolean checkForWin(boolean gameEnd) {
        if (gameEnd == true) {
            System.out.println("Game Over! Thanks for playing.");
            //winner is determined by score
            if (playerOneScore > playerTwoScore) {
                System.out.println("Player 1 wins!");
            } else if (playerTwoScore > playerOneScore) {
                System.out.println("Player 2 wins!");
            } else {
                System.out.println("It's a tie!");
            }
            return true; // Game has ended
        }
        System.out.println("Checking for win...");
        // Placeholder for win logic
        return false; // Game has not ended
    }

}
// Compile: javac -d bin src/GoBoardGame.java
// Run:     java -cp bin GoBoardGame

/* Git workflow:
git add .
git commit -m "v0.x - brief description of your changes"
git push origin main
*/