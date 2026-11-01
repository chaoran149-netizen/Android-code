package cn.edu.sicnu.cardgame;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import cn.edu.sicnu.cardgame.model.Card;
import cn.edu.sicnu.cardgame.model.CardMatchingGame;
import cn.edu.sicnu.cardgame.model.PlayerProfile;

/** Controller：分发点击给纯 Java Model，再统一刷新 XML 与牌面 View。 */
public class GameActivity extends Activity {
    private static final int CARD_COUNT = 16;
    private CardMatchingGame game;
    private Button[] buttons;
    private String[] suits, ranks;
    private int statusMessage = R.string.game_ready;
    private final int[] cardIds = {R.id.card_0,R.id.card_1,R.id.card_2,R.id.card_3,
            R.id.card_4,R.id.card_5,R.id.card_6,R.id.card_7,R.id.card_8,R.id.card_9,
            R.id.card_10,R.id.card_11,R.id.card_12,R.id.card_13,R.id.card_14,R.id.card_15};
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); setContentView(R.layout.activity_game);
        game = state == null ? null : (CardMatchingGame) state.getSerializable(getString(R.string.key_game));
        if (game == null) game = new CardMatchingGame(CARD_COUNT);
        if (state != null) statusMessage = state.getInt(getString(R.string.key_message), R.string.game_ready);
        suits = getResources().getStringArray(R.array.card_suits);
        ranks = getResources().getStringArray(R.array.card_ranks);
        GridLayout grid = findViewById(R.id.card_grid);
        buttons = new Button[CARD_COUNT];
        int margin = Math.round(4 * getResources().getDisplayMetrics().density);
        for (int i = 0; i < CARD_COUNT; i++) {
            final int index = i;
            Button button = new Button(this);
            button.setId(cardIds[i]); button.setAllCaps(false); button.setMinWidth(0); button.setMinimumWidth(0);
            button.setMinHeight(0); button.setMinimumHeight(0); button.setPadding(0,0,0,0);
            button.setBackgroundTintList(null); button.setStateListAnimator(null);
            button.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.card_text));
            GridLayout.LayoutParams params = new GridLayout.LayoutParams(GridLayout.spec(i / 4), GridLayout.spec(i % 4, 1f));
            params.width = 0; params.height = getResources().getDimensionPixelSize(R.dimen.card_height);
            params.setMargins(margin, margin, margin, margin);
            button.setLayoutParams(params); button.setOnClickListener(v -> choose(index));
            grid.addView(button); buttons[i] = button;
        }
        findViewById(R.id.reset_game).setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle(R.string.restart_title).setMessage(R.string.restart_message)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.restart_confirm, (dialog, which) -> {
                    game = new CardMatchingGame(CARD_COUNT); statusMessage = R.string.game_ready; render();
                }).show());
        findViewById(R.id.exit_game).setOnClickListener(v -> finish());
        render();
    }
    private void choose(int index) {
        CardMatchingGame.Turn turn = game.chooseCardAtIndex(index);
        switch (turn) {
            case CLOSED: statusMessage = R.string.game_closed; break;
            case CHOSEN: statusMessage = R.string.game_choose; break;
            case RANK_MATCH: statusMessage = R.string.game_rank_match; break;
            case SUIT_MATCH: statusMessage = R.string.game_suit_match; break;
            case MISMATCH: statusMessage = R.string.game_mismatch; break;
            default: break;
        }
        profile().recordScore(game.getScore());
        render();
    }
    private PlayerProfile profile() { return ((CardGameApplication) getApplication()).getProfile(); }
    private void render() {
        boolean ended = !game.hasPossibleMatch();
        for (int i = 0; i < buttons.length; i++) {
            Card card = game.cardAtIndex(i); Button button = buttons[i];
            String face = getString(R.string.card_face, suits[card.getSuit()], ranks[card.getRank()]);
            boolean open = card.isChosen() || card.isMatched();
            button.setText(open ? face : getString(R.string.card_back));
            button.setTextColor(getColor(!open ? R.color.gold : card.getSuit() < 2 ? R.color.red : R.color.ink));
            button.setBackgroundResource(card.isMatched() ? R.drawable.card_matched : open ? R.drawable.card_face : R.drawable.card_back);
            button.setAlpha(card.isMatched() ? 0.65f : 1f);
            button.setEnabled(!card.isMatched() && !ended);
            button.setContentDescription(open ? getString(card.isMatched() ? R.string.card_matched_description : R.string.card_open_description, i + 1, face)
                    : getString(R.string.card_hidden_description, i + 1));
        }
        ((TextView) findViewById(R.id.score)).setText(getString(R.string.score, game.getScore()));
        ((TextView) findViewById(R.id.progress)).setText(getString(R.string.progress, game.matchedCount(), game.size()));
        ((TextView) findViewById(R.id.best_hint)).setText(getString(R.string.best_updated, profile().getHighestScore()));
        ((TextView) findViewById(R.id.game_status)).setText(game.isComplete() ? R.string.game_complete : ended ? R.string.game_no_pairs : statusMessage);
    }
    CardMatchingGame getGame() { return game; }
    @Override protected void onSaveInstanceState(Bundle out) {
        out.putSerializable(getString(R.string.key_game), game);
        out.putInt(getString(R.string.key_message), statusMessage);
        super.onSaveInstanceState(out);
    }
}
