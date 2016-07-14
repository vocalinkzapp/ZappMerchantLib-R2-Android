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

package com.zapp.library.merchant.ui;

import android.support.v4.app.FragmentActivity;

/**
 * Callback interface to propagate events from Pay by Bank app popups to the Merchant App.
 * <p>
 * The Pay by Bank app popups keep {@link java.lang.ref.WeakReference} to the popup callbacks in order to avoid context leaking (e.g. on orientation change).
 *
 * @author msagi
 * @since 1.0.0
 */
public interface PBBAPopupCallback {

    /**
     * Event callback for tap on the 'Pay by Bank app' button on the Pay by Bank app Error Popup event. The Error Popup is displayed if any error happens during
     * initiating the Pay by Bank app payment. The Merchant App retries to initiate the payment if it receives this callback. The error popup stays visible
     * until the retry request is completed and either another Error Popup or a normal Popup is displayed.
     *
     * @see com.zapp.library.merchant.util.PBBAAppUtils#showPBBAErrorPopup(FragmentActivity, String, String, String, PBBAPopupCallback)
     */
    void onRetryPaymentRequest();

    /**
     * Event callback for tap on the 'X' (top-right corner, popup dismiss) button on any Pay by Bank app popup. This callback method is also called in case of dismissing
     * any Pay by Bank app using the {@link com.zapp.library.merchant.util.PBBAAppUtils#dismissPBBAPopup(FragmentActivity)} API.
     *
     * @see com.zapp.library.merchant.util.PBBAAppUtils#showPBBAPopup(FragmentActivity, String, String, PBBAPopupCallback)
     * @see com.zapp.library.merchant.util.PBBAAppUtils#showPBBAErrorPopup(FragmentActivity, String, String, String, PBBAPopupCallback)
     * @see com.zapp.library.merchant.util.PBBAAppUtils#dismissPBBAPopup(FragmentActivity)
     */
    void onDismissPopup();

}
