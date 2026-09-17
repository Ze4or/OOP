package ru.nsu.batyaev;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void playerShouldReceiveCard() {
        Player player = new Player();

        Card card = new Card(
                Card.Suit.HEARTS,
                Card.Rank.SEVEN
        );

        player.takeCard(card);

        assertEquals(1, player.getHand().getCards().size());
    }

    @Test
    void playerShouldCalculateScore() {
        Player player = new Player();

        player.takeCard(new Card(
                Card.Suit.HEARTS,
                Card.Rank.TEN
        ));

        player.takeCard(new Card(
                Card.Suit.CLUBS,
                Card.Rank.SEVEN
        ));

        assertEquals(17, player.getScore());
    }

    @Test
    void playerShouldDetectBlackjack() {
        Player player = new Player();

        player.takeCard(new Card(
                Card.Suit.HEARTS,
                Card.Rank.ACE
        ));

        player.takeCard(new Card(
                Card.Suit.CLUBS,
                Card.Rank.KING
        ));

        assertTrue(player.hasBlackjack());
    }

    @Test
    void playerShouldDetectBust() {
        Player player = new Player();

        player.takeCard(new Card(
                Card.Suit.HEARTS,
                Card.Rank.KING
        ));

        player.takeCard(new Card(
                Card.Suit.CLUBS,
                Card.Rank.QUEEN
        ));

        player.takeCard(new Card(
                Card.Suit.DIAMONDS,
                Card.Rank.FIVE
        ));

        assertTrue(player.isBust());
    }

    @Test
    void clearHandShouldRemoveAllCards() {
        Player player = new Player();

        player.takeCard(new Card(
                Card.Suit.HEARTS,
                Card.Rank.TEN
        ));

        player.clearHand();

        assertEquals(0, player.getHand().getCards().size());
        assertEquals(0, player.getScore());
    }
}