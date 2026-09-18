package ru.nsu.batyaev;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DealerTest {

    @Test
    void dealerShouldHaveHiddenCard() {
        Dealer dealer = new Dealer();

        Card hiddenCard = new Card(
                Card.Suit.HEARTS,
                Card.Rank.KING
        );

        dealer.setHiddenCard(hiddenCard);

        assertTrue(dealer.hasHiddenCard());
        assertSame(hiddenCard, dealer.getHiddenCard());
    }

    @Test
    void hiddenCardShouldNotBeIncludedInScore() {
        Dealer dealer = new Dealer();

        Card hiddenCard = new Card(
                Card.Suit.HEARTS,
                Card.Rank.KING
        );

        dealer.setHiddenCard(hiddenCard);

        dealer.takeCard(new Card(
                Card.Suit.CLUBS,
                Card.Rank.SEVEN
        ));

        assertEquals(7, dealer.getScore());
    }

    @Test
    void revealHiddenCardShouldAddCardToHand() {
        Dealer dealer = new Dealer();

        Card hiddenCard = new Card(
                Card.Suit.HEARTS,
                Card.Rank.KING
        );

        dealer.setHiddenCard(hiddenCard);

        dealer.takeCard(new Card(
                Card.Suit.CLUBS,
                Card.Rank.SEVEN
        ));

        dealer.revealHiddenCard();

        assertEquals(2, dealer.getHand().getCards().size());
    }

    @Test
    void revealHiddenCardShouldUpdateScore() {
        Dealer dealer = new Dealer();

        Card hiddenCard = new Card(
                Card.Suit.HEARTS,
                Card.Rank.KING
        );

        dealer.setHiddenCard(hiddenCard);

        dealer.takeCard(new Card(
                Card.Suit.CLUBS,
                Card.Rank.SEVEN
        ));

        dealer.revealHiddenCard();

        assertEquals(17, dealer.getScore());
    }

    @Test
    void revealHiddenCardShouldRemoveHiddenCard() {
        Dealer dealer = new Dealer();

        Card hiddenCard = new Card(
                Card.Suit.HEARTS,
                Card.Rank.KING
        );

        dealer.setHiddenCard(hiddenCard);

        dealer.revealHiddenCard();

        assertFalse(dealer.hasHiddenCard());
        assertNull(dealer.getHiddenCard());
    }

    @Test
    void clearHandShouldAlsoRemoveHiddenCard() {
        Dealer dealer = new Dealer();

        dealer.setHiddenCard(new Card(
                Card.Suit.HEARTS,
                Card.Rank.KING
        ));

        dealer.takeCard(new Card(
                Card.Suit.CLUBS,
                Card.Rank.SEVEN
        ));

        dealer.clearHand();

        assertEquals(0, dealer.getHand().getCards().size());
        assertFalse(dealer.hasHiddenCard());
        assertNull(dealer.getHiddenCard());
    }
}