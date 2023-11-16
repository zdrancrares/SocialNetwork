package com.example.socialnetworkgui.domain.validators;

public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}