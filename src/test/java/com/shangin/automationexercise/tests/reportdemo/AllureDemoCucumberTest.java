package com.shangin.automationexercise.tests.reportdemo;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.Test;

@CucumberOptions(
        features = "examples/allure/failure.feature",
        glue = {
            "com.shangin.automationexercise.tests.support.cucumberprobe",
            "com.shangin.automationexercise.cucumber.hooks"
        },
        plugin = "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm")
@Test(groups = "cucumber")
public class AllureDemoCucumberTest extends AbstractTestNGCucumberTests {}
