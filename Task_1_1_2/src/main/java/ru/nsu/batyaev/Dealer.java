package ru.nsu.batyaev;

import java.util.List;

/**
 * Представляет дилера в Blackjack.
 * Использует композицию вместо наследования от Player (LSP).
 */
public class Dealer {

    private final Hand hand = new Hand();
    private boolean hiddenCardOpened = false;

    public void takeCard(Card card) {
        hand.addCard(card);
    }

    public Hand getHand() {
        return hand;
    }

    public int getScore() {
        return hand.getScore();
    }

    public boolean hasBlackjack() {
        return hand.isBlackjack();
    }

    public boolean isBust() {
        return hand.isBust();
    }

    public boolean hasHiddenCard() {
        return !hiddenCardOpened && hand.getCards().size() >= 2;
    }

    /**
     * Открывает скрытую карту и сразу возвращает её наружу.
     */
    public Card revealHiddenCard() {
        hiddenCardOpened = true;
        List<Card> cards = hand.getCards();
        return cards.get(cards.size() - 1);
    }

    public void clearHand() {
        hand.clear();
        hiddenCardOpened = false;
    }
}
