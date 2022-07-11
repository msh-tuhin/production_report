package myapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.productionreport.bd.BuildConfig;
import com.productionreport.bd.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class OrphanUtilityMethods {

    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public static String getCurrentUserName(Context context){
        SharedPreferences sPref = context.getSharedPreferences(
                context.getString(R.string.vital_info),
                Context.MODE_PRIVATE);
        Log.i("current_user_name", sPref.getString("name", ""));
        return sPref.getString("name", "");
    }

    public static void hideKeyboard(Context context){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        try{
            imm.hideSoftInputFromWindow(((Activity)context).getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException e){
//            e.printStackTrace();
        }
    }
}
