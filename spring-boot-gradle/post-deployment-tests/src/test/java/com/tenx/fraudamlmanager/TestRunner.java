package com.tenx.fraudamlmanager;


import io.cucumber.junit.Cucumber;
import io.cucumber.testng.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "classpath:cucumber/FirstTest.feature",
    glue = "com.tenx.fraudamlmanager.stepdefs",
    tags = {"@canary"},
    strict = false,
    monochrome = true,
    plugin = {},
    dryRun = false
)
public class TestRunner {

}
