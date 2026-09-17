package ru.nsu.batyaev;

/**
 * Представляет игрока в Blackjack.
 * Игрок имеет руку с картами и может получать новые карты.
 */
public class Player {

    private final Hand hand = new Hand();

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

    public void clearHand() {
        hand.clear();
    }
}
