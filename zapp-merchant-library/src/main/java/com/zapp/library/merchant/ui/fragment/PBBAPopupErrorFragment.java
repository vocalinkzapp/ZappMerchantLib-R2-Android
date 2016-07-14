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
import com.zapp.library.merchant.ui.view.PBBAButton;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Pay by Bank app popup implementation for error popups.
 *
 * @author msagi
 * @since 1.0.0
 */
public final class PBBAPopupErrorFragment extends PBBAPopup {

    /**
     * Key for fragment argument: Error Code.
     */
    private static final String KEY_ERROR_CODE = "key.errorCode";

    /**
     * Key for fragment argument: Error Title.
     */
    private static final String KEY_ERROR_TITLE = "key.errorTitle";

    /**
     * Key for fragment argument: Error Message.
     */
    private static final String KEY_ERROR_MESSAGE = "key.errorMessage";


    @Nullable
    private String mErrorCode;

    @Nullable
    private String mErrorTitle;

    @NonNull
    private String mErrorMessage = "";

    /**
     * Create new instance.
     *
     * @param errorCode    The error code to use (optional).
     * @param errorTitle   The error title to use (optional).
     * @param errorMessage The error message to use.
     * @return The error popup instance.
     */
    public static PBBAPopupErrorFragment newInstance(@Nullable final String errorCode, @Nullable final String errorTitle, @NonNull final String errorMessage) {
        final PBBAPopupErrorFragment fragment = new PBBAPopupErrorFragment();
        final Bundle arguments = new Bundle();
        arguments.putString(KEY_ERROR_CODE, errorCode);
        arguments.putString(KEY_ERROR_TITLE, errorTitle);
        arguments.putString(KEY_ERROR_MESSAGE, errorMessage);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle arguments = getArguments();
        mErrorCode = arguments.getString(KEY_ERROR_CODE);
        mErrorTitle = arguments.getString(KEY_ERROR_TITLE);

        final String errorMessage = arguments.getString(KEY_ERROR_MESSAGE);
        if (!TextUtils.isEmpty(errorMessage)) {
            mErrorMessage = errorMessage;
        }
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final View content = inflater.inflate(R.layout.pbba_popup_fragment_error, container, false);
        final Bundle arguments = getArguments();

        final TextView titleTextView = (TextView) content.findViewById(R.id.pbba_popup_error_title_text);
        final String errorTitle = arguments.getString(KEY_ERROR_TITLE);
        if (TextUtils.isEmpty(errorTitle)) {
            titleTextView.setText(getString(R.string.pbba_popup_error_generic_title));
        } else {
            titleTextView.setText(errorTitle);
        }

        final TextView messageTextView = (TextView) content.findViewById(R.id.pbba_popup_error_message_text);
        final String errorCode = arguments.getString(KEY_ERROR_CODE);
        final String errorMessage = arguments.getString(KEY_ERROR_MESSAGE);
        if (TextUtils.isEmpty(errorMessage)) {
            messageTextView.setText(getString(R.string.pbba_popup_error_generic_message));
        } else {
            final String errorMessageText = TextUtils.isEmpty(errorCode) ? errorMessage : String.format("%s (%s)", errorMessage, errorCode);
            messageTextView.setText(errorMessageText);
        }

        final PBBAButton pbbaButton = (PBBAButton) content.findViewById(R.id.pbba_button);
        pbbaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final PBBAPopupCallback callback = getCallback();
                if (callback != null) {
                    callback.onRetryPaymentRequest();
                }
            }
        });

        return content;
    }

    /**
     * Get the error code displayed in the fragment.
     *
     * @return The error code value (if any).
     */
    @Nullable
    public String getErrorCode() {
        return mErrorCode;
    }

    /**
     * Get the error title displayed in the fragment.
     *
     * @return The error title value (if any).
     */
    @Nullable
    public String getErrorTitle() {
        return mErrorTitle;
    }

    /**
     * Get the error message displayed in the fragment.
     *
     * @return The error message value.
     */
    @NonNull
    public String getErrorMessage() {
        return mErrorMessage;
    }
}
