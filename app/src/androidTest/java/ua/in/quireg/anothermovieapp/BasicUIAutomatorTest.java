package ua.in.quireg.anothermovieapp;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiCollection;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;
import android.util.Log;

import org.junit.Before;
import org.junit.runner.RunWith;

import java.io.File;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)

public class BasicUIAutomatorTest {
    private UiDevice mDevice;

    @Before
    public void before() {
        // Initialize UiDevice instance
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        assertThat(mDevice, notNullValue());

        // Start from the home screen
        mDevice.pressHome();

    }
    @org.junit.Test
    public void test() throws InterruptedException {
        openApp("ua.in.quireg.anothermovieapp");

        //UiObject2 editText = waitForObject(By.res("ua.in.quireg.anothermovieapp:id/numboard_pwd_edittext"));
        //UiCollection movies = new UiCollection(new UiSelector().resourceId("ua.in.quireg.anothermovieapp:id/movie_list_recycler_view"));

        UiScrollable moviesScrollable = new UiScrollable(new UiSelector()
                .resourceId("ua.in.quireg.anothermovieapp:id/movie_list_recycler_view"));
        mDevice.wait(Until.findObject(By.res("ua.in.quireg.anothermovieapp", "movie_list_recycler_view")), 5000);
        Thread.sleep(5000);
        int currentCount = 0;
        int totalCount  = moviesScrollable.getChildCount(new UiSelector().resourceId("ua.in.quireg.anothermovieapp:id/image"));

        while (true){
            try {

                UiObject movie =  moviesScrollable.getChildByInstance(new UiSelector().resourceId("ua.in.quireg.anothermovieapp:id/image"), currentCount);
                //UiObject movie =  movies.getChild(new UiSelector().resourceId("ua.in.quireg.anothermovieapp:id/image"), count);
                movie.click();
                Thread.sleep(1000);
                mDevice.pressBack();
                Thread.sleep(1000);
                if(currentCount == totalCount - 1){
                    moviesScrollable.scrollForward();
                    moviesScrollable.scrollForward();
                    totalCount = moviesScrollable.getChildCount(new UiSelector().resourceId("ua.in.quireg.anothermovieapp:id/image"));
                    currentCount = 0;
                    continue;

                }
                currentCount++;

            } catch (UiObjectNotFoundException e) {
                e.printStackTrace();
            }
        }
//        takeScreenshot("screenshot-1.png");
//
//        //editText.setText("123456");
//        UiObject2 protectObject = waitForObject(By.text("Submit"));
//        protectObject.click();
//
//        takeScreenshot("screenshot-2.png");

//        Thread.sleep(10000);
    }

    private void openApp(String packageName) {
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
    private void closeApp(){

    }

    private UiObject2 waitForObject(BySelector selector) throws InterruptedException {
        UiObject2 object = null;
        int timeout = 30000;
        int delay = 1000;
        long time = System.currentTimeMillis();
        while (object == null) {
            object = mDevice.findObject(selector);
            Thread.sleep(delay);
            if (System.currentTimeMillis() - timeout > time) {
                fail();
            }
        }
        return object;
    }

    private void takeScreenshot(String name) {
        Log.d("TEST", "takeScreenshot");
        String dir = String.format("%s/%s", Environment.getExternalStorageDirectory().getPath(), "test-screenshots");
        File theDir = new File(dir);
        if (!theDir.exists()) {
            theDir.mkdir();
        }
        mDevice.takeScreenshot(new File(String.format("%s/%s", dir, name)));
    }
}
