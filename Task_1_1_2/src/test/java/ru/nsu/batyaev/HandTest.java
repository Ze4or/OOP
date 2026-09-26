package ru.nsu.batyaev;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HandTest {

    @Test
    void testAddCard() {
        Hand hand = new Hand();

        Card card = new Card(Card.Suit.HEARTS, Card.Rank.FIVE);
        hand.addCard(card);

        assertEquals(1, hand.getCards().size());
        assertEquals(card, hand.getCards().get(0));
    }

    @Test
    void testGetScore() {
        Hand hand = new Hand();

        hand.addCard(new Card(Card.Suit.HEARTS, Card.Rank.FIVE));
        hand.addCard(new Card(Card.Suit.SPADES, Card.Rank.SEVEN));

        assertEquals(12, hand.getScore());
    }

    @Test
    void testAceAsEleven() {
        Hand hand = new Hand();

        hand.addCard(new Card(Card.Suit.HEARTS, Card.Rank.ACE));
        hand.addCard(new Card(Card.Suit.SPADES, Card.Rank.FIVE));

        assertEquals(16, hand.getScore());
    }

    @Test
    void testAceAsOne() {
        Hand hand = new Hand();

        hand.addCard(new Card(Card.Suit.HEARTS, Card.Rank.ACE));
        hand.addCard(new Card(Card.Suit.SPADES, Card.Rank.TEN));
        hand.addCard(new Card(Card.Suit.CLUBS, Card.Rank.FIVE));

        assertEquals(16, hand.getScore());
    }

    @Test
    void testSeveralAces() {
        Hand hand = new Hand();

        hand.addCard(new Card(Card.Suit.HEARTS, Card.Rank.ACE));
        hand.addCard(new Card(Card.Suit.SPADES, Card.Rank.ACE));
        hand.addCard(new Card(Card.Suit.CLUBS, Card.Rank.NINE));

        assertEquals(21, hand.getScore());
    }

    @Test
    void testBlackjack() {
        Hand hand = new Hand();

        hand.addCard(new Card(Card.Suit.HEARTS, Card.Rank.ACE));
        hand.addCard(new Card(Card.Suit.SPADES, Card.Rank.KING));

        assertTrue(hand.isBlackjack());
    }

    @Test
    void testNotBlackjack() {
        Hand hand = new Hand();

        hand.addCard(new Card(Card.Suit.HEARTS, Card.Rank.TEN));
        hand.addCard(new Card(Card.Suit.SPADES, Card.Rank.NINE));

        assertFalse(hand.isBlackjack());
    }

    @Test
    void testBlackjackOnlyTwoCards() {
        Hand hand = new Hand();

        hand.addCard(new Card(Card.Suit.HEARTS, Card.Rank.ACE));
        hand.addCard(new Card(Card.Suit.SPADES, Card.Rank.FIVE));
        hand.addCard(new Card(Card.Suit.CLUBS, Card.Rank.FIVE));

        assertFalse(hand.isBlackjack());
    }

    @Test
    void testBust() {
        Hand hand = new Hand();

        hand.addCard(new Card(Card.Suit.HEARTS, Card.Rank.TEN));
        hand.addCard(new Card(Card.Suit.SPADES, Card.Rank.NINE));
        hand.addCard(new Card(Card.Suit.CLUBS, Card.Rank.FIVE));

        assertTrue(hand.isBust());
    }

    @Test
    void testNotBust() {
        Hand hand = new Hand();

        hand.addCard(new Card(Card.Suit.HEARTS, Card.Rank.TEN));
        hand.addCard(new Card(Card.Suit.SPADES, Card.Rank.SIX));
        hand.addCard(new Card(Card.Suit.CLUBS, Card.Rank.FIVE));

        assertFalse(hand.isBust());
    }

    @Test
    void testClear() {
        Hand hand = new Hand();

        hand.addCard(new Card(Card.Suit.HEARTS, Card.Rank.FIVE));
        hand.addCard(new Card(Card.Suit.SPADES, Card.Rank.SIX));

        hand.clear();

        assertEquals(0, hand.getCards().size());
        assertEquals(0, hand.getScore());
    }

    @Test
    void testToString() {
        Hand hand = new Hand();

        Card card1 = new Card(Card.Suit.HEARTS, Card.Rank.FIVE);
        Card card2 = new Card(Card.Suit.SPADES, Card.Rank.KING);

        hand.addCard(card1);
        hand.addCard(card2);

        assertEquals(
                "[Пятерка Червы (5), Король Пики (10)]",
                hand.toString()
        );
    }
}