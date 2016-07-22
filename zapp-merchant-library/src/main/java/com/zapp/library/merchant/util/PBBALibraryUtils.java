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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Internal utility class for the Zapp Merchant Library. Do not use this class as it is not part of the API and its methods can change without notice.
 *
 * @author msagi
 * @since 1.0.0
 */
public final class PBBALibraryUtils {

    /**
     * File name of the custom configuration file.
     */
    private static final String CUSTOM_CONFIGURATION_FILE = "pbbaCustomConfig.properties";

    /**
     * Common tag for logging.
     */
    public static final String PBBA_LOG_TAG = "PBBA";

    /**
     * The Pay by Bank app theme custom configuration key.
     */
    public static final String KEY_PBBA_THEME = "pbbaTheme";

    /**
     * Standard PBBA theme value.
     */
    public static final int PBBA_THEME_STANDARD = 1;

    /**
     * Co-branded Pingit / PBBA light theme value.
     */
    public static final int PBBA_THEME_PINGIT_LIGHT = 2;

    /**
     * Co-branded Pingit / PBBA dark theme value.
     */
    public static final int PBBA_THEME_PINGIT_DARK = 3;

    /**
     * The default value for Pay by Bank app theme.
     */
    public static final int PBBA_THEME_DEFAULT_VALUE = PBBA_THEME_STANDARD;

    /**
     * Constant for banking app button clicked shared preferences key.
     */
    private static final String BANKING_APP_BTN_CLICKED = "openBankingAppButtonClicked";

    /**
     * The custom configuration.
     */
    @SuppressWarnings("RedundantFieldInitialization")
    private static Properties sCustomConfiguration = null;

    /**
     * Hidden constructor for utility class.
     */
    private PBBALibraryUtils() {
    }

    /**
     * Set custom configuration.
     *
     * @param properties The custom configuration to use.
     */
    @SuppressWarnings({"MethodMayBeSynchronized", "ElementOnlyUsedFromTestCode"})
    public static void setCustomConfiguration(@NonNull final Properties properties) {
        synchronized (PBBALibraryUtils.class) {
            //noinspection ConstantConditions
            if (properties != null) {
                sCustomConfiguration = properties;
            }
        }
    }

    /**
     * Get the Pay by Bank app theme setting.
     *
     * @param context The context to use.
     * @return The theme value.
     */
    public static int getPbbaTheme(@NonNull final Context context) {
        String pbbaThemeValue;
        synchronized (PBBALibraryUtils.class) {
            if (sCustomConfiguration == null) {
                sCustomConfiguration = new Properties();
                try {
                    @SuppressWarnings("resource") final AssetManager assetManager = context.getAssets();
                    final InputStream inputStream = assetManager.open(CUSTOM_CONFIGURATION_FILE);
                    sCustomConfiguration.load(inputStream);
                    inputStream.close();
                } catch (IOException ignored) {
                    //nothing to do here
                }
            }
            pbbaThemeValue = sCustomConfiguration.getProperty(KEY_PBBA_THEME);
        }

        try {
            int theme = Integer.parseInt(pbbaThemeValue);
            //noinspection IfMayBeConditional
            if (theme == PBBA_THEME_STANDARD || theme == PBBA_THEME_PINGIT_LIGHT || theme == PBBA_THEME_PINGIT_DARK) {
                return theme;
            } else {
                return PBBA_THEME_DEFAULT_VALUE;
            }
        } catch (NumberFormatException ignored) {
            return PBBA_THEME_DEFAULT_VALUE;
        }
    }

    /**
     * Checks whether the user has opened the bank app by clicking the button from the popup dialog.
     * If so, second time the bank app should be opened automatically.
     *
     * @param context The context to use.
     * @return true if it was clicked, false otherwise.
     */
    public static boolean isOpenBankingAppButtonClicked(@NonNull final Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(BANKING_APP_BTN_CLICKED, false);
    }

    /**
     * Set the open banking app button clicked flag in shared preferences.
     *
     * @param context The context to use.
     * @param value The new flag value.
     */
    public static void setOpenBankingAppButtonClicked(@NonNull final Context context, final boolean value) {
        final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(BANKING_APP_BTN_CLICKED, value);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            editor.apply();
        } else {
            editor.commit();
        }
    }
}
