package com.example.socialnetworkgui.domain.validators;

import com.example.socialnetworkgui.domain.Utilizator;

public class UtilizatorValidator implements Validator<Utilizator> {
    @Override
    public void validate(Utilizator entity) throws ValidationException {
        String errors = "";
        errors += validateFirstName(entity.getFirstName());
        errors += validateLastName(entity.getLastName());
        errors += validateEmail(entity.getEmail());
        errors += validatePassword(entity.getPassword());
        if (!errors.isEmpty()){
            throw new ValidationException(errors);
        }
    }

    private static String validateFirstName(String firstName) throws ValidationException{
        String errors = "";
        if (firstName.isEmpty()){
            errors += "Lungimea prenumelui nu poate sa fie nula.\n";
        }
        if (firstName.length() > 50){
            errors += "Lungimea prenumelui nu poate sa depaseasca 50 de caractere.\n";
        }
        return errors;
    }

    private static String validateLastName(String lastName) throws ValidationException{
        String errors = "";
        if (lastName.isEmpty()){
            errors += "Lungimea numelui nu poate sa fie nula.\n";
        }
        if (lastName.length() > 50){
            errors += "Lungimea numelui nu poate sa depaseasca 50 de caractere.\n";
        }
        return errors;
    }

    private static String validateEmail(String email) throws ValidationException{
        String errors = "";
        if (email.isEmpty()){
            errors += "Lungimea email-ului nu poate sa fie nula.\n";
        }
        if (!(email.length() > 4 && email.length() < 50)){
            errors += "Lungimea email-ului trebuie sa fie cuprinsa intre 5 si 50 de caractere.\n";
        }
        return errors;
    }

    private static String validatePassword(String password) throws ValidationException{
        String errors = "";
        if (password.isEmpty()){
            errors += "Lungimea parolei nu poate sa fie nula.\n";
        }
        if (!(password.length() > 7 && password.length() < 100)){
            errors += "Lungimea parolei trebuie sa fie cuprinsa intre 8 si 100 de caractere.\n";
        }
        return errors;
    }
}

