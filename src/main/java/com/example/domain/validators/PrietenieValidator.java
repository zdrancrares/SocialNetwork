package com.example.domain.validators;

import com.example.domain.Prietenie;

public class PrietenieValidator implements Validator<Prietenie>{
    @Override
    public void validate(Prietenie entity) throws ValidationException {
        String errors = "";
        if (entity.getUser1() == null || entity.getUser2()==null){
            errors += "Cel putin unul dintre utilizatori nu exista.\n";
        }
        if (entity.getUser1() == entity.getUser2()){
            errors += "Cei doi prieteni nu pot sa aibe acelasi ID.\n";
        }
        if (!errors.isEmpty()){
            throw new ValidationException(errors);
        }
    }
}
