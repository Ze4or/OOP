package ru.nsu.batyaev;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HandTest {

    @Test
    void newHandShouldHaveZeroScore() {
        Hand hand = new Hand();

        assertEquals(0, hand.getScore());
    }

    @Test
    void shouldCalculateScoreOfSeveralCards() {
        Hand hand = new Hand();

        hand.addCard(new Card(
                Card.Suit.HEARTS,
                Card.Rank.SEVEN
        ));

        hand.addCard(new Card(
                Card.Suit.CLUBS,
                Card.Rank.EIGHT
        ));

        assertEquals(15, hand.getScore());
    }

    @Test
    void aceShouldCountAs11WhenPossible() {
        Hand hand = new Hand();

        hand.addCard(new Card(
                Card.Suit.HEARTS,
                Card.Rank.ACE
        ));

        hand.addCard(new Card(
                Card.Suit.CLUBS,
                Card.Rank.SIX
        ));

        assertEquals(17, hand.getScore());
    }

    @Test
    void aceShouldCountAs1When11WouldCauseBust() {
        Hand hand = new Hand();

        hand.addCard(new Card(
                Card.Suit.HEARTS,
                Card.Rank.ACE
        ));

        hand.addCard(new Card(
                Card.Suit.CLUBS,
                Card.Rank.KING
        ));

        hand.addCard(new Card(
                Card.Suit.DIAMONDS,
                Card.Rank.FIVE
        ));

        assertEquals(16, hand.getScore());
    }

    @Test
    void twoAcesShouldHaveScore12() {
        Hand hand = new Hand();

        hand.addCard(new Card(
                Card.Suit.HEARTS,
                Card.Rank.ACE
        ));

        hand.addCard(new Card(
                Card.Suit.CLUBS,
                Card.Rank.ACE
        ));

        assertEquals(12, hand.getScore());
    }

    @Test
    void shouldDetectBlackjack() {
        Hand hand = new Hand();

        hand.addCard(new Card(
                Card.Suit.HEARTS,
                Card.Rank.ACE
        ));

        hand.addCard(new Card(
                Card.Suit.CLUBS,
                Card.Rank.KING
        ));

        assertTrue(hand.isBlackjack());
    }

    @Test
    void threeCardsWith21ShouldNotBeBlackjack() {
        Hand hand = new Hand();

        hand.addCard(new Card(
                Card.Suit.HEARTS,
                Card.Rank.SEVEN
        ));

        hand.addCard(new Card(
                Card.Suit.CLUBS,
                Card.Rank.SEVEN
        ));

        hand.addCard(new Card(
                Card.Suit.DIAMONDS,
                Card.Rank.SEVEN
        ));

        assertEquals(21, hand.getScore());
        assertFalse(hand.isBlackjack());
    }

    @Test
    void shouldDetectBust() {
        Hand hand = new Hand();

        hand.addCard(new Card(
                Card.Suit.HEARTS,
                Card.Rank.KING
        ));

        hand.addCard(new Card(
                Card.Suit.CLUBS,
                Card.Rank.QUEEN
        ));

        hand.addCard(new Card(
                Card.Suit.DIAMONDS,
                Card.Rank.FIVE
        ));

        assertEquals(25, hand.getScore());
        assertTrue(hand.isBust());
    }

    @Test
    void score21ShouldNotBeBust() {
        Hand hand = new Hand();

        hand.addCard(new Card(
                Card.Suit.HEARTS,
                Card.Rank.KING
        ));

        hand.addCard(new Card(
                Card.Suit.CLUBS,
                Card.Rank.JACK
        ));

        hand.addCard(new Card(
                Card.Suit.DIAMONDS,
                Card.Rank.ACE
        ));

        assertEquals(21, hand.getScore());
        assertFalse(hand.isBust());
    }

    @Test
    void clearShouldRemoveAllCards() {
        Hand hand = new Hand();

        hand.addCard(new Card(
                Card.Suit.HEARTS,
                Card.Rank.TEN
        ));

        hand.clear();

        assertEquals(0, hand.getCards().size());
        assertEquals(0, hand.getScore());
    }
}