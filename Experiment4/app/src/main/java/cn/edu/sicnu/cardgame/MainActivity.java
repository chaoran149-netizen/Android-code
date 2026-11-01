package cn.edu.sicnu.cardgame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import cn.edu.sicnu.cardgame.model.PlayerProfile;

public class MainActivity extends Activity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); setContentView(R.layout.activity_main);
        findViewById(R.id.edit_profile).setOnClickListener(v -> startActivity(new Intent(this, EditProfileActivity.class)));
        findViewById(R.id.play_game).setOnClickListener(v -> startActivity(new Intent(this, GameActivity.class)));
    }
    @Override protected void onResume() {
        super.onResume();
        PlayerProfile p = ((CardGameApplication) getApplication()).getProfile();
        row(R.id.profile_name, R.string.name_label, p.name);
        row(R.id.profile_student, R.string.student_label, p.student);
        row(R.id.profile_phone, R.string.phone_label, p.phone);
        row(R.id.profile_email, R.string.email_label, p.email);
        int[] genders = {R.string.male, R.string.female, R.string.unspecified};
        row(R.id.profile_gender, R.string.gender_label, getString(genders[p.gender]));
        int[] hobbies = {R.string.hobby_travel, R.string.hobby_reading, R.string.hobby_game, R.string.hobby_shopping};
        StringBuilder selected = new StringBuilder();
        for (int i = 0; i < hobbies.length; i++) if (p.hobbies[i]) {
            if (selected.length() > 0) selected.append(getString(R.string.separator));
            selected.append(getString(hobbies[i]));
        }
        row(R.id.profile_hobbies, R.string.hobbies_label, selected.length() == 0 ? getString(R.string.no_hobbies) : selected.toString());
        ((TextView) findViewById(R.id.profile_best)).setText(getString(R.string.best_score, p.getHighestScore()));
        ImageView avatar = findViewById(R.id.avatar);
        if (p.avatar == null) avatar.setImageResource(R.drawable.ic_avatar);
        else avatar.setImageBitmap(p.avatar);
    }
    private void row(int view, int label, String value) {
        ((TextView) findViewById(view)).setText(getString(R.string.profile_row, getString(label),
                value.isEmpty() ? getString(R.string.unfilled) : value));
    }
}
