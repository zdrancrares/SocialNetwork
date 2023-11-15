package com.example.exceptions;

/**
 * Class for repository exceptions
 */

public class RepositoryExceptions extends Exception{
    private String message;
    public RepositoryExceptions(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }
}
