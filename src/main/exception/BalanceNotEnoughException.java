//余额不足
package main.exception;

public class BalanceNotEnoughException extends Exception{

    public BalanceNotEnoughException(String message){
        super(message);
    }

}
