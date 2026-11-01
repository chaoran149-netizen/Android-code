package cn.edu.sicnu.cardgame;

import android.app.Application;
import cn.edu.sicnu.cardgame.model.PlayerProfile;

public class CardGameApplication extends Application {
    private PlayerProfile profile;
    @Override public void onCreate() { super.onCreate(); profile = new PlayerProfile(getString(R.string.empty)); }
    public PlayerProfile getProfile() { return profile; }
}
