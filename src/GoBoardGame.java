/*
 * Thomas Fortunati
 * 3/11/2025
 */

import java.util.Scanner;

public class GoBoardGame {
    private static final int boardSize = 9;
    private static char[][] gameBoard = new char[boardSize][boardSize];
    private static int playerPassCount = 0;
    
    public static void main(String[] args) {
        Scanner userInput = new Scanner(System.in);
        resetOrInitializeBoard(); // Initialize or reset the game board
        gameLoop(userInput, 1);
        userInput.close();
        System.out.println("Game Over! Thanks for playing.");
    }

    //working
    private static void gameLoop(Scanner userInput, int currentPlayer) {
        screenSpaceClear();
        displayIntro();
        printBoard();

        while (true) {
            checkBoardForCaptures(determinePlayer(currentPlayer));
            System.out.println("Player " + (currentPlayer == 1 ? "X" : "O") + ", enter your move (x y) or -1 to pass: ");
            Integer moveX = getValidIntegerInput(userInput);
            if (moveX == null) continue;

            if (moveX == -1) {
                playerPassCount++;
                System.out.println("Player " + (currentPlayer == 1 ? "X" : "O") + " has passed.");

                if (playerPassCount == 2) {
                    System.out.println("Both players have passed. Game over!");
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
                playerPassCount = 0;
                currentPlayer = (currentPlayer == 1) ? 2 : 1;
                screenSpaceClear();
                displayIntro();
                printBoard();

            }
        }
    }

    //working
    private static Integer getValidIntegerInput(Scanner input) {
        if (!input.hasNextInt()) {
            System.out.println("Invalid input! Please enter an integer.");
            input.next(); 
            return null;
        }
        return input.nextInt();
    }

    //formatting working
    private static void screenSpaceClear() {
        for (int i = 0; i < 20; i++) {
            System.out.println();
        }
    }

    //working
    private static void displayIntro() {
        System.out.println(
            "==================================================================\n" +
            "                    Welcome to the Go Board Game!                 \n" +
            "                     Player 1: X    Player 2: O                   \n" +
            "       To make a move, enter the x and y coordinates (1-9).       \n" +
            "         The player with the most pieces on the board wins!       \n" +
            "                       =To pass, enter -1 for x                   \n" +
            "         If both players pass consecutively, the game ends!       \n" +
            "------------------------------ v0.9 DEBUG ------------------------\n" +
            "==================================================================\n"
        );
    }

    //working
    private static void resetOrInitializeBoard() {
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                gameBoard[row][col] = '+';
            }
        }
    }

    //working board formatting and updating
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

    //working
    private static char determinePlayer(int currentPlayer) {
        return (currentPlayer == 1) ? 'X' : 'O';
    }

    //working
    private static boolean validateMove(int x, int y, int currentPlayer) {

        if ((x < 0 || x >= boardSize) || (y < 0 || y >= boardSize)) {
            System.out.println("Invalid move! Coordinates must be between 1 and " + boardSize + ".");
            return false;
        }

        if (gameBoard[y][x] != '+') {
            System.out.println("Invalid move! That spot is already taken.");
            return false;
        }
        //liberty check maybe make separate method
        boolean hasLiberty = false;
        char playerChar = determinePlayer(currentPlayer);

        if (x > 0             && (gameBoard[y][x - 1] == '+' || gameBoard[y][x - 1] == playerChar)) hasLiberty = true;
        if (x < boardSize - 1 && (gameBoard[y][x + 1] == '+' || gameBoard[y][x + 1] == playerChar)) hasLiberty = true;
        if (y > 0             && (gameBoard[y - 1][x] == '+' || gameBoard[y - 1][x] == playerChar)) hasLiberty = true;
        if (y < boardSize - 1 && (gameBoard[y + 1][x] == '+' || gameBoard[y + 1][x] == playerChar)) hasLiberty = true;

        if (!hasLiberty) {
            System.out.println("Invalid move! No liberties.");
            return false;
        }

        return true;
    }
    
    //working
    private static void applyMove(int x, int y, int currentPlayer) {
        gameBoard[y][x] = determinePlayer(currentPlayer);
    }

    //in progress
    private static void checkBoardForCaptures(char currentPlayer) {
        
        // Placeholder for capture logic
        //we want a 2D array to keep track of the board from top to bottom for analysis, this way we can at least determine the probability of a player winning
        //we can also use this to determine if a player has won as it will know how many pieces are on the board
        // for an
        System.out.println("Checking for captures...");
    }

    //in progress
    private static boolean canBreathe(int x, int y, char playerChar, boolean[][] visited) {
            //i think i need something like if false, remove pieces
            if (x < 0 || x >= boardSize || y < 0 || y >= boardSize) return false;
        
        
        
            visited[y][x] = true; 
        
            if (gameBoard[y][x] == '+') return true;
        
            if (gameBoard[y][x] != playerChar) return false;
        
            return canBreathe(x + 1, y, playerChar, visited) ||
                canBreathe(x - 1, y, playerChar, visited) ||
                canBreathe(x, y + 1, playerChar, visited) ||
                canBreathe(x, y - 1, playerChar, visited);
    }

    //in progress
    private static boolean captureGroup(int x, int y, char piece, boolean[][] visited) {
        if (x < 0 || x >= boardSize || y < 0 || y >= boardSize || visited[y][x]) return false;

        visited[y][x] = true;

        if (gameBoard[y][x] == '+') return false;
        if (gameBoard[y][x] != piece) return true;

        boolean captured = true;
        captured &= captureGroup(x + 1, y, piece, visited);
        captured &= captureGroup(x - 1, y, piece, visited);
        captured &= captureGroup(x, y + 1, piece, visited);
        captured &= captureGroup(x, y - 1, piece, visited);

        if (captured) gameBoard[y][x] = '+';
        return captured;
    }


    //in progress
    private static void removePieces() {
        boolean[][] visited = new boolean[boardSize][boardSize];
        if (captureGroup(0, 0, determinePlayer(1), visited)) {
            System.out.println("Group captured!");
        }
        // Placeholder for piece removal logic
    }

    //in progress
    private static void checkForWin() {
        // Placeholder for win condition logic
        //1.win conditions 
        //player has most combined points from capturing pieces and having territory
        //player two starts with 7 points to make up for going second

        //if both players pass consecutively, the game ends
        // --> if yes, count pieces

        //count pieces on board for each player
        //--> player x total pieces
        //--> player o total pieces

        //determine winner
        //--> if player x > player o, player x wins
        //--> if player o > player x, player o wins
        //--> if player x == player o, tie game

        //print winner
        //winner anouncement 
        //final board state
        //piece count summary for each player
        //probability of winning for each player

    }
    //in progress
    private static void probabilityOfWin(boolean[][] visited) {
        // Placeholder for probability of win logic
        /*an iterative loop to scan a 2D array from top to botton
         * row & col
         * the loop will scan the board and use recursion to determine the probability of a player winning
         * the recursion will check connected pieces of a players pieces and determine theyre captured territory
         * the recursion could further split into groups for probability of winning
         * so we have one score on board for each player 
         * 1. check for connected pieces
         * 2.calcualte the probability of winning based on conncted pieces and liberties
         * 3. example: if a player has 3 connected pieces and 2 liberties, the probability of winning is 66%
         * 4. so we take the combined prob of each players territory and output the current probability of winning
         * 5. we can also use this to determnine the possible number of moves for each player to win
         * player A: 
         * 2 oppenent groups can be capture in 1 move
         * 1 group could be saved with 1 move
         * 
         * player B:
         * 1 group in atari
         * 0 imediate caputers
         * 
         * 
         * 
         * maybe for game it prints out like 
         * player A: 66% chance of winning
         * -2 groups: safe
         * -1 group: Warning
         * -1 group: Critical
         * player B: 33% chance of winning
         * -3 group: Warning
         * -2 groups: safe
         * 
         * critical: total possible lib 4 and current lib 1 = 75 - 100
         * warning: total possible lib 4 and current lib 2 = 50
         * safe: total possible lib 4 and current lib 4 = 0
         *  
         * visited method neeeded. once a group has been scanned it will be marked as visited and skipped in the future, this way we can avoid infinite loops so the whole board scanned is our base case
         * 
         */
    }

    //in progress
    private static void printVisited(boolean[][] visited) {
        System.out.println("Visited Map:");
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                System.out.print(visited[row][col] ? "1 " : ". ");
            }
            System.out.println();
        }
    }
    

    
}

//for compile javac -d bin src/GoBoardGame.java
//for run java -cp bin GoBoardGame

//for comit 
