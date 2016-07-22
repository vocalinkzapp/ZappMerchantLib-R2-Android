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
package com.zapp.library.merchant.test.pbbaapputils;

import com.zapp.library.merchant.R;
import com.zapp.library.merchant.test.TestActivity;
import com.zapp.library.merchant.test.TestSuite;
import com.zapp.library.merchant.test.TestUtils;
import com.zapp.library.merchant.ui.PBBAPopupCallback;
import com.zapp.library.merchant.util.PBBAAppUtils;
import com.zapp.library.merchant.util.PBBALibraryUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.matcher.IntentMatchers;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.FragmentActivity;

/**
 * Instrumented unit tests for PBBAAppUtils class. Note: run the test with the device in portrait mode, otherwise rotation tests will fail.
 *
 * @author msagi
 */
@SuppressWarnings({"OverlyBroadThrowsClause", "ProhibitedExceptionDeclared", "InstanceMethodNamingConvention", "ConstantConditions", "CyclicClassDependency"})
@RunWith(AndroidJUnit4.class)
@SmallTest
public class ShowPBBAPopupTest {

    /**
     * Flag to track if callback onRetryPaymentRequest has been called.
     */
    private boolean mOnRetryPaymentRequestCalled;

    /**
     * Flag to track if callback onDismissPopup has been called.
     */
    private boolean mOnDismissPopupCalled;

    /**
     * Callback interface for popups.
     */
    private final PBBAPopupCallback mCallback = new PBBAPopupCallback() {
        @Override
        public void onRetryPaymentRequest() {
            mOnRetryPaymentRequestCalled = true;
        }

        @Override
        public void onDismissPopup() {
            mOnDismissPopupCalled = true;
        }
    };

    @SuppressWarnings("PublicField")
    @Rule
    public ActivityTestRule<TestActivity> mActivityTestRule = new ActivityTestRule<>(TestActivity.class, /* initialTouchMode */ true, /* launchActivity */ true);

    @Before
    public void setUp() {
        final Activity activity = mActivityTestRule.getActivity();
        PBBALibraryUtils.setOpenBankingAppButtonClicked(activity, Boolean.FALSE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testShowPBBAPopupNulls() throws Exception {
        PBBAAppUtils.showPBBAPopup(/* fragmentActivity */ null, /* secureToken */ null, /* brn */ null, /* mCallback */ null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testShowPBBAPopupNoSecureToken() throws Exception {
        final TestActivity activity = mActivityTestRule.getActivity();
        PBBAAppUtils.showPBBAPopup(/* fragmentActivity */ activity, /* secureToken */ null, TestSuite.BRN, /* mCallback */ null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testShowPBBAPopupNoBRN() throws Exception {
        final TestActivity activity = mActivityTestRule.getActivity();
        PBBAAppUtils.showPBBAPopup(/* fragmentActivity */ activity, TestSuite.SECURE_TOKEN, /* brn */ null, /* mCallback */ null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testShowPBBAPopupInvalidBRN() throws Exception {
        final TestActivity activity = mActivityTestRule.getActivity();
        PBBAAppUtils.showPBBAPopup(/* fragmentActivity */ activity, TestSuite.SECURE_TOKEN, TestSuite.INVALID_BRN, /* mCallback */ null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testShowPBBAPopupNoCallback() throws Exception {
        final TestActivity activity = mActivityTestRule.getActivity();
        PBBAAppUtils.showPBBAPopup(/* fragmentActivity */ activity, TestSuite.SECURE_TOKEN, TestSuite.BRN, /* mCallback */ null);
    }

    @Test
    public void testShowPBBAPopupOK() throws Exception {
        final TestActivity activity = mActivityTestRule.getActivity();
        PBBAAppUtils.showPBBAPopup(/* fragmentActivity */ activity, TestSuite.SECURE_TOKEN, TestSuite.BRN, mCallback);
        Assert.assertFalse(TestSuite.ON_RETRY_PAYMENT_REQUEST_SHOULD_NOT_BE_CALLED, mOnRetryPaymentRequestCalled);
        Assert.assertFalse(TestSuite.ON_DISMISS_POPUP_CALLED_SHOULD_NOT_BE_CALLED, mOnDismissPopupCalled);
        Espresso.onView(ViewMatchers.withId(R.id.pbba_popup_close)).perform(ViewActions.click());
        Assert.assertFalse(TestSuite.ON_RETRY_PAYMENT_REQUEST_SHOULD_NOT_BE_CALLED, mOnRetryPaymentRequestCalled);
        Assert.assertTrue(TestSuite.ON_DISMISS_POPUP_CALLED_SHOULD_BE_CALLED, mOnDismissPopupCalled);
    }

    @Test
    public void testShowPBBAPopupAndAutoOpenOK() throws Exception {
        Intents.init();
        final TestActivity activity = mActivityTestRule.getActivity();
        final Intent resultData = new Intent();
        final Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
        Intents.intending(IntentMatchers.hasData(TestSuite.OPEN_BANKING_APP_INTENT_URI)).respondWith(result);

        PBBALibraryUtils.setOpenBankingAppButtonClicked(activity, Boolean.TRUE);

        PBBAAppUtils.showPBBAPopup(/* fragmentActivity */ activity, TestSuite.SECURE_TOKEN, TestSuite.BRN, mCallback);

        Intents.release();
    }

    @Test
    public void testShowPBBAPopupAndRotateOK() throws Exception {
        final TestActivity activity = mActivityTestRule.getActivity();
        PBBAAppUtils.showPBBAPopup(/* fragmentActivity */ activity, TestSuite.SECURE_TOKEN, TestSuite.BRN, mCallback);
        Assert.assertFalse(TestSuite.ON_RETRY_PAYMENT_REQUEST_SHOULD_NOT_BE_CALLED, mOnRetryPaymentRequestCalled);
        Assert.assertFalse(TestSuite.ON_DISMISS_POPUP_CALLED_SHOULD_NOT_BE_CALLED, mOnDismissPopupCalled);

        final FragmentActivity landscapeActivity = TestUtils.changeOrientationWithPopup(activity, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //mCallback should be lost due to orientation change
        Assert.assertFalse(TestSuite.ON_RETRY_PAYMENT_REQUEST_SHOULD_NOT_BE_CALLED, mOnRetryPaymentRequestCalled);
        Assert.assertFalse(TestSuite.ON_DISMISS_POPUP_CALLED_SHOULD_NOT_BE_CALLED, mOnDismissPopupCalled);
        Espresso.onView(ViewMatchers.withId(R.id.pbba_popup_close)).perform(ViewActions.click());
        Assert.assertFalse(TestSuite.ON_RETRY_PAYMENT_REQUEST_SHOULD_NOT_BE_CALLED, mOnRetryPaymentRequestCalled);
        Assert.assertFalse(TestSuite.ON_DISMISS_POPUP_CALLED_SHOULD_NOT_BE_CALLED, mOnDismissPopupCalled);

        landscapeActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Test
    public void testShowPBBAPopupAndRotateWithReconnectOK() throws Exception {

        TestActivity activity = mActivityTestRule.getActivity();
        PBBAAppUtils.showPBBAPopup(/* fragmentActivity */ activity, TestSuite.SECURE_TOKEN, TestSuite.BRN, mCallback);
        Assert.assertFalse(TestSuite.ON_RETRY_PAYMENT_REQUEST_SHOULD_NOT_BE_CALLED, mOnRetryPaymentRequestCalled);
        Assert.assertFalse(TestSuite.ON_DISMISS_POPUP_CALLED_SHOULD_NOT_BE_CALLED, mOnDismissPopupCalled);

        final FragmentActivity landscapeActivity = TestUtils.changeOrientationWithPopup(activity, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //refresh activity reference after orientation change
        PBBAAppUtils.setPBBAPopupCallback(landscapeActivity, mCallback);

        Espresso.onView(ViewMatchers.withId(R.id.pbba_popup_close)).perform(ViewActions.click());
        //mCallback .onDismissPopup should be called after mCallback reconnect
        Assert.assertFalse(TestSuite.ON_RETRY_PAYMENT_REQUEST_SHOULD_NOT_BE_CALLED, mOnRetryPaymentRequestCalled);
        Assert.assertTrue(TestSuite.ON_DISMISS_POPUP_CALLED_SHOULD_BE_CALLED, mOnDismissPopupCalled);

        landscapeActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}