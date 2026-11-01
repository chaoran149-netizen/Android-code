package cn.edu.sicnu.cardgame;

import android.content.Context;
import android.widget.GridLayout;
import androidx.test.core.app.ActivityScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import cn.edu.sicnu.cardgame.model.Card;
import cn.edu.sicnu.cardgame.model.CardMatchingGame;
import cn.edu.sicnu.cardgame.model.PlayerProfile;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class AppUiTest {
    private Context context;
    @Before public void resetPlayer() {
        context=InstrumentationRegistry.getInstrumentation().getTargetContext();
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            PlayerProfile p=((CardGameApplication)context.getApplicationContext()).getProfile();
            p.name=context.getString(R.string.empty);p.student=p.name;p.phone=p.name;p.email=p.name;
            p.gender=2;p.avatar=null;
            for(int i=0;i<4;i++)p.hobbies[i]=false;
        });
    }
    private String row(int label,String value) { return context.getString(R.string.profile_row,context.getString(label),value); }
    @Test public void profileSaveReturnsAllChangedValuesToHome() {
        try(ActivityScenario<MainActivity> scenario=ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.edit_profile)).perform(scrollTo(),click());
            onView(withId(R.id.edit_name)).perform(replaceText(context.getString(R.string.demo_name)),closeSoftKeyboard());
            onView(withId(R.id.edit_student)).perform(replaceText(context.getString(R.string.demo_student)),closeSoftKeyboard());
            onView(withId(R.id.edit_phone)).perform(replaceText(context.getString(R.string.demo_phone)),closeSoftKeyboard());
            onView(withId(R.id.edit_email)).perform(replaceText(context.getString(R.string.demo_email)),closeSoftKeyboard());
            onView(withId(R.id.gender_female)).perform(scrollTo(),click());
            onView(withId(R.id.hobby_reading)).perform(scrollTo(),click());
            onView(withId(R.id.save_profile)).perform(scrollTo(),click());
            onView(withId(R.id.profile_name)).check(matches(withText(row(R.string.name_label,context.getString(R.string.demo_name)))));
            onView(withId(R.id.profile_gender)).check(matches(withText(row(R.string.gender_label,context.getString(R.string.female)))));
            onView(withId(R.id.profile_student)).check(matches(withText(row(R.string.student_label,context.getString(R.string.demo_student)))));
            onView(withId(R.id.profile_phone)).check(matches(withText(row(R.string.phone_label,context.getString(R.string.demo_phone)))));
            onView(withId(R.id.profile_email)).check(matches(withText(row(R.string.email_label,context.getString(R.string.demo_email)))));
            onView(withId(R.id.profile_hobbies)).check(matches(withText(row(R.string.hobbies_label,context.getString(R.string.hobby_reading)))));
        }
    }
    @Test public void cancelDoesNotApplyDraft() {
        try(ActivityScenario<MainActivity> scenario=ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.edit_profile)).perform(scrollTo(),click());
            onView(withId(R.id.edit_name)).perform(replaceText(context.getString(R.string.demo_name)),closeSoftKeyboard());
            onView(withId(R.id.cancel_profile)).perform(scrollTo(),click());
            onView(withId(R.id.profile_name)).check(matches(withText(row(R.string.name_label,context.getString(R.string.unfilled)))));
        }
    }
    @Test public void emptyNameIsRejected() {
        try(ActivityScenario<EditProfileActivity> scenario=ActivityScenario.launch(EditProfileActivity.class)) {
            onView(withId(R.id.save_profile)).perform(scrollTo(),click());
            onView(withId(R.id.edit_name)).check(matches(hasErrorText(context.getString(R.string.name_required))));
        }
    }
    @Test public void invalidEmailIsRejectedWithoutSavingName() {
        try(ActivityScenario<EditProfileActivity> scenario=ActivityScenario.launch(EditProfileActivity.class)) {
            onView(withId(R.id.edit_name)).perform(replaceText(context.getString(R.string.demo_name)),closeSoftKeyboard());
            onView(withId(R.id.edit_email)).perform(replaceText(context.getString(R.string.demo_name)),closeSoftKeyboard());
            onView(withId(R.id.save_profile)).perform(scrollTo(),click());
            onView(withId(R.id.edit_email)).check(matches(hasErrorText(context.getString(R.string.invalid_email))));
            scenario.onActivity(a -> assertTrue(((CardGameApplication)a.getApplication()).getProfile().name.isEmpty()));
        }
    }
    @Test public void gameCanFlipRecreateAndRestart() {
        try(ActivityScenario<GameActivity> scenario=ActivityScenario.launch(GameActivity.class)) {
            scenario.onActivity(a -> assertEquals(16,((GridLayout)a.findViewById(R.id.card_grid)).getChildCount()));
            onView(withId(R.id.card_0)).perform(scrollTo(),click());
            onView(withId(R.id.score)).check(matches(withText(context.getString(R.string.score,-1))));
            scenario.recreate();
            onView(withId(R.id.score)).check(matches(withText(context.getString(R.string.score,-1))));
            onView(withId(R.id.card_0)).check(matches(not(withText(R.string.card_back))));
            onView(withId(R.id.reset_game)).perform(scrollTo(),click());
            onView(withText(R.string.restart_confirm)).perform(click());
            onView(withId(R.id.score)).check(matches(withText(context.getString(R.string.score,0))));
            onView(withId(R.id.card_0)).check(matches(withText(R.string.card_back)));
            onView(withId(R.id.progress)).check(matches(withText(context.getString(R.string.progress,0,16))));
        }
    }
    @Test public void matchingCardsAreDisabledAndHighestScoreReturnsHome() {
        try(ActivityScenario<MainActivity> home=ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.play_game)).perform(scrollTo(),click());
            // ActivityScenario 不启动第二个页面；用 ActivityLifecycleMonitor 读取已显示的真实 Controller。
            int[] pair=new int[3];
            InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
                java.util.Collection<android.app.Activity> resumed=androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry.getInstance()
                        .getActivitiesInStage(androidx.test.runner.lifecycle.Stage.RESUMED);
                for(android.app.Activity a:resumed) if(a instanceof GameActivity) {
                    GameActivity gameActivity=(GameActivity)a;
                    CardMatchingGame game=gameActivity.getGame();
                    for(int i=0;i<game.size();i++) for(int j=i+1;j<game.size();j++) {
                        Card card=game.cardAtIndex(i);
                        int match=card.match(new Card[]{game.cardAtIndex(j)});
                        if(match>0) {
                            GridLayout grid=a.findViewById(R.id.card_grid);
                            pair[0]=grid.getChildAt(i).getId();pair[1]=grid.getChildAt(j).getId();pair[2]=match*4-2;return;
                        }
                    }
                }
            });
            assertNotEquals(0,pair[0]);
            onView(withId(pair[0])).perform(scrollTo(),click());
            onView(withId(pair[1])).perform(scrollTo(),click());
            onView(withId(pair[0])).check(matches(not(isEnabled())));
            onView(withId(pair[1])).check(matches(not(isEnabled())));
            onView(withId(R.id.score)).check(matches(withText(context.getString(R.string.score,pair[2]))));
            onView(withId(R.id.exit_game)).perform(scrollTo(),click());
            int[] best = new int[1];
            home.onActivity(a -> best[0]=((CardGameApplication)a.getApplication()).getProfile().getHighestScore());
            assertTrue(best[0]>=pair[2]);
            onView(withId(R.id.profile_best)).check(matches(withText(context.getString(R.string.best_score,best[0]))));
        }
    }
    @Test public void draftSurvivesActivityRecreation() {
        try(ActivityScenario<EditProfileActivity> scenario=ActivityScenario.launch(EditProfileActivity.class)) {
            onView(withId(R.id.edit_name)).perform(replaceText(context.getString(R.string.demo_name)),closeSoftKeyboard());
            onView(withId(R.id.gender_male)).perform(scrollTo(),click());
            onView(withId(R.id.hobby_game)).perform(scrollTo(),click());
            scenario.recreate();
            onView(withId(R.id.edit_name)).check(matches(withText(R.string.demo_name)));
            onView(withId(R.id.gender_male)).check(matches(isChecked()));
            onView(withId(R.id.hobby_game)).check(matches(isChecked()));
        }
    }
}
