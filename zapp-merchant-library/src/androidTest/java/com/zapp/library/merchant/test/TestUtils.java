/*
 * Copyright 2016 IPCO 2012 Limited
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.zapp.library.merchant.test;

import com.zapp.library.merchant.ui.fragment.PBBAPopup;
import com.zapp.library.merchant.util.PBBAAppUtils;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.support.v4.app.FragmentActivity;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Locale;

/**
 * Utility class for testing.
 *
 * @author msagi
 */
public class TestUtils {

    private static class Wrapper<T> {
        private T mObject;
        public Wrapper(T object) {
            mObject = object;
        }
        public T get() {
            return mObject;
        }
        public void set(T object) {
            mObject = object;
        }
    }

    /**
     * Timeout in milliseconds for .wait() calls.
     */
    private static final long WAIT_TIMEOUT_MS = 10000L;

    /**
     * Change orientation of given activity and wait until the popup re-appears.
     *
     * @param activity The activity to rotate.
     * @param newOrientation The new orientation to change to.
     * @throws InterruptedException If thread synchronisation is interrupted.
     * @return The new activity after orientation change.
     */
    @SuppressWarnings({"SynchronizationOnLocalVariableOrMethodParameter", "UnconditionalWait", "WaitNotInLoop"})
    public static FragmentActivity changeOrientationWithPopup(@NonNull final FragmentActivity activity, final int newOrientation) throws InterruptedException {
        final Object monitor = new Object();
        final Wrapper<FragmentActivity> wrapper = new Wrapper<>(activity);

        activity.setRequestedOrientation(newOrientation);
        //wait until the popup re-appears
        Espresso.onView(ViewMatchers.withId(R.id.pbba_popup_container)).perform(ViewActions.click());

        activity.runOnUiThread(new Runnable() {
            @SuppressWarnings("NakedNotify")
            @Override
            public void run() {
                final Collection<Activity> activities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
                final int numberOfActivities = activities.size();
                if (numberOfActivities != 1) {
                    throw new IllegalStateException(String.format(Locale.ENGLISH, "activities.size() = %d", numberOfActivities));
                }
                final TestActivity landscapeActivity = (TestActivity) activities.iterator().next();

                wrapper.set(landscapeActivity);

                synchronized (monitor) {
                    //noinspection NakedNotify
                    monitor.notifyAll();
                }
            }
        });

        synchronized (monitor) {
            //wait for popup callback re-connect on the UI thread.
            monitor.wait(WAIT_TIMEOUT_MS);
        }

        return wrapper.get();
    }
}
