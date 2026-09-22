package ru.nsu.batyaev;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DealerTest {

    @Test
    void dealerShouldHaveHiddenCardWhenTwoCardsDealt() {
        Dealer dealer = new Dealer();

        dealer.takeCard(new Card(Card.Suit.HEARTS, Card.Rank.KING));
        dealer.takeCard(new Card(Card.Suit.CLUBS, Card.Rank.SEVEN));

        assertTrue(dealer.hasHiddenCard());
    }

    @Test
    void revealHiddenCardShouldReturnLastCardAndOpenIt() {
        Dealer dealer = new Dealer();

        Card card1 = new Card(Card.Suit.HEARTS, Card.Rank.KING);
        Card card2 = new Card(Card.Suit.CLUBS, Card.Rank.SEVEN);

        dealer.takeCard(card1);
        dealer.takeCard(card2);

        Card revealedCard = dealer.revealHiddenCard();

        assertEquals(card2, revealedCard);
        assertFalse(dealer.hasHiddenCard());
    }

    @Test
    void dealerScoreShouldIncludeAllCardsInHand() {
        Dealer dealer = new Dealer();

        dealer.takeCard(new Card(Card.Suit.HEARTS, Card.Rank.KING));
        dealer.takeCard(new Card(Card.Suit.CLUBS, Card.Rank.SEVEN));

        assertEquals(17, dealer.getScore());
    }

    @Test
    void clearHandShouldResetHandAndHiddenFlag() {
        Dealer dealer = new Dealer();

        dealer.takeCard(new Card(Card.Suit.HEARTS, Card.Rank.KING));
        dealer.takeCard(new Card(Card.Suit.CLUBS, Card.Rank.SEVEN));

        dealer.clearHand();

        assertEquals(0, dealer.getHand().getCards().size());
        assertFalse(dealer.hasHiddenCard());
    }

    @Test
    void dealerShouldDetectBlackjack() {
        Dealer dealer = new Dealer();

        dealer.takeCard(new Card(Card.Suit.HEARTS, Card.Rank.ACE));
        dealer.takeCard(new Card(Card.Suit.CLUBS, Card.Rank.KING));

        assertTrue(dealer.hasBlackjack());
    }
}
