package com.github.ccmagic.apt.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.lang.model.type.TypeMirror;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface SimpleFactory {
    String name();

    String classType();
}

