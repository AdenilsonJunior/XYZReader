package com.adenilson.xyzreader.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * @author: Adenilson N. da Silva Junior
 * On date: 21/08/2018
 */

public class DimensUtil {

    public static int dpToPx(Context context, int dp){
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }
}
