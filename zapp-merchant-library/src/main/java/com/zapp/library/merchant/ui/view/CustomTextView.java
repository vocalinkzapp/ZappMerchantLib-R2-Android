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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.zapp.library.merchant.R;

/**
 * Custom text view for the Pay by Bank app popups.
 *
 * @author gdragan
 * @since 1.0.0
 */
public class CustomTextView extends TextView {

    /**
     * Create new instance.
     *
     * @param context The context to use.
     */
    public CustomTextView(final Context context) {
        super(context);
        init(context, null);
    }

    /**
     * Create new instance.
     *
     * @param context The context to use.
     * @param attrs   The attribute set to use.
     */
    public CustomTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * Create new instance.
     *
     * @param context  The context to use.
     * @param attrs    The attribute set to use.
     * @param defStyle The default type id to use.
     */
    public CustomTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    /**
     * Initialize custom view.
     *
     * @param context The context to use.
     * @param attrs   The attribute set to use.
     */
    @TargetApi(24)
    private void init(final Context context, final AttributeSet attrs) {
        final TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomTextView, 0, 0);

        try {
            final String fontName = a.getString(R.styleable.CustomTextView_fontName);
            final Typeface customFont = Typeface.createFromAsset(context.getAssets(), fontName);
            setTypeface(customFont);

            final String htmlText = a.getString(R.styleable.CustomTextView_htmlText);
            setHtmlText(htmlText);
        } finally {
            a.recycle();
        }
    }

    /**
     * Sets the string value of the TextView and applies the html format provided in the string.
     *
     * @param htmlText Text which contains html tags.
     */
    public void setHtmlText(@Nullable final String htmlText) {
        if (!TextUtils.isEmpty(htmlText)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                setText(Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY));
            } else {
                //noinspection deprecation
                setText(Html.fromHtml(htmlText));
            }
        }
    }
}
