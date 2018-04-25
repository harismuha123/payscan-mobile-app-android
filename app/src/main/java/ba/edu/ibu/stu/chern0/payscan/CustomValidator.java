package ba.edu.ibu.stu.chern0.payscan;

import android.support.design.widget.TextInputLayout;
import android.text.Html;
import android.widget.EditText;

public class CustomValidator {

    public static boolean validatePassword(TextInputLayout passwordLayout) {
        String passwordText = passwordLayout.getEditText().getText().toString();
        if (passwordText.length() < 8 ) {
            passwordLayout.setError(Html.fromHtml(
                    "<font color='white'>The password should contain at least 8 characters.</font>"));
            //passwordLayout.getEditText().getBackground().setColorFilter();
            return false;
        } else {
            passwordLayout.setError(null);
        }
        return true;
    }


}
