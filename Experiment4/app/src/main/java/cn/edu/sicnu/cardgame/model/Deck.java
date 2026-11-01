package cn.edu.sicnu.cardgame.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** 完整 52 张牌，不放回地随机抽取；对应附件 Deck.kt。 */
public final class Deck {
    private final List<Card> cards = new ArrayList<>();
    private final Random random;
    public Deck() { this(new Random()); }
    public Deck(Random random) {
        if (random == null) throw new IllegalArgumentException();
        this.random = random;
        for (int suit = 0; suit < 4; suit++)
            for (int rank = 0; rank < 13; rank++) cards.add(new Card(suit, rank));
    }
    public Card drawRandomCard() {
        return cards.isEmpty() ? null : cards.remove(random.nextInt(cards.size()));
    }
    public int remaining() { return cards.size(); }
}
