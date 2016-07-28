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
package com.zapp.library.merchant.util;

import com.zapp.library.merchant.ui.PBBAPopupCallback;
import com.zapp.library.merchant.ui.fragment.PBBAPopup;
import com.zapp.library.merchant.ui.fragment.PBBAPopupErrorFragment;
import com.zapp.library.merchant.ui.fragment.PBBAPopupFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

/**
 * Utility class which provides API for the Merchant App developers.
 *
 * @author msagi
 * @since 1.0.0
 */
@SuppressWarnings("CyclicClassDependency")
public final class PBBAAppUtils {

    /**
     * Constant for Pay by Bank app (Zapp) custom scheme.
     */
    private static final String ZAPP_SCHEME = "zapp";

    /**
     * Zapp specific URI format string.
     */
    @SuppressWarnings("HardcodedFileSeparator")
    private static final String ZAPP_URI_FORMAT_STRING = "%s://%s";

    /**
     * Hidden constructor for utility class.
     */
    private PBBAAppUtils() {
    }

    /**
     * Checks if there is any Pay by Bank app enabled CFI App installed on the device.
     *
     * @param context The context to use.
     * @return True if there is at least one PBBA enabled CFI App available, false otherwise.
     * @see #openBankingApp(Context, String)
     */
    public static boolean isCFIAppAvailable(@NonNull final Context context) {

        //noinspection ConstantConditions
        if (context == null) {
            throw new IllegalArgumentException("context == null");
        }

        final Intent zappIntent = new Intent();
        zappIntent.setData(new Uri.Builder().scheme(ZAPP_SCHEME).build());
        zappIntent.setAction(Intent.ACTION_VIEW);
        final ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(zappIntent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo != null;
    }

    /**
     * Open banking App for given context and secure token. Use this method if you do not use the Pay by Bank app Popup API (which takes care of the banking app
     * opening).
     *
     * @param context     The (activity or application) context to start the banking App.
     * @param secureToken The secure token of the payment for which the banking App is to be started.
     * @see #isCFIAppAvailable(Context)
     * @see #showPBBAPopup(FragmentActivity, String, String, PBBAPopupCallback)
     * @see #showPBBAErrorPopup(FragmentActivity, String, String, String, PBBAPopupCallback)
     */
    public static void openBankingApp(@NonNull Context context, @NonNull final String secureToken) {

        //noinspection ConstantConditions
        if (context == null) {
            throw new IllegalArgumentException("context == null");
        }

        if (TextUtils.isEmpty(secureToken)) {
            throw new IllegalArgumentException("secureToken is required");
        }

        final Uri zappUri = Uri.parse(String.format(ZAPP_URI_FORMAT_STRING, ZAPP_SCHEME, secureToken));
        final Intent bankingAppStartIntent = new Intent(Intent.ACTION_VIEW, zappUri);
        final boolean isActivityContext = context instanceof Activity;
        if (!isActivityContext) {
            bankingAppStartIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(bankingAppStartIntent);
    }

    /**
     * Show the Pay by Bank app popup. It opens the CFI App automatically (without displaying the popup) if the device has CFI App installed and the user has tapped
     * 'Open banking app' button. It closes itself automatically if the user taps the 'Open banking app' button. It closes any other Pay by Bank App popup currently open.
     *
     * @param activity    The activity to use.
     * @param secureToken The secure token for the payment.
     * @param brn         The BRN code for the payment.
     * @param callback    The callback listener for the popup. The popup keeps {@link java.lang.ref.WeakReference} to the callback.
     * @see PBBAPopupCallback
     * @see #showPBBAErrorPopup(FragmentActivity, String, String, String, PBBAPopupCallback)
     * @see #dismissPBBAPopup(FragmentActivity)
     * @see #setPBBAPopupCallback(FragmentActivity, PBBAPopupCallback)
     */
    @SuppressWarnings({"FeatureEnvy", "OverlyComplexMethod", "OverlyLongMethod", "ElementOnlyUsedFromTestCode"})
    public static void showPBBAPopup(@NonNull final FragmentActivity activity, @NonNull final String secureToken, @NonNull final String brn,
            @NonNull final PBBAPopupCallback callback) {

        verifyActivity(activity);

        if (TextUtils.isEmpty(secureToken)) {
            throw new IllegalArgumentException("secureToken is required");
        }

        if (TextUtils.isEmpty(brn)) {
            throw new IllegalArgumentException("BRN is required");
        }
        if (brn.length() != 6) {
            throw new IllegalArgumentException("BRN length is not 6 characters");
        }

        //noinspection ConstantConditions
        if (callback == null) {
            throw new IllegalArgumentException("callback == null");
        }

        final FragmentManager fragmentManager = activity.getSupportFragmentManager();
        final PBBAPopup fragment = (PBBAPopup) fragmentManager.findFragmentByTag(PBBAPopup.TAG);

        final boolean hasBankApp = isCFIAppAvailable(activity);
        if (hasBankApp) {
            final boolean openBankAppAutomatically = PBBALibraryUtils.isOpenBankingAppButtonClicked(activity);

            if (openBankAppAutomatically) {
                //close any open PBBA popup (e.g. error popup was displayed due to network error and retry request was successful with auto bank App open enabled)
                if (fragment != null) {
                    fragmentManager.beginTransaction().remove(fragment).commit();
                }
                openBankingApp(activity, secureToken);
                return;
            }
        } else {
            //disable auto bank opening in case of no PBBA enabled App installed (or has been uninstalled)
            PBBALibraryUtils.setOpenBankingAppButtonClicked(activity, false);
        }

        if (fragment instanceof PBBAPopupFragment) {
            final PBBAPopupFragment popupFragment = (PBBAPopupFragment) fragment;
            if (TextUtils.equals(popupFragment.getSecureToken(), secureToken) && TextUtils.equals(popupFragment.getBrn(), brn)) {
                //same type of popup is already displayed, only reconnect is needed
                popupFragment.setCallback(callback);
                return;
            }
        }

        final PBBAPopupFragment newFragment = PBBAPopupFragment.newInstance(secureToken, brn);
        newFragment.setCallback(callback);

        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (fragment != null) {
            transaction.remove(fragment);
        }
        transaction.add(newFragment, PBBAPopup.TAG).commit();
    }

    /**
     * Show the Pay by Bank app Error Popup. It closes any other Pay by Bank App popup currently open.
     *
     * @param activity     The activity to use.
     * @param errorCode    The error code to display (optional). The error code is appended to the error message in round brackets (e.g. if errorCode is "A12.3" then the
     *                     message displayed will be "Network error (A12.3)")
     * @param errorTitle   The error title to display (optional).
     * @param errorMessage The error message to display.
     * @param callback     The callback listener for the popup. The popup keeps {@link java.lang.ref.WeakReference} to the callback.
     * @see PBBAPopupCallback
     * @see #showPBBAPopup(FragmentActivity, String, String, PBBAPopupCallback)
     * @see #dismissPBBAPopup(FragmentActivity)
     * @see #setPBBAPopupCallback(FragmentActivity, PBBAPopupCallback)
     */
    @SuppressWarnings("ElementOnlyUsedFromTestCode")
    public static void showPBBAErrorPopup(@NonNull final FragmentActivity activity, @Nullable String errorCode, @Nullable String errorTitle,
            @NonNull final String errorMessage, @NonNull final PBBAPopupCallback callback) {

        verifyActivity(activity);

        if (TextUtils.isEmpty(errorMessage)) {
            throw new IllegalArgumentException("errorMessage is required");
        }
        //noinspection ConstantConditions
        if (callback == null) {
            throw new IllegalArgumentException("callback == null");
        }

        final FragmentManager fragmentManager = activity.getSupportFragmentManager();

        final PBBAPopup fragment = (PBBAPopup) fragmentManager.findFragmentByTag(PBBAPopup.TAG);

        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (fragment != null) {
            transaction.remove(fragment);
        }
        final PBBAPopupErrorFragment newFragment = PBBAPopupErrorFragment.newInstance(errorCode, errorTitle, errorMessage);
        newFragment.setCallback(callback);

        transaction.add(newFragment, PBBAPopup.TAG).commit();
    }

    /**
     * Set the callback listener for the popup currently displayed (if any). Use this method to reconnect to the popup in case of orientation change. If there is no
     * popup shown then this method has no effect. If the activity implements the {@link PBBAPopupCallback} interface then the popup automatically reconnects
     * to the activity on attach. This method takes precedence over the automatic reconnecting, so if this method has been called with a valid callback listener before
     * the popup is displayed then the callback interface given in this method will be applied on the popup and the callback interface implemented by the activity will
     * be ignored.
     *
     * @param activity The activity to use.
     * @param callback The callback listener for the popup. The popup keeps {@link java.lang.ref.WeakReference} to the callback.
     * @return True if the callback is set (the popup is displayed), false otherwise.
     * @see #showPBBAPopup(FragmentActivity, String, String, PBBAPopupCallback)
     * @see #showPBBAErrorPopup(FragmentActivity, String, String, String, PBBAPopupCallback)
     */
    @SuppressWarnings({"BooleanMethodNameMustStartWithQuestion", "ElementOnlyUsedFromTestCode"})
    public static boolean setPBBAPopupCallback(@NonNull final FragmentActivity activity, @NonNull final PBBAPopupCallback callback) {

        verifyActivity(activity);

        //noinspection ConstantConditions
        if (callback == null) {
            throw new IllegalArgumentException("callback == null");
        }

        final FragmentManager fragmentManager = activity.getSupportFragmentManager();
        final PBBAPopup fragment = (PBBAPopup) fragmentManager.findFragmentByTag(PBBAPopup.TAG);
        if (fragment != null) {
            fragment.setCallback(callback);
            return true;
        }

        return false;
    }


    /**
     * Dismiss the Pay by Bank app popup. This method does not have any effect if there is no PBBA popup displayed.
     *
     * @param activity The activity to use.
     * @return True if the popup was dismissed, false if there was no popup displayed (so there was nothing to dismiss).
     * @see #showPBBAPopup(FragmentActivity, String, String, PBBAPopupCallback)
     * @see #showPBBAErrorPopup(FragmentActivity, String, String, String, PBBAPopupCallback)
     */
    @SuppressWarnings({"BooleanMethodNameMustStartWithQuestion", "ElementOnlyUsedFromTestCode"})
    public static boolean dismissPBBAPopup(@NonNull final FragmentActivity activity) {

        verifyActivity(activity);

        final FragmentManager fragmentManager = activity.getSupportFragmentManager();
        final PBBAPopup fragment = (PBBAPopup) fragmentManager.findFragmentByTag(PBBAPopup.TAG);
        if (fragment != null) {
            fragment.dismissWithCallback();
            return true;
        }

        return false;
    }

    /**
     * Verify if given activity is valid and can be used for the APIs.
     *
     * @param activity The activity to be checked.
     * @throws IllegalArgumentException If the given activity is null, finishing or destroyed.
     */
    private static void verifyActivity(@Nullable FragmentActivity activity) {

        if (activity == null) {
            throw new IllegalArgumentException("activity == null");
        }

        if (activity.isFinishing()) {
            throw new IllegalArgumentException("activity is finishing");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (activity.isDestroyed()) {
                throw new IllegalArgumentException("activity is destroyed");
            }
        }
    }
}
