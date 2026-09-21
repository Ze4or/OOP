package ru.nsu.batyaev;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlackjackGameTest {

    @Mock
    private Deck mockDeck;

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outputStreamCaptor;

    @BeforeEach
    void setUp() {
        outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    /**
     * Вспомогательный метод для создания экземпляра игры с перенаправленным вводом.
     */
    private BlackjackGame createGameWithInput(String input) {
        String bufferedInput = input + "\n0\n0\n0\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bufferedInput.getBytes());
        System.setIn(inputStream);
        Scanner scanner = new Scanner(inputStream);
        return new BlackjackGame(mockDeck, scanner);
    }

    private Card createCard(Card.Suit suit, Card.Rank rank) {
        return new Card(suit, rank);
    }

    @Test
    @DisplayName("Ничья при Блэкджеке у обоих участников на раздаче")
    void testBothHaveBlackjack() {
        when(mockDeck.drawCard()).thenReturn(
                createCard(Card.Suit.SPADES, Card.Rank.ACE),
                createCard(Card.Suit.HEARTS, Card.Rank.ACE),
                createCard(Card.Suit.SPADES, Card.Rank.KING),
                createCard(Card.Suit.HEARTS, Card.Rank.QUEEN)
        );

        BlackjackGame game = createGameWithInput("0");
        game.start();

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("У вас и у дилера блэкджек!") || output.contains("Ничья"),
                "Должно быть сообщение о ничье при блэкджеке");
        assertTrue(output.contains("Итоговый счет: 0:0"));
    }

    @Test
    @DisplayName("Победа игрока при получении Блэкджека на раздаче")
    void testPlayerHasBlackjack() {
        when(mockDeck.drawCard()).thenReturn(
                createCard(Card.Suit.SPADES, Card.Rank.ACE),
                createCard(Card.Suit.HEARTS, Card.Rank.TEN),
                createCard(Card.Suit.SPADES, Card.Rank.KING),
                createCard(Card.Suit.HEARTS, Card.Rank.NINE)
        );

        BlackjackGame game = createGameWithInput("0");
        game.start();

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("У вас БЛЭКДЖЕК!") || output.contains("выиграли"),
                "Должно быть сообщение о победе игрока");
        assertTrue(output.contains("Итоговый счет: 1:0"));
    }

    @Test
    @DisplayName("Победа дилера при получении Блэкджека на раздаче")
    void testDealerHasBlackjack() {
        when(mockDeck.drawCard()).thenReturn(
                createCard(Card.Suit.SPADES, Card.Rank.SEVEN),
                createCard(Card.Suit.HEARTS, Card.Rank.ACE),
                createCard(Card.Suit.SPADES, Card.Rank.EIGHT),
                createCard(Card.Suit.HEARTS, Card.Rank.KING)
        );

        BlackjackGame game = createGameWithInput("0");
        game.start();

        String output = outputStreamCaptor.toString();

        assertTrue(
                output.contains("БЛЭКДЖЕК") ||
                        output.contains("дилер") ||
                        output.contains("проиграли") ||
                        output.contains("0:1"),
                "Вывод должен сообщать о победе дилера или блэкджеке. Фактический вывод:\n" + output
        );
        assertTrue(output.contains("0:1"), "Счет должен стать 0:1 в пользу дилера");
    }

    @Test
    @DisplayName("Игрок берет карту и получает перебор (>21) -> Победа дилера")
    void testPlayerBust() {
        when(mockDeck.drawCard()).thenReturn(
                createCard(Card.Suit.SPADES, Card.Rank.TEN),
                createCard(Card.Suit.HEARTS, Card.Rank.TEN),
                createCard(Card.Suit.SPADES, Card.Rank.EIGHT),
                createCard(Card.Suit.HEARTS, Card.Rank.SEVEN),
                createCard(Card.Suit.CLUBS, Card.Rank.SEVEN)
        );

        BlackjackGame game = createGameWithInput("1\n0");
        game.start();

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("25") || output.contains("Перебор") || output.contains("проиграли"),
                "Вывод должен сообщать о переборе (25) или проигрыше");
        assertTrue(output.contains("Итоговый счет: 0:1"));
    }

    @Test
    @DisplayName("Перебор у дилера при доборе карт (<17) -> Победа игрока")
    void testDealerBust() {
        when(mockDeck.drawCard()).thenReturn(
                createCard(Card.Suit.SPADES, Card.Rank.TEN),
                createCard(Card.Suit.HEARTS, Card.Rank.TEN),
                createCard(Card.Suit.SPADES, Card.Rank.NINE),
                createCard(Card.Suit.HEARTS, Card.Rank.FIVE),
                createCard(Card.Suit.CLUBS, Card.Rank.EIGHT)
        );

        BlackjackGame game = createGameWithInput("0\n0");
        game.start();

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("23") || output.contains("перебор") || output.contains("выиграли"));
        assertTrue(output.contains("Итоговый счет: 1:0"));
    }

    @Test
    @DisplayName("Победа игрока по очкам (19 против 18)")
    void testPlayerWinsByPoints() {
        when(mockDeck.drawCard()).thenReturn(
                createCard(Card.Suit.SPADES, Card.Rank.TEN),
                createCard(Card.Suit.HEARTS, Card.Rank.TEN),
                createCard(Card.Suit.SPADES, Card.Rank.NINE),
                createCard(Card.Suit.HEARTS, Card.Rank.FIVE),
                createCard(Card.Suit.CLUBS, Card.Rank.THREE)
        );

        BlackjackGame game = createGameWithInput("0\n0");
        game.start();

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Вы выиграли") || output.contains("выиграли"));
        assertTrue(output.contains("Итоговый счет: 1:0"));
    }

    @Test
    @DisplayName("Победа дилера по очкам (17 против 19)")
    void testDealerWinsByPoints() {
        when(mockDeck.drawCard()).thenReturn(
                createCard(Card.Suit.SPADES, Card.Rank.TEN),
                createCard(Card.Suit.HEARTS, Card.Rank.TEN),
                createCard(Card.Suit.SPADES, Card.Rank.SEVEN),
                createCard(Card.Suit.HEARTS, Card.Rank.NINE)
        );

        BlackjackGame game = createGameWithInput("0\n0");
        game.start();

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Дилер выиграл") || output.contains("проиграли"));
        assertTrue(output.contains("Итоговый счет: 0:1"));
    }

    @Test
    @DisplayName("Ничья по очкам (18 против 18)")
    void testTieByPoints() {
        when(mockDeck.drawCard()).thenReturn(
                createCard(Card.Suit.SPADES, Card.Rank.TEN),
                createCard(Card.Suit.HEARTS, Card.Rank.TEN),
                createCard(Card.Suit.SPADES, Card.Rank.EIGHT),
                createCard(Card.Suit.HEARTS, Card.Rank.EIGHT)
        );

        BlackjackGame game = createGameWithInput("0\n0");
        game.start();

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Ничья"));
        assertTrue(output.contains("Итоговый счет: 0:0"));
    }

    @Test
    @DisplayName("Обработка невалидного ввода пользователя")
    void testInvalidUserInput() {
        when(mockDeck.drawCard()).thenReturn(
                createCard(Card.Suit.SPADES, Card.Rank.TEN),
                createCard(Card.Suit.HEARTS, Card.Rank.TEN),
                createCard(Card.Suit.SPADES, Card.Rank.EIGHT),
                createCard(Card.Suit.HEARTS, Card.Rank.EIGHT)
        );

        BlackjackGame game = createGameWithInput("hello\n5\n0\n0");
        game.start();

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Ошибка") || output.contains("1 или 0"));
    }

    @Test
    @DisplayName("Симуляция игры из двух раундов подряд")
    void testMultipleRounds() {
        when(mockDeck.drawCard()).thenReturn(
                createCard(Card.Suit.SPADES, Card.Rank.TEN),
                createCard(Card.Suit.HEARTS, Card.Rank.TEN),
                createCard(Card.Suit.SPADES, Card.Rank.NINE),
                createCard(Card.Suit.HEARTS, Card.Rank.SEVEN),

                createCard(Card.Suit.SPADES, Card.Rank.TEN),
                createCard(Card.Suit.HEARTS, Card.Rank.TEN),
                createCard(Card.Suit.SPADES, Card.Rank.SEVEN),
                createCard(Card.Suit.HEARTS, Card.Rank.NINE)
        );

        BlackjackGame game = createGameWithInput("0\n1\n0\n0");
        game.start();

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Раунд 1"));
        assertTrue(output.contains("Раунд 2"));
        assertTrue(output.contains("Итоговый счет: 1:1"));
    }
}