package ru.nsu.batyaev;

/**
 * Главный класс программы.
 * Запускает игру Blackjack.
 */
public class Main {

    public static void main(String[] args) {
        BlackjackGame game = new BlackjackGame(1);

        game.start();
    }
}
