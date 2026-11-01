package cn.edu.sicnu.cardgame.model;

import android.graphics.Bitmap;

/** 仅驻留内存，不写文件、数据库或 SharedPreferences。 */
public final class PlayerProfile {
    public String name;
    public String student;
    public String phone;
    public String email;
    public int gender = 2;
    public final boolean[] hobbies = new boolean[4];
    public Bitmap avatar;
    private int highestScore;
    public PlayerProfile(String empty) { name = empty; student = empty; phone = empty; email = empty; }
    public int getHighestScore() { return highestScore; }
    public void recordScore(int score) { highestScore = Math.max(highestScore, score); }
}
