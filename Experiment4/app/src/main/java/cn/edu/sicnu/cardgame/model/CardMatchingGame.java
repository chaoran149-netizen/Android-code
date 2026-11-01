package cn.edu.sicnu.cardgame.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** 按附件 CardMatchingGame.kt 保留全部选牌、计分与取消选择语义。 */
public final class CardMatchingGame implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int MISMATCH_PENALTY = 2;
    public static final int MATCH_BONUS = 4;
    public static final int COST_TO_CHOOSE = 1;
    public enum Turn { IGNORED, CLOSED, CHOSEN, RANK_MATCH, SUIT_MATCH, MISMATCH }
    private int score;
    private final List<Card> cards = new ArrayList<>();
    public CardMatchingGame(int count) {
        if (count < 2 || count > 52) throw new IllegalArgumentException();
        Deck deck = new Deck();
        for (int i = 0; i < count; i++) cards.add(deck.drawRandomCard());
    }
    /** 固定牌序构造器用于测试计分分支，并复制输入防止外部修改。 */
    public CardMatchingGame(List<Card> initialCards) {
        if (initialCards == null || initialCards.size() < 2 || initialCards.size() > 52)
            throw new IllegalArgumentException();
        for (int i = 0; i < initialCards.size(); i++) {
            Card card = initialCards.get(i);
            if (card == null) throw new IllegalArgumentException();
            for (int j = 0; j < i; j++) {
                Card other = initialCards.get(j);
                if (other.getRank() == card.getRank() && other.getSuit() == card.getSuit())
                    throw new IllegalArgumentException();
            }
            cards.add(new Card(card.getSuit(), card.getRank()));
        }
    }
    public Card cardAtIndex(int index) { return cards.get(index); }
    public int size() { return cards.size(); }
    public int getScore() { return score; }
    public Turn chooseCardAtIndex(int index) {
        Card card = cardAtIndex(index);
        if (card.isMatched()) return Turn.IGNORED;
        if (card.isChosen()) { card.setChosen(false); return Turn.CLOSED; }
        Turn turn = Turn.CHOSEN;
        for (Card other : cards) {
            if (other.isChosen() && !other.isMatched()) {
                int matchScore = card.match(new Card[]{other});
                if (matchScore > 0) {
                    score += matchScore * MATCH_BONUS;
                    other.setMatched(true); card.setMatched(true);
                    turn = matchScore == 4 ? Turn.RANK_MATCH : Turn.SUIT_MATCH;
                } else {
                    score -= MISMATCH_PENALTY;
                    other.setChosen(false);
                    turn = Turn.MISMATCH;
                }
                break;
            }
        }
        score -= COST_TO_CHOOSE;
        card.setChosen(true);
        return turn;
    }
    public int matchedCount() {
        int count = 0;
        for (Card card : cards) if (card.isMatched()) count++;
        return count;
    }
    public boolean isComplete() { return matchedCount() == cards.size(); }
    public boolean hasPossibleMatch() {
        for (int i = 0; i < size(); i++) {
            if (cards.get(i).isMatched()) continue;
            for (int j = i + 1; j < size(); j++)
                if (!cards.get(j).isMatched() && cards.get(i).match(new Card[]{cards.get(j)}) > 0)
                    return true;
        }
        return false;
    }
}
