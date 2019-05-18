package com.avogine.junkyard.annotations;

import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

// TODO Figure out if you can make annotations require a method to be called
@Retention(SOURCE)
@Target(ElementType.TYPE)
@Inherited
public @interface CleanMeUp {

}
