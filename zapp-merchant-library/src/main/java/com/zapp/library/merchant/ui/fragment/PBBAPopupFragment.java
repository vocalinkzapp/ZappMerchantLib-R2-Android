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

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zapp.library.merchant.R;
import com.zapp.library.merchant.ui.PBBAPopupCallback;
import com.zapp.library.merchant.ui.view.CustomTextView;
import com.zapp.library.merchant.util.PBBAAppUtils;
import com.zapp.library.merchant.util.PBBALibraryUtils;

import static com.zapp.library.merchant.util.PBBALibraryUtils.CFI_APP_NAME_DEFAULT;

/**
 * Popup fragment for Zapp electronic and mobile commerce (e-Comm and m-Comm) journeys.
 *
 * @author msagi
 * @since 1.0.0
 */
@SuppressWarnings("CyclicClassDependency")
public final class PBBAPopupFragment extends PBBAPopup {

    /**
     * Key for fragment argument: BRN.
     */
    private static final String KEY_BRN = "key.brn";

    /**
     * Key for fragment argument: Secure Token.
     */
    private static final String KEY_SECURE_TOKEN = "key.secureToken";

    /**
     * The BRN code for this popup.
     */
    private String mBrn;

    /**
     * The secure token for this popup.
     */
    private String mSecureToken;

    /**
     * Flag to track if 'Get code' button has been clicked on the m-Comm view.
     */
    @SuppressWarnings("BooleanVariableAlwaysNegated")
    private boolean mIsGetCodeButtonClicked;

    /**
     * Create new instance.
     *
     * @param secureToken The secure token to use to open the PBBA enabled CFI App.
     * @param brn         The BRN code to display.
     * @return The new fragment instance.
     */
    @SuppressWarnings("TypeMayBeWeakened")
    public static PBBAPopupFragment newInstance(@NonNull final String secureToken, @NonNull final String brn) {
        final PBBAPopupFragment fragment = new PBBAPopupFragment();
        final Bundle arguments = new Bundle();
        arguments.putSerializable(KEY_SECURE_TOKEN, secureToken);
        arguments.putSerializable(KEY_BRN, brn);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle arguments = getArguments();
        mBrn = arguments.getString(KEY_BRN);
        mSecureToken = arguments.getString(KEY_SECURE_TOKEN);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {

        final Context context = getContext();

        final boolean hasBankApp = PBBAAppUtils.isCFIAppAvailable(context);
        final View content;
        if (hasBankApp && !mIsGetCodeButtonClicked) {
            content = inflater.inflate(R.layout.pbba_popup_fragment_mcomm, container, false);
            onCreateMCommView(content);
        } else {
            content = inflater.inflate(R.layout.pbba_popup_fragment_ecomm, container, false);
            onCreateECommView(content);
        }

        final boolean useDefaultCfiAppName = PBBALibraryUtils.getCfiAppName(getContext()) == CFI_APP_NAME_DEFAULT;

        final String cfiAppName = useDefaultCfiAppName ? getString(R.string.pbba_name) : getString(R.string.pingit);

        CustomTextView openBankingAppCallToAction = (CustomTextView) content.findViewById(R.id.pbba_popup_open_banking_app_call_to_action);
        if (openBankingAppCallToAction != null) {
            openBankingAppCallToAction.setText(getString(R.string.pbba_popup_open_banking_app_call_to_action, cfiAppName));
        }

        CustomTextView payWithAnotherDeviceCallToAction = (CustomTextView) content.findViewById(R.id.pbba_popup_pay_with_another_device_call_to_action);
        if (payWithAnotherDeviceCallToAction != null) {
            payWithAnotherDeviceCallToAction.setText(getString(R.string.pbba_popup_pay_with_another_device_call_to_action, cfiAppName));
        }

        CustomTextView popupEcomItemTwoText = (CustomTextView) content.findViewById(R.id.pbba_popup_ecom_item_two_text);
        if (popupEcomItemTwoText != null) {
            popupEcomItemTwoText.setHtmlText(getString(R.string.pbba_popup_ecomm_item_two_text, cfiAppName));
        }

        final TextView openBankingAppButtonTitle = (TextView) content.findViewById(R.id.pbba_button_open_banking_app_text);
        if (openBankingAppButtonTitle != null) {
            if (useDefaultCfiAppName) {
                openBankingAppButtonTitle.setText(getString(R.string.pbba_button_text_open_banking_app));
            } else {
                openBankingAppButtonTitle.setText(getString(R.string.pbba_button_text_open_banking_app_co_branded));
            }
        }

        //note: 'What is Pay by Bank app' link is set up in the parent class

        return content;
    }

    private void onCreateECommView(@NonNull final View view) {

        final boolean isBankAppAvailable = PBBAAppUtils.isCFIAppAvailable(getActivity());
        if (isBankAppAvailable) {
            final View container = view.findViewById(R.id.pbba_ecomm_popup_body_no_bank_app_messages);
            if (container != null) {
                //we have this container on tablet layouts
                container.setVisibility(View.GONE);
            }
        }
        onCreateBrnView(view, mBrn);
    }

    private void onCreateMCommView(@NonNull final View view) {

        final View openBankingAppButton = view.findViewById(R.id.pbba_button_open_banking_app);
        openBankingAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final Activity activity = getActivity();
                if (activity != null) {
                    PBBALibraryUtils.setOpenBankingAppButtonClicked(getContext(), true);
                    if (PBBAAppUtils.isCFIAppAvailable(activity)) {
                        dismiss();
                        PBBAAppUtils.openBankingApp(activity, mSecureToken);
                    } else {
                        onDisplayBrn();
                    }
                }
            }
        });

        final View getCodeButton = view.findViewById(R.id.pbba_button_get_code);
        if (getCodeButton != null) {
            //'Get Code' button appears on phone layout only
            getCodeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    onDisplayBrn();
                }
            });
        }

        final View codeContainer = view.findViewById(R.id.pbba_popup_code_container);
        onCreateBrnView(codeContainer, mBrn);
    }

    private void setGetCodeButtonClicked() {
        mIsGetCodeButtonClicked = true;
    }

    private static void onCreateBrnView(@Nullable final View view, @NonNull final String brn) {
        if (view != null && !TextUtils.isEmpty(brn)) {
            final char[] code = brn.toCharArray();
            if (code.length == 6) {
                ((TextView) view.findViewById(R.id.code_value_1)).setText(String.valueOf(code[0]));
                ((TextView) view.findViewById(R.id.code_value_2)).setText(String.valueOf(code[1]));
                ((TextView) view.findViewById(R.id.code_value_3)).setText(String.valueOf(code[2]));
                ((TextView) view.findViewById(R.id.code_value_4)).setText(String.valueOf(code[3]));
                ((TextView) view.findViewById(R.id.code_value_5)).setText(String.valueOf(code[4]));
                ((TextView) view.findViewById(R.id.code_value_6)).setText(String.valueOf(code[5]));
            }
        }
    }

    private void onDisplayBrn() {
        final FragmentActivity activity = getActivity();
        final FragmentManager fragmentManager = activity.getSupportFragmentManager();
        final PBBAPopupFragment newFragment = PBBAPopupFragment.newInstance(mSecureToken, mBrn);
        newFragment.setGetCodeButtonClicked();
        final PBBAPopupCallback callback = getCallback();
        if (callback != null) {
            newFragment.setCallback(callback);
        }
        fragmentManager.beginTransaction().remove(this).add(newFragment, TAG).commit();
    }

    /**
     * Get the BRN code displayed in the fragment.
     *
     * @return The BRN code value.
     */
    @NonNull
    public String getBrn() {
        return mBrn;
    }

    /**
     * Get the secure token stored in the fragment.
     *
     * @return The secure token value.
     */
    @NonNull
    public String getSecureToken() {
        return mSecureToken;
    }
}
