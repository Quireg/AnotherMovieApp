package ua.in.quireg.anothermovieapp.common;


import android.content.Context;
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


}
