package ba.edu.ibu.stu.chern0.payscan;

import android.graphics.Typeface;
import android.support.design.widget.TextInputLayout;
import android.text.Html;
import android.util.Patterns;
import android.widget.EditText;

public class CustomValidator {

    /* validates password in passwordLayout */
    public static boolean validatePassword(TextInputLayout passwordLayout) {
        String passwordText = passwordLayout.getEditText().getText().toString();
        if (passwordText.length() < 8 ) {
            passwordLayout.setError(Html.fromHtml(
                    "<font color='#ffffff'><em>The password should contain at least 8 characters.</em></font>"));
            //passwordLayout.getEditText().getBackground().setColorFilter();
            return false;
        } else {
            passwordLayout.setError(null);
        }
        return true;
    }

    /* validates email in emailLayout */
    public static boolean validateEmail(TextInputLayout emailLayout) {
        String emailText = emailLayout.getEditText().getText().toString();

        if (emailText.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            emailLayout.setError(Html.fromHtml("<font color='#ffffff'><em>Please enter a valid email.</em></font>"));
            return false;
        } else {
            emailLayout.setError(null);
        }
        return true;
    }

    /* validates retyped password by comparing it to the original password */
    public static boolean validateRetypedPassword(TextInputLayout rePasswordLayout, TextInputLayout passwordLayout) {
        String rePasswordText = rePasswordLayout.getEditText().getText().toString();
        String passwordText = passwordLayout.getEditText().getText().toString();

        if(rePasswordText.isEmpty() || !rePasswordText.equals(passwordText)) {
            rePasswordLayout.setError(Html.fromHtml("<font color='#ffffff'><em>Passwords do not match.</em></font>"));
            return false;
        } else {
            rePasswordLayout.setError(null);
        }
        return true;
    }

    /* validates Name which user chooses for their account */
    public static boolean validateName(TextInputLayout nameLayout) {
        String nameText = nameLayout.getEditText().getText().toString();

        if (nameText.isEmpty()) {
            nameLayout.setError(Html.fromHtml("<font color='#ffffff'><em>Please enter your name.</em></font>"));
            return false;
        } else if (nameText.length() < 5) {
            nameLayout.setError(Html.fromHtml("<font color='#ffffff'><em>Your name has to be at least 5 characters long.</em></font>"));
            return false;
        } else if (nameText.length() > 50) {
            nameLayout.setError(Html.fromHtml("<font color='#ffffff'><em>Your name can't be longer than 50 characters.</em></font>"));
            return false;
        } else {
            nameLayout.setError(null);
        }

        return true;
    }


    /* performs validation on all fields and returns true if all fields are filled with valid data */
    public static boolean isValid(TextInputLayout nameLayout, TextInputLayout emailLayout, TextInputLayout passwordLayout, TextInputLayout rePasswordLayout) {
        boolean valid = false;

        if(validateName(nameLayout) && validateEmail(emailLayout) && validatePassword(passwordLayout) && validateRetypedPassword(rePasswordLayout, passwordLayout)) {
            valid = true;
        }

        return valid;
    }
}
