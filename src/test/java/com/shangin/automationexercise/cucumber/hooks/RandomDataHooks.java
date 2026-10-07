package com.shangin.automationexercise.cucumber.hooks;

import com.shangin.automationexercise.support.TestRandom;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class RandomDataHooks {
    @Before(order = Integer.MIN_VALUE)
    public void begin(Scenario scenario) {
        TestRandom.begin(
                message -> {
                    System.out.println(message);
                    scenario.attach(message, "text/plain", "Test selection");
                });
    }

    @After(order = Integer.MIN_VALUE)
    public void clear() {
        TestRandom.clear();
    }
}
