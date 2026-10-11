//库存不足
package main.exception;

public class StockNotEnoughException extends Exception{

    public StockNotEnoughException(String message){
        super(message);
    }

}
