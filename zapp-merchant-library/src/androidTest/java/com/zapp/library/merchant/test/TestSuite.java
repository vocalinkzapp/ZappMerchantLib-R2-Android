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

import com.zapp.library.merchant.test.libraryutils.SetCustomConfigurationTest;
import com.zapp.library.merchant.test.pbbaapputils.DismissPBBAPopupTest;
import com.zapp.library.merchant.test.pbbaapputils.IsCFIAppAvailableTest;
import com.zapp.library.merchant.test.pbbaapputils.OpenBankingAppTest;
import com.zapp.library.merchant.test.pbbaapputils.ShowPBBAErrorPopupTest;
import com.zapp.library.merchant.test.pbbaapputils.ShowPBBAPopupTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite for Zapp Merchant Library.
 *
 * @author msagi
 */
@SuppressWarnings("CyclicClassDependency")
@RunWith(Suite.class)
@Suite.SuiteClasses({
        IsCFIAppAvailableTest.class,
        OpenBankingAppTest.class,
        ShowPBBAPopupTest.class,
        ShowPBBAErrorPopupTest.class,
        DismissPBBAPopupTest.class,

        SetCustomConfigurationTest.class,
})
public class TestSuite {

    /**
     * Assert message.
     */
    @SuppressWarnings("ConstantNamingConvention")
    public static final String ON_DISMISS_POPUP_CALLED_SHOULD_NOT_BE_CALLED = ".onDismissPopupCalled() should not be called";

    /**
     * Assert message.
     */
    @SuppressWarnings("ConstantNamingConvention")
    public static final String ON_DISMISS_POPUP_CALLED_SHOULD_BE_CALLED = ".onDismissPopupCalled() should be called";

    /**
     * Assert message.
     */
    @SuppressWarnings("ConstantNamingConvention")
    public static final String ON_RETRY_PAYMENT_REQUEST_SHOULD_NOT_BE_CALLED = ".onRetryPaymentRequest() should not be called";

    /**
     * Assert message.
     */
    @SuppressWarnings("ConstantNamingConvention")
    public static final String ON_RETRY_PAYMENT_REQUEST_SHOULD_BE_CALLED = ".onRetryPaymentRequest() should be called";

    /**
     * Constant BRN code value.
     */
    @SuppressWarnings("ConstantNamingConvention")
    public static final String BRN = "BRNBRN";

    /**
     * Constant (invalid: not 6 characters long) BRN code value.
     */
    public static final String INVALID_BRN = "BRN";

    /**
     * Constant with another valid BRN code value.
     */
    @SuppressWarnings("ConstantNamingConvention")
    public static final String BRN2 = "BRN--2";

    /**
     * Constant secure token value.
     */
    public static final String SECURE_TOKEN = "secureToken";

    /**
     * Constant Pay by Bank app intent URI.
     */
    public static final String OPEN_BANKING_APP_INTENT_URI = String.format("zapp://%s", SECURE_TOKEN);

    /**
     * Constant error code value.
     */
    public static final String ERROR_CODE = "ErrorCode";

    /**
     * Constant error title value.
     */
    public static final String ERROR_TITLE = "Generic error title";

    /**
     * Constant error message value.
     */
    public static final String ERROR_MESSAGE = "Generic error message with a longer text";
}
