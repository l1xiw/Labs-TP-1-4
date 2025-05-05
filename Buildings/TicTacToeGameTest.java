package Buildings;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TicTacToeGameTest {
    @Test
    public void testGameOutput() {
        // Перехватываем вывод в консоль
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Запускаем игру
        TicTacToeGame game = new TicTacToeGame(100);
        game.play();

        // Проверяем вывод
        assertTrue(outContent.toString().contains("Начало игры в крестики-нолики"));
        assertTrue(outContent.toString().contains("Игрок сделал ход"));

        // Возвращаем оригинальный вывод
        System.setOut(System.out);
    }
}
