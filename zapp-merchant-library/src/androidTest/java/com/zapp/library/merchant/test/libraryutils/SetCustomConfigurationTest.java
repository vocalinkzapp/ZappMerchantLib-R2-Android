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
package com.zapp.library.merchant.test.libraryutils;

import com.zapp.library.merchant.test.TestActivity;
import com.zapp.library.merchant.util.PBBALibraryUtils;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

/**
 * Instrumented unit tests for PBBAAppUtils class.
 *
 * @author msagi
 */
@SuppressWarnings({"ProhibitedExceptionDeclared", "ConstantConditions", "FeatureEnvy"})
@RunWith(AndroidJUnit4.class)
@SmallTest
public class SetCustomConfigurationTest {

    @Rule
    public ActivityTestRule<TestActivity> mActivityTestRule = new ActivityTestRule<>(TestActivity.class, /* initialTouchMode */ true, /* launchActivity */ true);

    @Before
    public void setUp() {
    }

    @Test
    public void testSetCustomConfigurationNulls() throws Exception {
        final TestActivity activity = mActivityTestRule.getActivity();
        int pbbaTheme = PBBALibraryUtils.getPbbaTheme(activity);
        PBBALibraryUtils.setCustomConfiguration(/* properties */ null);
        int pbbaTheme2 = PBBALibraryUtils.getPbbaTheme(activity);
        Assert.assertEquals(pbbaTheme, pbbaTheme2);
    }

    @After
    public void tearDown() {
    }
}