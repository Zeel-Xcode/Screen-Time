package Model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

import com.screentime.R;

/**
 * Created by kmsoft on 07/03/18.
 */

public class AppStaticData {
    //        public static String mailUrl = "http://kmsof.com/pauseappmail.php";
    public static String mailUrl = "http://squareroot.cloud/pauseappmail.php";
    public static String answerOne = "";
    public static String answerTwo = "";
    public static String answerThree = "";
    public static String answerFour = "";
    public static float rating;
    private static int pagerPosition;
    public static String ratingString = "rate";
    public static String change = "change";
    public static String like = "like";
    public static String use = "use";
    public static String hear = "hear";


    public static int viewPagerPosition(Context mContext) {
        if (TextUtils.isEmpty(answerOne)) {
            return pagerPosition = 0;
        } else if (TextUtils.isEmpty(answerTwo)) {
            return pagerPosition = 1;
        } else if (TextUtils.isEmpty(answerThree)) {
            return pagerPosition = 2;
        } else if (TextUtils.isEmpty(answerFour)) {
            return pagerPosition = 3;
        } else if (rating == 0) {
//            ratingAlert(mContext);
            return pagerPosition = 4;
        }
        return 5;
    }

    public static void ratingAlert(Context mContext) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getResources().getString(R.string.rating));
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
