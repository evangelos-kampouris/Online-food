package Exceptions;

import java.io.Serial;
import java.io.Serializable;

public class NoValidStockInput extends Exception implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public NoValidStockInput(String message) { super(message); }
}
