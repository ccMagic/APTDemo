package com.github.ccmagic.apt.annotationcompiler;

import javax.lang.model.element.Element;

public class ProcessingException extends Exception {
    public ProcessingException(Element element, String message) {
        super(message);
    }

    public ProcessingException(Element element, String message, Throwable cause) {
        super(message, cause);
    }

    public ProcessingException(Throwable cause) {
        super(cause);
    }

    protected ProcessingException(Element element, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
