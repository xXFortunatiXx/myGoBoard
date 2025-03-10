/*
 * Thomas Fortunati
 * 2/8/2025
 * This program is my current best attempt at the Go Board Game. It is not complete.
 */

 import java.util.Scanner; // Import scanner class for user input

 public class GoBoardGame {
     private static int boardSize = 9;
     private static char[][] gameBoard = new char[boardSize][boardSize];
     private static int playerPassCount = 0;
     private static int currentPlayer = 0;
 
     private static void initializeBoard() {
         for (int row = 0; row < boardSize; row++) {
             for (int col = 0; col < boardSize; col++) {
                 gameBoard[row][col] = '+';
             }
         }
     }
 
     private static void printBoard() {
         System.out.print("  ");
         for (int col = 0; col < boardSize; col++) {
             System.out.print("|" + (col + 1) + "|");
         }
         System.out.println();
 
         for (int row = 0; row < boardSize; row++) {
             
             System.out.print((row + 1) + "  ");
             
             for (int col = 0; col < boardSize; col++) {
                 System.out.print(gameBoard[row][col] + "  ");
             }
             
             System.out.println();
         }
     }
 
     private static boolean validateMove(int x, int y) {
         if (x < 0 || x >= boardSize || y < 0 || y >= boardSize) {
             System.out.println("Invalid move! Coordinates must be between 1 and 9.");
             return false;
         }
         if (gameBoard[y][x] != '+') {
             System.out.println("Invalid move! That spot is already taken.");
             return false;
         }
         return true;
     }
 
     private static void removePieces(int x, int y) {
        char currentPiece = gameBoard[y][x];
        char opponentPiece = (currentPiece == 'X') ? 'O' : 'X';
    
        // Future: Implement recursion for group capture? We should look up rules for group capture, im not super sure how this will work but i have an inclination
    
        // Check if the piece is on the edge and has no liberties
        if (x == 0 || x == boardSize || y == 0 || y == boardSize) {             // Check if the piece is on the edge
            boolean noLeft  = (x == 0) || (x > 0 && gameBoard[y][x - 1] != '+'); // if x is 0 or x is greater than 0 and the piece to the left is not empty
            boolean noRight = (x == boardSize - 1) || (x < boardSize - 1 && gameBoard[y][x + 1] != '+');
            boolean noUp    = (y == 0) || (y > 0 && gameBoard[y - 1][x] != '+');
            boolean noDown  = (y == boardSize - 1) || (y < boardSize - 1 && gameBoard[y + 1][x] != '+');

            if (noLeft && noRight && noUp && noDown) {
                gameBoard[y][x] = '+'; // Remove captured piece
                System.out.println("Piece at (" + (x + 1) + ", " + (y + 1) + ") was captured!");
                return;
            }
        }
    
        // Check for full surround capture in the middle of the board
        if (x > 0 && x < boardSize - 1 && y > 0 && y < boardSize - 1) {
            boolean surrounded =
                (gameBoard[y][x - 1] == opponentPiece) &&
                (gameBoard[y][x + 1] == opponentPiece) &&
                (gameBoard[y - 1][x] == opponentPiece) &&
                (gameBoard[y + 1][x] == opponentPiece);
    
            if (surrounded) {
                gameBoard[y][x] = '+'; // Remove surrounded piece
                System.out.println("Piece at (" + (x + 1) + ", " + (y + 1) + ") was captured!");
            }
        }
    }
    
 
     private static void checkBoardForCaptures() {
         for (int y = 0; y < boardSize; y++) {
             for (int x = 0; x < boardSize; x++) {
                 removePieces(x, y);
             }
         }
     }
 
     private static void gameLoop(Scanner userInput) {
         while (true) {
             System.out.println("Player " + (currentPlayer + 1) + "'s turn:");
 
             while (true) {
                 System.out.print("Enter the x and y coordinate (1-9 or -1 to pass): ");
                 int moveX = userInput.nextInt();
 
                 if (moveX == -1) {
                     playerPassCount++;
                     System.out.println("Player " + (currentPlayer + 1) + " passes!\n");
                     break;
                 }
                 int moveY = userInput.nextInt();
                 
                 // Convert user input to array index
                 moveX -= 1;
                 moveY -= 1;
 
                 if (validateMove(moveX, moveY)) {
                     gameBoard[moveY][moveX] = (currentPlayer == 0) ? 'X' : 'O';
                     playerPassCount = 0;
 
                     checkBoardForCaptures();
                     break;
                 }
             }
 
             printBoard();
 
             if (playerPassCount == 2) {
                 System.out.println("Both players have passed!");
                 break;
             }
 
             currentPlayer = 1 - currentPlayer;
         }
     }
 
     public static void main(String[] args) {
        Scanner userInput = new Scanner(System.in);
        initializeBoard(); // prints the starting board
        System.out.println( // prints the rules of the game
            "==================================================================\n" +
            "                    Welcome to the Go Board Game!                 \n" +
            "                     Player 1: X    Player 2: O                   \n" +
            "       To make a move, enter the x and y coordinates (1-9).       \n" +
            "         The player with the most pieces on the board wins!       \n" +
            "                       =To pass, enter -1 for x                   \n" +
            "         If both players pass consecutively, the game ends!       \n" +
            "==================================================================\n"
        );
        printBoard(); // prints the board for updating
        gameLoop(userInput); // majority of the game logic
        userInput.close();
        System.out.println("Game Over! Thanks for playing.");
    }
}
