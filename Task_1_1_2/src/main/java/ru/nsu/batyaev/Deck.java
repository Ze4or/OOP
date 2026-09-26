package ru.nsu.batyaev;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Представляет колоду карт для игры Blackjack.
 * Колода может состоять из одной или нескольких колод.
 */
public class Deck {

    private final List<Card> cards = new ArrayList<>();

    public Deck(int numberOfDecks) {
        createDecks(numberOfDecks);
        shuffle();
    }

    private void createDecks(int numberOfDecks) {
        for (int i = 0; i < numberOfDecks; i++) {

            for (Card.Suit suit : Card.Suit.values()) {

                for (Card.Rank rank : Card.Rank.values()) {
                    cards.add(new Card(suit, rank));
                }
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card drawCard() {

        if (cards.isEmpty()) {
            throw new IllegalStateException(
                    "В колоде закончились карты!"
            );
        }

        return cards.remove(cards.size() - 1);
    }

    public int getRemainingCards() {
        return cards.size();
    }
}
