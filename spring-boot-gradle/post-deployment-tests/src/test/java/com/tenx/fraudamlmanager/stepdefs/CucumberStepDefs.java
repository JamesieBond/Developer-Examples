package com.tenx.fraudamlmanager.stepdefs;

import io.cucumber.java.en.Given;

public class CucumberStepDefs {

  @Given("I say {string}")
  public void i_say(String string) {
    System.out.println("\n =============     Hello ! This is cucumber " + string + " test    ===============");
  }

}
