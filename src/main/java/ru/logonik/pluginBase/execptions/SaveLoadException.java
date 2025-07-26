package ru.logonik.pluginBase.execptions;

public class SaveLoadException extends Exception {

    public SaveLoadException() {
    }

    public SaveLoadException(String message) {
        super(message);
    }

    public SaveLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public SaveLoadException(Throwable cause) {
        super(cause);
    }

    public SaveLoadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
