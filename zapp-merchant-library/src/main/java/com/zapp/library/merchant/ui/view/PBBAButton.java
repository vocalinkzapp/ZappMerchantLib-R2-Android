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
package com.zapp.library.merchant.ui.view;

import com.zapp.library.merchant.R;
import com.zapp.library.merchant.util.PBBALibraryUtils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * The Pay by Bank app button to include in the Merchant App. This is a plain compound view without any custom logic and can be used as a simple button.
 * <p>
 * To display it in a layout, insert the following snippet:<br>
 *
 * {@code
 * <com.zapp.library.merchant.ui.view.PBBAButton
 * android:id="@+id/pbba_button"
 * android:layout_width="@dimen/pbba_button_width"
 * android:layout_height="@dimen/pbba_button_height" />
 * }
 * <p>
 * The button theme can be configured as per the Pay by Bank app Developer Guide documentation.
 *
 * @author msagi
 * @since 1.0.0
 */
public class PBBAButton extends RelativeLayout implements View.OnClickListener {

    /**
     * Bundle key for super instance state field.
     */
    private static final String KEY_SUPER_INSTANCE_STATE = "key.super.instanceState";

    /**
     * Bundle key for 'is enabled' button state field.
     */
    private static final String KEY_IS_ENABLED = "key.isEnabled";

    /**
     * Timeout for auto re-enabling button.
     */
    private static final long AUTO_RE_ENABLE_TIMEOUT = 10000L;

    /**
     * The progress animation for the button.
     */
    private ImageView mAnimatedIcon;

    /**
     * The button view.
     */
    private final View mButtonContainer;

    /**
     * The buttons click listener.
     */
    private OnClickListener mClickListener;

    /**
     * The Pay by Bank app icon on the button.
     */
    private Drawable mIcon;

    /**
     * The Pay by Bank app icon animation on the button.
     */
    private Drawable mIconAnimation;

    /**
     * Worker for button auto re-enable feature.
     */
    private final Runnable mAutoReEnableWorker = new Runnable() {
        @Override
        public void run() {
            setEnabled(true);
        }
    };

    /**
     * Creates a new Pay by Bank app button.
     *
     * @param context The view context.
     */
    public PBBAButton(final Context context) {
        this(context, null);
    }

    /**
     * Creates a new Pay by Bank app button.
     *
     * @param context The view context.
     * @param attrs   The styled attributes.
     */
    @SuppressWarnings("ThisEscapedInObjectConstruction")
    public PBBAButton(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        View view;

        int pbbaTheme = PBBALibraryUtils.getPbbaTheme(context);

        if (pbbaTheme == PBBALibraryUtils.PBBA_THEME_STANDARD) {
            view = inflate(getContext(), R.layout.pbba_button_pay_by_bank_app, this);
            mButtonContainer = view.findViewById(R.id.pbba_button_container);
            mAnimatedIcon = (ImageView) view.findViewById(R.id.progress);
            mIconAnimation = getDrawable(R.drawable.pbba_animation);
            mIcon = getDrawable(R.drawable.pbba_symbol_icon);
            mAnimatedIcon.setImageDrawable(mIcon);
        } else if (pbbaTheme == PBBALibraryUtils.PBBA_THEME_PINGIT_LIGHT) {
            view = inflate(getContext(), R.layout.pbba_button_cobranded, this);
            mButtonContainer = view.findViewById(R.id.pbba_button_container);
            mButtonContainer.setBackgroundResource(R.drawable.pingit_light_button_selector);
            final ImageView brandImageView = (ImageView) view.findViewById(R.id.pbba_brand_image);
            brandImageView.setImageDrawable(getDrawable(R.drawable.pingit_light_button_image));
        } else if (pbbaTheme == PBBALibraryUtils.PBBA_THEME_PINGIT_DARK) {
            view = inflate(getContext(), R.layout.pbba_button_cobranded, this);
            mButtonContainer = view.findViewById(R.id.pbba_button_container);
            mButtonContainer.setBackgroundResource(R.drawable.pingit_dark_button_selector);
            final ImageView brandImageView = (ImageView) view.findViewById(R.id.pbba_brand_image);
            brandImageView.setImageDrawable(getDrawable(R.drawable.pingit_dark_button_image));
        } else {
            throw new IllegalStateException("pbbaTheme value not supported");
        }

        mButtonContainer.setOnClickListener(this);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle state = new Bundle();
        state.putParcelable(KEY_SUPER_INSTANCE_STATE, super.onSaveInstanceState());
        state.putBoolean(KEY_IS_ENABLED, isEnabled());
        return state;
    }

    @Override
    protected void onRestoreInstanceState(final Parcelable state) {
        final Bundle savedInstanceState = (Bundle) state;
        final Parcelable superInstanceState = savedInstanceState.getParcelable(KEY_SUPER_INSTANCE_STATE);
        final Boolean isEnabled = savedInstanceState.getBoolean(KEY_IS_ENABLED, Boolean.TRUE);
        if (!isEnabled) {
            setEnabled(false);
        }
        super.onRestoreInstanceState(superInstanceState);
    }

    @Override
    public void onClick(final View v) {
        setEnabled(false);
        if (mClickListener != null) {
            mClickListener.onClick(this);
        }
    }

    @Override
    public void setEnabled(final boolean enabled) {
        removeCallbacks(mAutoReEnableWorker);
        super.setEnabled(enabled);
        if (enabled) {
            stopAnimation();
        } else {
            startAnimation();
            postDelayed(mAutoReEnableWorker, AUTO_RE_ENABLE_TIMEOUT);
        }
        mButtonContainer.setEnabled(enabled);
    }

    @Override
    public void setOnClickListener(final OnClickListener listener) {
        super.setOnClickListener(listener);
        this.mClickListener = listener;
    }

    /**
     * Start the Pay by Bank app button icon animation.
     */
    private void startAnimation() {
        if (mAnimatedIcon != null) {
            mAnimatedIcon.setImageDrawable(mIconAnimation);
            // Start the animation (looped playback by default).
            ((Animatable) mIconAnimation).start();
        }
    }

    /**
     * Stop the Pay by Bank app button icon animation.
     */
    private void stopAnimation() {
        if (mAnimatedIcon != null) {
            ((Animatable) mIconAnimation).stop();
            mAnimatedIcon.setImageDrawable(mIcon);
        }
    }

    /**
     * Get drawable from resource id.
     *
     * @param drawableResId The resource id of the drawable.
     * @return The drawable instance.
     */
    @SuppressWarnings("deprecation")
    private Drawable getDrawable(@DrawableRes final int drawableResId) {
        final Resources res = getResources();

        final Drawable drawable;
        //noinspection IfMayBeConditional
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = res.getDrawable(drawableResId, null);
        } else {
            drawable = res.getDrawable(drawableResId);
        }
        return drawable;
    }
}
