package ua.in.quireg.anothermovieapp.common;


import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class GeneralUtils {
    private static Toast toast_short = null;

    public static void showToastMessage(Context context, String msg) {
        if(toast_short != null){
            toast_short.cancel();
        }
        toast_short = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        toast_short.show();
    }

    public static float dipToPixels(Context context, float dipValue){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,  dipValue, metrics);
    }
}
