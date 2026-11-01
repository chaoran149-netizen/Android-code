package cn.edu.sicnu.cardgame.model;

import java.io.Serializable;

/** 附件 Card.kt 的 Java 实现。花色与点数用索引表示，显示符号由资源层提供。 */
public final class Card implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int suit;
    private final int rank;
    private boolean chosen;
    private boolean matched;
    public Card(int suit, int rank) {
        if (suit < 0 || suit >= 4 || rank < 0 || rank >= 13) throw new IllegalArgumentException();
        this.suit = suit; this.rank = rank;
    }
    public int getSuit() { return suit; }
    public int getRank() { return rank; }
    public boolean isChosen() { return chosen; }
    public boolean isMatched() { return matched; }
    void setChosen(boolean value) { chosen = value; }
    void setMatched(boolean value) { matched = value; }
    public int match(Card[] others) {
        if (others == null || others.length != 1 || others[0] == null) return 0;
        Card other = others[0];
        if (other.rank == rank) return 4;
        if (other.suit == suit) return 1;
        return 0;
    }
}
