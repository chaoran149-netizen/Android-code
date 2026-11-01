package cn.edu.sicnu.cardgame;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;
import cn.edu.sicnu.cardgame.model.PlayerProfile;

public class EditProfileActivity extends Activity {
    private static final int REQUEST_AVATAR = 41;
    private EditText name, student, phone, email;
    private Bitmap draftAvatar;
    private final int[] hobbyViews = {R.id.hobby_travel, R.id.hobby_reading, R.id.hobby_game, R.id.hobby_shopping};
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); setContentView(R.layout.activity_edit_profile);
        PlayerProfile p = ((CardGameApplication) getApplication()).getProfile();
        name = findViewById(R.id.edit_name); student = findViewById(R.id.edit_student);
        phone = findViewById(R.id.edit_phone); email = findViewById(R.id.edit_email);
        draftAvatar = state == null ? p.avatar : state.getParcelable(getString(R.string.key_avatar));
        if (state == null) {
            name.setText(p.name); student.setText(p.student); phone.setText(p.phone); email.setText(p.email);
            int[] ids = {R.id.gender_male, R.id.gender_female, R.id.gender_unspecified};
            ((RadioGroup) findViewById(R.id.gender_group)).check(ids[p.gender]);
            for (int i = 0; i < hobbyViews.length; i++) ((CheckBox) findViewById(hobbyViews[i])).setChecked(p.hobbies[i]);
        }
        showAvatar();
        findViewById(R.id.take_avatar).setOnClickListener(v -> takeAvatar());
        findViewById(R.id.save_profile).setOnClickListener(v -> save());
        findViewById(R.id.cancel_profile).setOnClickListener(v -> finish());
    }
    private void save() {
        String draftName = name.getText().toString().trim();
        String draftPhone = phone.getText().toString().trim();
        String draftEmail = email.getText().toString().trim();
        if (draftName.isEmpty()) { name.setError(getString(R.string.name_required)); name.requestFocus(); return; }
        if (!draftPhone.isEmpty() && !Patterns.PHONE.matcher(draftPhone).matches()) {
            phone.setError(getString(R.string.invalid_phone)); phone.requestFocus(); return;
        }
        if (!draftEmail.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(draftEmail).matches()) {
            email.setError(getString(R.string.invalid_email)); email.requestFocus(); return;
        }
        // 通过全部校验后一次性提交草稿；取消或返回不会改写共享 Model。
        PlayerProfile p = ((CardGameApplication) getApplication()).getProfile();
        p.name = draftName; p.student = student.getText().toString().trim(); p.phone = draftPhone; p.email = draftEmail;
        int checked = ((RadioGroup) findViewById(R.id.gender_group)).getCheckedRadioButtonId();
        p.gender = checked == R.id.gender_male ? 0 : checked == R.id.gender_female ? 1 : 2;
        for (int i = 0; i < hobbyViews.length; i++) p.hobbies[i] = ((CheckBox) findViewById(hobbyViews[i])).isChecked();
        p.avatar = draftAvatar;
        finish();
    }
    private void takeAvatar() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (camera.resolveActivity(getPackageManager()) == null) { notifyUser(R.string.photo_unavailable); return; }
        try { startActivityForResult(camera, REQUEST_AVATAR); }
        catch (ActivityNotFoundException | SecurityException e) { notifyUser(R.string.photo_unavailable); }
    }
    @Override protected void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);
        if (request != REQUEST_AVATAR) return;
        if (result != RESULT_OK) { notifyUser(R.string.photo_cancelled); return; }
        Bundle extras = data == null ? null : data.getExtras();
        Object value = extras == null ? null : extras.get(getString(R.string.camera_data));
        if (value instanceof Bitmap) { draftAvatar = (Bitmap) value; showAvatar(); }
        else notifyUser(R.string.photo_empty);
    }
    private void notifyUser(int message) { Toast.makeText(this, message, Toast.LENGTH_SHORT).show(); }
    private void showAvatar() {
        ImageView image = findViewById(R.id.avatar);
        if (draftAvatar == null) image.setImageResource(R.drawable.ic_avatar); else image.setImageBitmap(draftAvatar);
    }
    @Override protected void onSaveInstanceState(Bundle out) {
        out.putParcelable(getString(R.string.key_avatar), draftAvatar);
        super.onSaveInstanceState(out);
    }
}
