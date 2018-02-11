package com.example.currencymonitor.di.components;

/**
 * Created by l1maginaire on 1/3/18.
 */

import com.example.currencymonitor.di.modules.CurrencyModule;
import com.example.currencymonitor.interfaces.ApplicationScope;
import dagger.Component;
import com.example.currencymonitor.interfaces.FixerAPI;

@ApplicationScope
@Component(modules = {CurrencyModule.class})
public interface CurrencyComponent {

    FixerAPI getCurrencyService();
}
