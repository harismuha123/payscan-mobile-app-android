package ba.edu.ibu.stu.chern0.payscan;

import android.widget.EditText;

public class CustomValidator {

    public static boolean validatePassword(EditText password) {
        String passwordText = password.getText().toString();
        if (passwordText.length() < 8) {
          //  password.setError("The password should contain at least 8 characters.",

            return false;
        }
        return true;
    }
}
