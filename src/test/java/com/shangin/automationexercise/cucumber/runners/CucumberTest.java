package com.shangin.automationexercise.cucumber.runners;

import org.testng.annotations.Test;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = "com.shangin.automationexercise.cucumber",
        plugin = "pretty"
        )

@Test(groups = "cucumber")
public class CucumberTest extends AbstractTestNGCucumberTests {

}
