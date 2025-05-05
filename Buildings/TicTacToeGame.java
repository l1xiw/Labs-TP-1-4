package Buildings;

import java.util.Random;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.util.logging.*;

public class TicTacToeGame {
    private char[][] board;
    private char playerSymbol;
    private char botSymbol;
    private boolean playerTurn;
    private int betAmount;

    // Логгер
    private static final Logger logger = Logger.getLogger(TicTacToeGame.class.getName());

    public TicTacToeGame(int betAmount) {
        this.board = new char[3][3];
        this.playerSymbol = 'X';
        this.botSymbol = 'O';
        this.betAmount = betAmount;
        initializeBoard();

        // Настройка логгера
        try {
            File logDir = new File("logs");
            if (!logDir.exists()) {
                logDir.mkdir();
            }
            FileHandler fileHandler = new FileHandler("logs/tic_tac_toe.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false); // Отключаем вывод в консоль
            logger.info("Игра в крестики-нолики создана. Ставка: " + betAmount);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка при настройке логгера", e);
        }
    }

    private void initializeBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = '-';
            }
        }
        logger.info("Игровое поле инициализировано.");
    }

    public int play() {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        logger.info("Начало игры в крестики-нолики.");

        // Определение первого хода через камень-ножницы-бумага
        while (true) {
            System.out.println("Камень (1), ножницы (2), бумага (3):");
            int playerChoice = scanner.nextInt();
            int botChoice = random.nextInt(3) + 1;

            logger.info("Игрок выбрал: " + playerChoice + ", Бот выбрал: " + botChoice);
            System.out.println("Бот выбрал: " + botChoice);

            if (playerChoice == botChoice) {
                logger.warning("Ничья в выборе первого хода. Повторите выбор.");
                System.out.println("Ничья! Повторите выбор.");
            } else {
                playerTurn = determineFirstTurn(playerChoice, botChoice);
                logger.info("Первым ходит: " + (playerTurn ? "Игрок" : "Бот"));
                break;
            }
        }

        // Основной цикл игры
        while (true) {
            printBoard();

            if (playerTurn) {
                playerMove(scanner);
            } else {
                botMove();
            }

            // Проверка победы после каждого хода
            if (checkWin(playerSymbol)) {
                printBoard();
                logger.info("Игрок выиграл!");
                System.out.println("Вы выиграли!");
                return betAmount;
            }
            if (checkWin(botSymbol)) {
                printBoard();
                logger.info("Бот выиграл!");
                System.out.println("Вы проиграли...");
                return -betAmount;
            }
            if (isBoardFull()) {
                printBoard();
                logger.warning("Ничья в крестиках-ноликах!");
                System.out.println("Ничья!");
                return 0;
            }

            playerTurn = !playerTurn;
        }
    }

    private boolean determineFirstTurn(int player, int bot) {
        return (player == 1 && bot == 2) ||
                (player == 2 && bot == 3) ||
                (player == 3 && bot == 1);
    }

    private void playerMove(Scanner scanner) {
        System.out.println("Введите строку (1-3) и столбец (1-3):");
        int row = scanner.nextInt() - 1;
        int col = scanner.nextInt() - 1;

        if (isValidMove(row, col)) {
            board[row][col] = playerSymbol;
            logger.info("Игрок сделал ход в ячейку (" + (row + 1) + ", " + (col + 1) + ")");
        } else {
            logger.severe("Некорректный ход игрока: строка " + (row + 1) + ", столбец " + (col + 1));
            System.out.println("Неверный ход! Попробуйте снова.");
            playerMove(scanner);
        }
    }

    private void botMove() {
        Random random = new Random();
        int row, col;

        // Бот пытается выиграть
        if (tryWinMove(botSymbol)) {
            logger.info("Бот сделал победный ход");
            return;
        }

        // Бот блокирует игрока
        if (tryBlockPlayer()) {
            logger.info("Бот заблокировал ход игрока");
            return;
        }

        // Случайный ход
        do {
            row = random.nextInt(3);
            col = random.nextInt(3);
        } while (!isValidMove(row, col));

        board[row][col] = botSymbol;
        logger.info("Бот сделал ход в ячейку (" + (row + 1) + ", " + (col + 1) + ")");
    }

    private boolean tryWinMove(char symbol) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '-') {
                    board[i][j] = symbol;
                    boolean win = checkWin(symbol);
                    board[i][j] = '-';
                    if (win) {
                        board[i][j] = symbol;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean tryBlockPlayer() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '-') {
                    board[i][j] = playerSymbol;
                    boolean win = checkWin(playerSymbol);
                    board[i][j] = '-';
                    if (win) {
                        board[i][j] = botSymbol;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean checkWin(char symbol) {
        try {
            boolean win = checkRows(symbol) || checkColumns(symbol) || checkDiagonals(symbol);
            if (win) {
                logger.info("Победа зафиксирована для символа: " + symbol);
            }
            return win;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка при проверке победы", e);
            return false;
        }
    }

    private boolean checkRows(char symbol) {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == symbol &&
                    board[i][1] == symbol &&
                    board[i][2] == symbol) {
                logger.info("Победа по строке " + (i + 1));
                return true;
            }
        }
        return false;
    }

    private boolean checkColumns(char symbol) {
        for (int j = 0; j < 3; j++) {
            if (board[0][j] == symbol &&
                    board[1][j] == symbol &&
                    board[2][j] == symbol) {
                logger.info("Победа по столбцу " + (j + 1));
                return true;
            }
        }
        return false;
    }

    private boolean checkDiagonals(char symbol) {
        boolean mainDiagonal = true;
        for (int i = 0; i < 3; i++) {
            if (board[i][i] != symbol) {
                mainDiagonal = false;
                break;
            }
        }

        boolean secondaryDiagonal = true;
        for (int i = 0; i < 3; i++) {
            if (board[i][2 - i] != symbol) {
                secondaryDiagonal = false;
                break;
            }
        }

        if (mainDiagonal) {
            logger.info("Победа по главной диагонали");
        }
        if (secondaryDiagonal) {
            logger.info("Победа по побочной диагонали");
        }
        return mainDiagonal || secondaryDiagonal;
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '-') {
                    return false;
                }
            }
        }
        logger.warning("Игровое поле заполнено. Объявляется ничья.");
        return true;
    }

    private boolean isValidMove(int row, int col) {
        boolean valid = row >= 0 && row < 3 &&
                col >= 0 && col < 3 &&
                board[row][col] == '-';
        if (!valid) {
            logger.warning("Попытка некорректного хода: строка " + (row + 1) + ", столбец " + (col + 1));
        }
        return valid;
    }

    private void printBoard() {
        System.out.println("\n  1 2 3");
        for (int i = 0; i < 3; i++) {
            System.out.print((i + 1) + " ");
            for (int j = 0; j < 3; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}