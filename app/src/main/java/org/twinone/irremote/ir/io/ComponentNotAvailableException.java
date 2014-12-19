package org.twinone.irremote.ir.io;

class ComponentNotAvailableException extends RuntimeException {

    private static final long serialVersionUID = 7203846418677846118L;

    public ComponentNotAvailableException() {
        super();
    }

    public ComponentNotAvailableException(String detailMessage,
                                          Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ComponentNotAvailableException(String detailMessage) {
        super(detailMessage);
    }

    public ComponentNotAvailableException(Throwable throwable) {
        super(throwable);
    }

}
