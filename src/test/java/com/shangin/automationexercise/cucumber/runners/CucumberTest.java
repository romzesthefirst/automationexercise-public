package com.shangin.automationexercise.cucumber.runners;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = "com.shangin.automationexercise.cucumber",
        plugin = {
                "pretty",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
             }
        )

@Test(groups = {"cucumber", "smoke"})
public class CucumberTest extends AbstractTestNGCucumberTests {
   
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        Object[][] scenarios = super.scenarios();
        if (scenarios.length == 0) {
            throw new IllegalStateException(
                    "No Cucumber scenarios matched the requested selection. Check cucumber.filter.tags and features.");
        }
        return scenarios;
    }
}
