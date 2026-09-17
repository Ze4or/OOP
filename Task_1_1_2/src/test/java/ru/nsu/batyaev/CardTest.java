package ru.nsu.batyaev;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    @Test
    void sevenShouldHaveValue7() {
        Card card = new Card(Card.Suit.HEARTS, Card.Rank.SEVEN);

        assertEquals(7, card.getValue());
    }

    @Test
    void jackShouldHaveValue10() {
        Card card = new Card(Card.Suit.HEARTS, Card.Rank.JACK);

        assertEquals(10, card.getValue());
    }

    @Test
    void queenShouldHaveValue10() {
        Card card = new Card(Card.Suit.HEARTS, Card.Rank.QUEEN);

        assertEquals(10, card.getValue());
    }

    @Test
    void kingShouldHaveValue10() {
        Card card = new Card(Card.Suit.HEARTS, Card.Rank.KING);

        assertEquals(10, card.getValue());
    }

    @Test
    void aceShouldHaveValue11() {
        Card card = new Card(Card.Suit.HEARTS, Card.Rank.ACE);

        assertEquals(11, card.getValue());
    }

    @Test
    void toStringShouldReturnCardNameAndSuit() {
        Card card = new Card(
                Card.Suit.SPADES,
                Card.Rank.QUEEN
        );

        assertEquals("Дама Пики", card.toString());
    }
}