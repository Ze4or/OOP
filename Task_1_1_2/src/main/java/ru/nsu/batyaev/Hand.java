package ru.nsu.batyaev;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Представляет набор карт одного игрока.
 * Отвечает за добавление карт и подсчёт очков.
 */
public class Hand {

    private final List<Card> cards = new ArrayList<>();

    public void addCard(Card card) {
        cards.add(card);
    }

    public List<Card> getCards() {
        return Collections.unmodifiableList(cards);
    }

    public int getScore() {

        int score = 0;
        int aces = 0;

        for (Card card : cards) {

            score += card.getValue();

            if (card.getRank() == Card.Rank.ACE) {
                aces++;
            }
        }

        // Если сумма больше 21, превращаем тузы с 11 в 1.
        while (score > 21 && aces > 0) {
            score -= 10;
            aces--;
        }

        return score;
    }

    public boolean isBlackjack() {
        return cards.size() == 2 && getScore() == 21;
    }

    public boolean isBust() {
        return getScore() > 21;
    }

    public void clear() {
        cards.clear();
    }

    @Override
    public String toString() {

        int score = 0;
        int aces = 0;

        // Сначала считаем сумму всех карт, считая каждый туз за 11.
        for (Card card : cards) {
            score += card.getValue();

            if (card.getRank() == Card.Rank.ACE) {
                aces++;
            }
        }

        // Определяем, сколько тузов нужно считать за 1.
        int acesAsOne = 0;

        while (score > 21 && acesAsOne < aces) {
            score -= 10;
            acesAsOne++;
        }

        StringBuilder result = new StringBuilder("[");
        int aceIndex = 0;

        for (int i = 0; i < cards.size(); i++) {

            Card card = cards.get(i);

            int value = card.getValue();

            // Для туза определяем его реальное значение в текущей комбинации.
            if (card.getRank() == Card.Rank.ACE) {

                if (aceIndex < acesAsOne) {
                    value = 1;
                }

                aceIndex++;
            }

            result.append(card).append(" (").append(value).append(")");

            if (i < cards.size() - 1) {
                result.append(", ");
            }
        }

        result.append("]");

        return result.toString();
    }
}
