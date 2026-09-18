package ru.nsu.batyaev;

import java.util.Scanner;

/**
 * Управляет игровым процессом Blackjack.
 * Отвечает за раздачу карт, ходы игрока и дилера,
 * проверку Blackjack и определение победителя.
 */
public class BlackjackGame {

    private final Scanner scanner = new Scanner(System.in);

    private final Deck deck;
    private final Player player;
    private final Dealer dealer;

    private int playerWins;
    private int dealerWins;
    private int roundNumber = 1;

    /**
     * Создаёт новую игру Blackjack.
     *
     * @param numberOfDecks количество используемых колод
     */
    public BlackjackGame(int numberOfDecks) {
        deck = new Deck(numberOfDecks);
        player = new Player();
        dealer = new Dealer();
    }

    public void start() {

        System.out.println("Добро пожаловать в Блэкджек!");

        boolean playAgain = true;

        while (playAgain) {

            playRound();

            System.out.println();
            System.out.println("Счет: " + playerWins + ":" + dealerWins);

            System.out.print("Сыграть еще раз? " + "(1 - да, 0 - нет): ");

            int answer = readChoice();

            if (answer == 1) {
                roundNumber++;
            } else {
                playAgain = false;
            }
        }

        System.out.println();
        System.out.println("Игра окончена!");
        System.out.println("Итоговый счет: " + playerWins + ":" + dealerWins);
    }

    private void playRound() {

        player.clearHand();
        dealer.clearHand();

        System.out.println();
        System.out.println("Раунд " + roundNumber);

        dealCards();

        printGameState();

        // Проверяем блэкджек сразу после раздачи.
        if (player.hasBlackjack() || dealer.hasBlackjack()) {
            checkBlackjack();
            return;
        }

        // Ход игрока.
        boolean playerFinished = playerTurn();

        // Если игрок проиграл из-за перебора, ход дилера не нужен.
        if (!playerFinished) {
            return;
        }

        // Ход дилера.
        dealerTurn();

        // Определяем победителя.
        determineWinner();
    }

    /**
     * Первоначальная раздача.
     * Игрок:
     * 2 открытые карты.
     * Дилер:
     * 1 открытая карта + 1 закрытая.
     */
    private void dealCards() {

        player.takeCard(deck.drawCard());
        dealer.takeCard(deck.drawCard());

        player.takeCard(deck.drawCard());
        dealer.setHiddenCard(deck.drawCard());

        System.out.println("Дилер раздал карты");
    }

    /**
     * Ход игрока.
     * Возвращает false, если игрок проиграл
     * из-за превышения 21.
     */
    private boolean playerTurn() {

        System.out.println();
        System.out.println("Ваш ход");
        System.out.println("-------");

        while (true) {

            System.out.println("Введите \"1\", чтобы взять карту, " + "и \"0\", чтобы остановиться");

            int choice = readChoice();

            if (choice == 0) {
                return true;
            }

            Card card = deck.drawCard();

            player.takeCard(card);

            System.out.println("Вы открыли карту " + card + " (" + card.getValue() + ")");

            printGameState();

            // Если игрок превысил 21, он сразу проигрывает.
            if (player.isBust()) {

                System.out.println("Вы проиграли! " + "Сумма ваших карт: " + player.getScore());

                dealerWins++;

                return false;
            }
        }
    }

    /**
     * Ход дилера.
     * Сначала открывается закрытая карта.
     * После этого дилер берёт карты,
     * пока сумма меньше 17.
     */
    private void dealerTurn() {

        System.out.println();
        System.out.println("Ход дилера");
        System.out.println("-------");

        dealer.revealHiddenCard();

        Card revealedCard = dealer.getHand().getCards().get(dealer.getHand().getCards().size() - 1);

        System.out.println("Дилер открывает закрытую карту " + revealedCard + " (" + revealedCard.getValue() + ")");

        printGameState();

        // Дилер обязан брать карту, пока его сумма меньше 17.
        while (dealer.getScore() < 17) {

            Card card = deck.drawCard();

            dealer.takeCard(card);

            System.out.println("Дилер открывает карту " + card + " (" + card.getValue() + ")");

            printGameState();

            if (dealer.isBust()) {

                System.out.println("У дилера перебор: " + dealer.getScore());

                break;
            }
        }
    }

    /**
     * Проверка блэкджека после первоначальной раздачи.
     */
    private void checkBlackjack() {

        // Открываем карту дилера, чтобы показать результат.
        dealer.revealHiddenCard();

        if (player.hasBlackjack() && dealer.hasBlackjack()) {

            System.out.println("У вас и у дилера блэкджек!");

            System.out.println("Ничья.");

        } else if (player.hasBlackjack()) {

            playerWins++;

            System.out.println("У вас БЛЭКДЖЕК!");

            System.out.println("Вы выиграли раунд!");

        } else {

            dealerWins++;

            System.out.println("У дилера БЛЭКДЖЕК!");

            System.out.println("Вы проиграли раунд.");
        }

        printGameState();
    }

    /**
     * Определение победителя после хода дилера.
     */
    private void determineWinner() {

        int playerScore = player.getScore();
        int dealerScore = dealer.getScore();

        System.out.println();

        if (player.isBust()) {

            dealerWins++;

            System.out.println("Вы проиграли раунд!");

            return;
        }

        // Если дилер превысил 21, игрок автоматически победил.
        if (dealer.isBust()) {

            playerWins++;

            System.out.println("У дилера перебор!");

            System.out.println("Вы выиграли раунд!");

            return;
        }

        // Сравниваем очки.
        if (playerScore > dealerScore) {

            playerWins++;

            System.out.println("Вы выиграли раунд!");

        } else if (playerScore < dealerScore) {

            dealerWins++;

            System.out.println("Дилер выиграл раунд!");

        } else {

            System.out.println("Ничья!");
        }

        System.out.println("Счет: " + playerWins + ":" + dealerWins);
    }

    /**
     * Вывод текущего состояния игры.
     */
    private void printGameState() {

        System.out.println("Ваши карты: " + player.getHand() + "  " + player.getScore());

        System.out.print("Карты дилера: " + dealer.getHand());

        if (dealer.hasHiddenCard()) {

            System.out.println(", <закрытая карта>");

        } else {

            System.out.println("  " + dealer.getScore());
        }
    }

    /**
     * Чтение выбора пользователя.
     */
    private int readChoice() {

        while (true) {

            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                return 0;
            }

            if (input.equals("1")) {
                return 1;
            }

            System.out.print("Ошибка. Введите только 1 или 0: ");
        }
    }
}
