package ru.nsu.batyaev;

/**
 * Представляет дилера в Blackjack.
 * Дилер наследуется от класса Player и дополнительно
 * имеет одну скрытую карту.
 */
public class Dealer extends Player {

    private Card hiddenCard;

    public void setHiddenCard(Card card) {
        hiddenCard = card;
    }

    public Card getHiddenCard() {
        return hiddenCard;
    }

    public boolean hasHiddenCard() {
        return hiddenCard != null;
    }

    public void revealHiddenCard() {

        if (hiddenCard != null) {
            takeCard(hiddenCard);
            hiddenCard = null;
        }
    }

    @Override
    public void clearHand() {
        super.clearHand();
        hiddenCard = null;
    }
}
