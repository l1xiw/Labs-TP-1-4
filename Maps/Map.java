package Maps;

import Players.Bot;
import Players.Character;
import java.util.Random;
import java.util.Scanner;

public class Map {
    private Cell[][] massmap; // Текущее состояние карты (с юнитами)
    private Cell[][] stock;   // Базовый ландшафт (без юнитов)
    private int xx;
    private int yy;
    private Character character;
    private Bot bot;
    private Random random;
    private String p;
    private String b;

    // Координаты замков
    private int playerCastleX, playerCastleY;
    private int enemyCastleX, enemyCastleY;

    // Конструктор для новой игры
    public Map() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите размер карты (X Y):");
        this.xx = scanner.nextInt();
        this.yy = scanner.nextInt();
        this.massmap = new Cell[xx][yy];
        this.stock = new Cell[xx][yy];
        this.random = new Random();
        initializeEmptyMap();
        generateStockCopy();
        runForestEditor(scanner);
        runRoadEditor(scanner);
        placeCastles(scanner);
        validateMapFullness(scanner);
        placeUnitsNearCastles();
    }

    // Конструктор для загрузки из сохранения
    // В конструкторе Map для загрузки
    public Map(int xx, int yy,  String[][] cellTypes, String [][] stockCellTypes) {
        this.xx = xx;
        this.yy = yy;
        this.massmap = new Cell[xx][yy];
        this.stock = new Cell[xx][yy];
        for (int x = 0; x < xx; x++) {
            for (int y = 0; y < yy; y++) {
                String cell = stockCellTypes[x][y];
                /*if (cell.equals("🤖")) {
                    stock[x][y] = new Cell("-"); // ← Очищаем stock от бота
                } else {
                    stock[x][y] = new Cell(cell);
                }*/
                massmap[x][y] = new Cell(cellTypes[x][y]);
            }
        }
        for(int x = 0; x < xx; x++){
            for(int y = 0; y < yy; y++){
                stock[x][y]=new Cell(stockCellTypes[x][y]);
            }
        }
        restoreCastlesAndUnits(); // ← Восстанавливает только замки и героя
    }
    private void generateStockCopy(){
        for(int x = 0; x < xx; x++){
            for( int y = 0; y < yy; y++){
                stock[x][y] = new Cell(massmap[x][y].getCelltype());
            }
        }
    }
    /*public Map(int xx, int yy, String[][] cellTypes) {
        this.xx = xx;
        this.yy = yy;
        this.massmap = new Cell[xx][yy];
        this.stock = new Cell[xx][yy];

        // Инициализация stock (базовый ландшафт)
        for (int x = 0; x < xx; x++) {
            for (int y = 0; y < yy; y++) {
                String cell = cellTypes[x][y];
                stock[x][y] = new Cell(cell); //.equals("🦸") || cell.equals("🤖") ? "-" : cell);
                massmap[x][y] = new Cell(cell); // massmap содержит юнитов из сохранения
            }
        }

        restoreCastlesAndUnits(); // Восстановление замков, героя и бота
    }*/

    // Инициализация пустой карты
    private void initializeEmptyMap() {
        for (int x = 0; x < xx; x++) {
            for (int y = 0; y < yy; y++) {
                massmap[x][y] = new Cell("-");
                stock[x][y] = new Cell("-");
            }
        }
    }

    public Cell[][] getStock() {
        return stock;
    }

    // Редактирование леса
    private void runForestEditor(Scanner scanner) {
        boolean editing = true;
        while (editing) {
            displayMap();
            System.out.println("\nРедактирование леса:");
            System.out.println("1. Точка\n2. Прямоугольник\n3. Треугольник\n4. Завершить редактирование леса");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> placeSingleTree(scanner);
                case 2 -> placeRectangle(scanner);
                case 3 -> placeTriangle(scanner);
                case 4 -> editing = false;
                default -> System.out.println("Неверный выбор!");
            }
        }
    }

    // Размещение единичного дерева
    private void placeSingleTree(Scanner scanner) {
        System.out.println("Введите координаты клетки (X Y):");
        int x = scanner.nextInt();
        int y = scanner.nextInt();
        if (isValidCoordinate(x, y)) {
            massmap[x][y].setCelltype("\uD83C\uDF33");
            stock[x][y].setCelltype("\uD83C\uDF33");
        }
    }

    // Размещение прямоугольника леса
    private void placeRectangle(Scanner scanner) {
        System.out.println("Введите начальные координаты (X1 Y1):");
        int x1 = scanner.nextInt();
        int y1 = scanner.nextInt();
        System.out.println("Введите конечные координаты (X2 Y2):");
        int x2 = scanner.nextInt();
        int y2 = scanner.nextInt();

        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
            for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
                if (isValidCoordinate(x, y)) {
                    massmap[x][y].setCelltype("\uD83C\uDF33");
                    stock[x][y].setCelltype("\uD83C\uDF33");
                }
            }
        }
    }

    // Размещение треугольника леса
    private void placeTriangle(Scanner scanner) {
        System.out.println("Введите координаты основания (X1 Y1):");
        int x1 = scanner.nextInt();
        int y1 = scanner.nextInt();
        System.out.println("Введите координаты вершины (X2 Y2):");
        int x2 = scanner.nextInt();
        int y2 = scanner.nextInt();

        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY - (maxX - x); y++) {
                if (isValidCoordinate(x, y)) {
                    massmap[x][y].setCelltype("\uD83C\uDF33");
                    stock[x][y].setCelltype("\uD83C\uDF33");
                }
            }
        }
    }

    // Редактирование дорог
    private void runRoadEditor(Scanner scanner) {
        boolean editing = true;
        while (editing) {
            displayMap();
            System.out.println("\nРедактирование дороги:");
            System.out.println("1. Прямая\n2. Диагональная\n3. Завершить редактирование дороги");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> createStraightRoad(scanner);
                case 2 -> createDiagonalRoad(scanner);
                case 3 -> editing = false;
                default -> System.out.println("Неверный выбор!");
            }
        }
    }

    // Создание прямой дороги
    private void createStraightRoad(Scanner scanner) {
        System.out.println("Введите начальные координаты дороги (X1 Y1):");
        int x1 = scanner.nextInt();
        int y1 = scanner.nextInt();
        System.out.println("Введите конечные координаты дороги (X2 Y2):");
        int x2 = scanner.nextInt();
        int y2 = scanner.nextInt();

        if (x1 == x2 || y1 == y2) {
            for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
                for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
                    if (isValidCoordinate(x, y)) {
                        massmap[x][y].setCelltype("\uD83D\uDCC9");
                        stock[x][y].setCelltype("\uD83D\uDCC9");
                    }
                }
            }
        } else {
            System.out.println("Ошибка: для прямой дороги одна из координат должна быть одинаковой.");
        }
    }

    // В классе Map.java
    public void Removeunit() {
        if (bot != null) {
            // Удаляем из massmap
            int botX = bot.getX();
            int botY = bot.getY();
            if (isValidCoordinate(botX, botY)) {
                massmap[botX][botY].setCelltype(stock[botX][botY].getCelltype());
            }

            // Удаляем из stock (если он был там по ошибке)
            if (isValidCoordinate(botX, botY)) {
                stock[botX][botY].setCelltype(stock[botX][botY].getCelltype()); // Восстанавливаем ландшафт
            }

            bot = null; // Удаляем объект бота
        }
    }

    // Создание диагональной дороги
    private void createDiagonalRoad(Scanner scanner) {
        System.out.println("Введите начальные координаты дороги (X1 Y1):");
        int x1 = scanner.nextInt();
        int y1 = scanner.nextInt();
        System.out.println("Введите конечные координаты дороги (X2 Y2):");
        int x2 = scanner.nextInt();
        int y2 = scanner.nextInt();

        int dx = Integer.compare(x2, x1);
        int dy = Integer.compare(y2, y1);

        for (int x = x1, y = y1;
             x != x2 + dx && y != y2 + dy;
             x += dx, y += dy) {
            if (isValidCoordinate(x, y)) {
                massmap[x][y].setCelltype("\uD83D\uDCC9");
                stock[x][y].setCelltype("\uD83D\uDCC9");
            }
        }
    }

    // Проверка заполненности карты
    private void validateMapFullness(Scanner scanner) {
        while (!isMapFilled()) {
            System.out.println("На карте есть пустые клетки. Продолжите редактирование.");
            runForestEditor(scanner);
            runRoadEditor(scanner);
        }
    }

    // Проверка, заполнена ли карта
    private boolean isMapFilled() {
        for (int x = 0; x < xx; x++) {
            for (int y = 0; y < yy; y++) {
                if (massmap[x][y].getCelltype().equals("-")) {
                    return false;
                }
            }
        }
        return true;
    }

    // Размещение замков
    private void placeCastles(Scanner scanner) {
        System.out.println("Разместите замок игрока (X Y):");
        playerCastleX = scanner.nextInt();
        playerCastleY = scanner.nextInt();
        placeCastle(playerCastleX, playerCastleY, "\uD83C\uDFF0");

        System.out.println("Разместите замок противника (X Y):");
        enemyCastleX = scanner.nextInt();
        enemyCastleY = scanner.nextInt();
        placeCastle(enemyCastleX, enemyCastleY, "\uD83C\uDFEF");
        generateStockCopy();
        for(int x = 0; x < xx; x++){
            for(int y = 0; y < yy; y++){
                stock[x][y].setCelltype(massmap[x][y].getCelltype());
            }
        }
        generateStockCopy();
    }

    // Установка замка на карту
    private void placeCastle(int x, int y, String symbol) {
        if (isValidCoordinate(x, y)) {
            massmap[x][y].setCelltype(symbol);
            stock[x][y].setCelltype(symbol);
        }
    }


    // Размещение юнитов рядом с замками
    private void placeUnitsNearCastles() {
        placeCharacterNearCastle(playerCastleX, playerCastleY);
        placeBotNearCastle(enemyCastleX, enemyCastleY);
    }

    // Размещение героя рядом с замком игрока
    private void placeCharacterNearCastle(int castleX, int castleY) {
        int unitX = castleX;
        int unitY = castleY + 1; // Справа от замка

        if (!isValidCoordinate(unitX, unitY)) {
            unitX = castleX + 1; // Если справа нельзя, то ниже
            unitY = castleY;
        }

        if (isValidCoordinate(unitX, unitY)) {
            character = new Character(unitX, unitY);
            massmap[unitX][unitY].setCelltype("🦸");
        }
    }

    // Размещение бота рядом с замком противника
    private void placeBotNearCastle(int castleX, int castleY) {
        int unitX = castleX;
        int unitY = castleY - 1; // Слева от замка

        if (!isValidCoordinate(unitX, unitY)) {
            unitX = castleX - 1; // Если слева нельзя, то выше
            unitY = castleY;
        }

        if (isValidCoordinate(unitX, unitY)) {
            bot = new Bot(unitX, unitY);
            massmap[unitX][unitY].setCelltype("🤖");
        }
    }

    // Восстановление замков и юнитов из сохранения
    private void restoreCastlesAndUnits() {
        for (int x = 0; x < xx; x++) {
            for (int y = 0; y < yy; y++) {
                String cell = massmap[x][y].getCelltype();
                if (cell.equals("\uD83C\uDFF0")) { // Замок игрока
                    playerCastleX = x;
                    playerCastleY = y;
                }
                if (cell.equals("\uD83C\uDFEF")) { // Замок противника
                    enemyCastleX = x;
                    enemyCastleY = y;
                }
                if (cell.equals("🦸")) { // Герой
                    character = new Character(x, y);
                }
                if (cell.equals("🤖")) { // Бот
                    bot = new Bot(x, y);
                }
            }
        }
    }

    // Отображение карты
    public void displayMap() {
        for (int x = 0; x < xx; x++) {
            for (int y = 0; y < yy; y++) {
                if (massmap[x][y].getCelltype() == "🤖" && bot == null){
                    String cell2 = stock[x][y].getCelltype();
                    System.out.print("[ " + cell2 + " ] ");
                }
                if (massmap[x][y].getCelltype()=="-"){
                    System.out.print("[ " + stock[x][y].getCelltype() + " ] ");
                }
                else {
                    String cell = massmap[x][y].getCelltype();
                    System.out.print("[ " + cell + " ] ");
                }
            }
            System.out.println();
        }
    }

    // Проверка корректности координат
    private boolean isValidCoordinate(int x, int y) {
        return x >= 0 && x < xx && y >= 0 && y < yy;
    }

    // Обновление карты после перемещений
    public void updateMap(Character hero, Bot bot) {
        // Очистка предыдущих позиций
        if (hero != null && isValidCoordinate(hero.getPreviousX(), hero.getPreviousY())) {
            massmap[hero.getPreviousX()][hero.getPreviousY()].setCelltype(stock[hero.getPreviousX()][hero.getPreviousY()].getCelltype());
        }
        if (bot != null && isValidCoordinate(bot.getPreviousX(), bot.getPreviousY())) {
            massmap[bot.getPreviousX()][bot.getPreviousY()].setCelltype(stock[bot.getPreviousX()][bot.getPreviousY()].getCelltype());
        }

        // Установка новых позиций
        if (hero != null && isValidCoordinate(hero.getX(), hero.getY())) {
            massmap[hero.getX()][hero.getY()].setCelltype("🦸");
        }
        if (bot != null && isValidCoordinate(bot.getX(), bot.getY())) {
            massmap[bot.getX()][bot.getY()].setCelltype("🤖");
        }
    }

    // Геттеры и сеттеры для героя и бота
    public void setCharacter(Character character) {
        this.character = character;
        updateMap(character, bot);
    }

    public void setBot(Bot bot) {
        this.bot = bot;
        updateMap(character, bot);
    }

    // Геттеры для координат замков
    public int getPlayerCastleX() { return playerCastleX; }
    public int getPlayerCastleY() { return playerCastleY; }
    public int getEnemyCastleX() { return enemyCastleX; }
    public int getEnemyCastleY() { return enemyCastleY; }

    // Геттеры для героя и бота
    public Character getCharacter() { return character; }
    public Bot getBot() { return bot; }

    // Геттер для типа клетки (для сохранения)
    public String getCellType(int x, int y) {
        return isValidCoordinate(x, y) ? massmap[x][y].getCelltype() : "";
    }

    // Геттер для базового ландшафта (для сохранения)
    public String getStockCellType(int x, int y) {
        return isValidCoordinate(x, y) ? stock[x][y].getCelltype() : "";
    }

    // Метод для получения всех типов клеток карты
    public String[][] getCellTypes() {
        String[][] cellTypes = new String[xx][yy];
        for (int x = 0; x < xx; x++) {
            for (int y = 0; y < yy; y++) {
                cellTypes[x][y] = massmap[x][y].getCelltype();
            }
        }
        return cellTypes;
    }

    // Метод для получения базового ландшафта


    public int getXX() { return xx; }
    public int getYY() { return yy; }
}

