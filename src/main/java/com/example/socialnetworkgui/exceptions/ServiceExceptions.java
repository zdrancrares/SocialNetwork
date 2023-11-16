package com.example.socialnetworkgui.exceptions;

/**
 * Class for service exceptions
 */

public class ServiceExceptions extends Exception{
    private String message;
    public ServiceExceptions(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }
}
