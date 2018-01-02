package com.example.currencymonitor.interfaces;

/**
 * Created by l1maginaire on 1/3/18.
 */


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

@Scope
@Retention(RetentionPolicy.CLASS)
public @interface ApplicationScope {}