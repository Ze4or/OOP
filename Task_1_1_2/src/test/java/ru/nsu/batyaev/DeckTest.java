package ru.nsu.batyaev;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {

    @Test
    void oneDeckShouldContain52Cards() {
        Deck deck = new Deck(1);

        assertEquals(52, deck.getRemainingCards());
    }

    @Test
    void twoDecksShouldContain104Cards() {
        Deck deck = new Deck(2);

        assertEquals(104, deck.getRemainingCards());
    }

    @Test
    void drawingCardShouldDecreaseDeckSize() {
        Deck deck = new Deck(1);

        deck.drawCard();

        assertEquals(51, deck.getRemainingCards());
    }

    @Test
    void drawCardShouldReturnCard() {
        Deck deck = new Deck(1);

        Card card = deck.drawCard();

        assertNotNull(card);
    }

    @Test
    void oneDeckShouldContain52UniqueCards() {
        Deck deck = new Deck(1);

        Set<String> cards = new HashSet<>();

        for (int i = 0; i < 52; i++) {
            cards.add(deck.drawCard().toString());
        }

        assertEquals(52, cards.size());
    }

    @Test
    void drawingFromEmptyDeckShouldThrowException() {
        Deck deck = new Deck(1);

        for (int i = 0; i < 52; i++) {
            deck.drawCard();
        }

        assertThrows(
                IllegalStateException.class,
                deck::drawCard
        );
    }
}