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
package com.zapp.library.merchant.ui.fragment;

import com.zapp.library.merchant.R;
import com.zapp.library.merchant.ui.PBBAPopupCallback;
import com.zapp.library.merchant.util.PBBALibraryUtils;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * The base Pay by Bank app Popup Fragment.
 *
 * @author msagi
 * @since 1.0.0
 */
@SuppressWarnings("AbstractClassExtendsConcreteClass")
public abstract class PBBAPopup extends DialogFragment {

    /**
     * Tag for logging and fragment tagging.
     */
    @SuppressWarnings("ConstantNamingConvention")
    public static final String TAG = PBBAPopup.class.getSimpleName();

    /**
     * Web link to 'what is Pay by Bank app' web page.
     */
    private static final String WHAT_IS_PAY_BY_BANK_APP_LINK = "http://www.paybybankapp.co.uk/how-it-works/the-experience/";

    /**
     * The (weak reference to the) callback interface to the popup controller.
     */
    @Nullable
    private WeakReference<PBBAPopupCallback> mCallbackWeakReference;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setStyle(STYLE_NORMAL, R.style.PBBAPopup_Screen);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);

        //auto re-connect if callback is not set and attached to a suitable context
        //noinspection InstanceofIncompatibleInterface
        if (context instanceof PBBAPopupCallback && getCallback() == null) {
            //noinspection CastToIncompatibleInterface
            setCallback((PBBAPopupCallback) context);
        }
    }

    /**
     * Set the callback listener.
     *
     * @param callback The callback listener instance.
     */
    public void setCallback(@NonNull final PBBAPopupCallback callback) {
        mCallbackWeakReference = new WeakReference<>(callback);
    }

    /**
     * Close the popup and fire dismiss event on the callback interface.
     */
    public void dismissWithCallback() {
        final PBBAPopupCallback callback = getCallback();
        if (callback != null) {
            callback.onDismissPopup();
        }
        dismiss();
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ImageView closePopupButton = (ImageView) view.findViewById(R.id.pbba_popup_close);
        if (closePopupButton != null) {
            closePopupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    dismissWithCallback();
                }
            });
        }
        final View whatIsPayByBankAppLink = view.findViewById(R.id.pbba_link_what_is_pay_by_bank_app);
        if (whatIsPayByBankAppLink != null) {
            whatIsPayByBankAppLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    try {
                        final Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(WHAT_IS_PAY_BY_BANK_APP_LINK));
                        startActivity(myIntent);
                    } catch (ActivityNotFoundException ignored) {
                        //no web browser installed, ignore the error
                        Log.w(PBBALibraryUtils.PBBA_LOG_TAG, "Cannot start 'What is Pay by Bank app' link: no browser installed?");
                    }
                }
            });
        }
    }

    /**
     * Get the Pay by Bank app popup callback handler.
     *
     * @return The {@link PBBAPopupCallback callback}.
     */
    @Nullable
    protected PBBAPopupCallback getCallback() {
        return mCallbackWeakReference != null ? mCallbackWeakReference.get() : null;
    }

}
