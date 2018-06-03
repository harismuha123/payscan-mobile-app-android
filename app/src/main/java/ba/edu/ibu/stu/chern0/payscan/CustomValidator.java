package ba.edu.ibu.stu.chern0.payscan;

import android.support.design.widget.TextInputLayout;
import android.text.Html;

public class CustomValidator {

    /* validates password in passwordLayout */
    public static boolean validatePassword(TextInputLayout passwordLayout) {
        String passwordText = passwordLayout.getEditText().getText().toString();
        if (passwordText.length() < 8 ) {
            passwordLayout.setError(Html.fromHtml(
                    "<font color='#ffffff'><em>Šifra treba sadržati barem 8 karaktera.</em></font>"));
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
            emailLayout.setError(Html.fromHtml("<font color='#ffffff'><em>Molimo unesite validnu email adresu.</em></font>"));
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
            rePasswordLayout.setError(Html.fromHtml("<font color='#ffffff'><em>Šifre se ne podudaraju.</em></font>"));
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
            nameLayout.setError(Html.fromHtml("<font color='#ffffff'><em>Molimo unesite ime.</em></font>"));
            return false;
        } else if (nameText.length() < 5) {
            nameLayout.setError(Html.fromHtml("<font color='#ffffff'><em>Ime treba biti dugo barem 5 karaktera.</em></font>"));
            return false;
        } else if (nameText.length() > 50) {
            nameLayout.setError(Html.fromHtml("<font color='#ffffff'><em>Ime ne smije biti duže od 50 karaktera.</em></font>"));
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

    public static  boolean isValid(TextInputLayout emailLayout, TextInputLayout passwordLayout) {
        return (validateEmail(emailLayout) && validatePassword(passwordLayout));
    }
}
