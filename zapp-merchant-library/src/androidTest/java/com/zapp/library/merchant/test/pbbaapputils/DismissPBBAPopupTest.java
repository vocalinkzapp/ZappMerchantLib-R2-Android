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
import com.zapp.library.merchant.ui.PBBAPopupCallback;
import com.zapp.library.merchant.util.PBBAAppUtils;
import com.zapp.library.merchant.util.PBBALibraryUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.app.Activity;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

/**
 * Instrumented unit tests for PBBAAppUtils class.
 *
 * @author msagi
 */
@SuppressWarnings({"ProhibitedExceptionDeclared", "InstanceMethodNamingConvention", "ConstantConditions", "CyclicClassDependency"})
@RunWith(AndroidJUnit4.class)
@SmallTest
public class DismissPBBAPopupTest {

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
    public void testDismissPBBAPopupNull() throws Exception {
        PBBAAppUtils.dismissPBBAPopup(/* fragmentActivity */ null);
    }

    @Test
    public void testDismissPBBAPopupNoPopupDisplayedOK() throws Exception {
        final TestActivity activity = mActivityTestRule.getActivity();
        final boolean isDismissed = PBBAAppUtils.dismissPBBAPopup(activity);
        Assert.assertFalse(mOnRetryPaymentRequestCalled);
        Assert.assertFalse(mOnDismissPopupCalled);
        Assert.assertFalse(isDismissed);
    }

    @Test
    public void testDismissPBBAPopupPopupDisplayedOK() throws Exception {
        final TestActivity activity = mActivityTestRule.getActivity();
        PBBAAppUtils.showPBBAPopup(activity, TestSuite.SECURE_TOKEN, TestSuite.BRN, mCallback);
        //wait until popup appears
        Espresso.onView(ViewMatchers.withId(R.id.pbba_popup_container)).perform(ViewActions.click());
        Assert.assertFalse(mOnRetryPaymentRequestCalled);
        Assert.assertFalse(mOnDismissPopupCalled);
        final boolean isDismissed = PBBAAppUtils.dismissPBBAPopup(activity);
        Assert.assertFalse(mOnRetryPaymentRequestCalled);
        Assert.assertTrue(mOnDismissPopupCalled);
        Assert.assertTrue(isDismissed);
    }

}