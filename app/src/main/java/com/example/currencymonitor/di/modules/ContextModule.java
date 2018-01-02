package com.example.currencymonitor.di.modules;

/**
 * Created by l1maginaire on 1/3/18.
 */

import android.content.Context;

import com.example.currencymonitor.interfaces.ApplicationScope;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class ContextModule {

    Context context;

    public ContextModule(Context context){
        this.context = context;
    }

    @Named("application_context")
    @ApplicationScope
    @Provides
    public Context context(){ return context.getApplicationContext(); }
}