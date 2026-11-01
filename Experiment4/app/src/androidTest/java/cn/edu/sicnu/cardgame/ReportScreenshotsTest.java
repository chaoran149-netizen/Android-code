package cn.edu.sicnu.cardgame;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.GridLayout;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;
import cn.edu.sicnu.cardgame.model.Card;
import cn.edu.sicnu.cardgame.model.CardMatchingGame;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.File;
import java.io.FileOutputStream;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static org.junit.Assert.*;

/** 实际操作应用并采集实验报告插图；输出文件采用数字命名，无硬编码文案。 */
@RunWith(AndroidJUnit4.class)
public class ReportScreenshotsTest {
    private android.content.Context context;
    private void capture(int stage) throws Exception {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        InstrumentationRegistry.getInstrumentation().getUiAutomation().waitForIdle(300,5000);
        Bitmap image=InstrumentationRegistry.getInstrumentation().getUiAutomation().takeScreenshot();
        assertNotNull(image);
        File file=new File(context.getExternalFilesDir(null),Integer.toString(stage));
        try(FileOutputStream output=new FileOutputStream(file)) { assertTrue(image.compress(Bitmap.CompressFormat.PNG,100,output)); }
    }
    @Test public void captureProfileEditBoardAndMatchedResult() throws Exception {
        context=InstrumentationRegistry.getInstrumentation().getTargetContext();
        try(ActivityScenario<MainActivity> home=ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.edit_profile)).perform(scrollTo(),click());
            onView(withId(R.id.edit_name)).perform(replaceText(context.getString(R.string.demo_name)),closeSoftKeyboard());
            onView(withId(R.id.edit_student)).perform(replaceText(context.getString(R.string.demo_student)),closeSoftKeyboard());
            onView(withId(R.id.edit_phone)).perform(replaceText(context.getString(R.string.demo_phone)),closeSoftKeyboard());
            onView(withId(R.id.edit_email)).perform(replaceText(context.getString(R.string.demo_email)),closeSoftKeyboard());
            onView(withId(R.id.gender_male)).perform(scrollTo(),click());
            onView(withId(R.id.hobby_game)).perform(scrollTo(),click());
            onView(withId(R.id.hobby_reading)).perform(scrollTo(),click());
            onView(withId(R.id.edit_name)).perform(scrollTo());
            capture(2);
            onView(withId(R.id.save_profile)).perform(scrollTo(),click());
            onView(withId(R.id.profile_name)).check(matches(withText(context.getString(R.string.profile_row,
                    context.getString(R.string.name_label),context.getString(R.string.demo_name)))));
            capture(1);
            onView(withId(R.id.play_game)).perform(scrollTo(),click());
            onView(withId(R.id.score)).perform(scrollTo());
            capture(3);
            int[] pair=new int[3];
            InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
                for(Activity a: ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED)) {
                    if(!(a instanceof GameActivity))continue;
                    CardMatchingGame game=((GameActivity)a).getGame();
                    for(int i=0;i<game.size();i++)for(int j=i+1;j<game.size();j++) {
                        int match=game.cardAtIndex(i).match(new Card[]{game.cardAtIndex(j)});
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
            onView(withId(R.id.score)).perform(scrollTo()).check(matches(withText(context.getString(R.string.score,pair[2]))));
            capture(4);
            onView(withId(R.id.exit_game)).perform(scrollTo(),click());
        }
    }
}
