package com.tenx.fraudamlmanager.domain;


public enum PaymentMetricsType {

  DIRECT_DEBIT("directdebit"),
  DIRECT_CREDIT("directcredit"),
  ON_US("onus"),
  DOMESTIC_IN("domesticin"),
  DOMESTIC_OUT("domesticout");

  private String type;

  PaymentMetricsType(String type){
    this.type = type;
  }

  @Override
  public String toString() {
    return type;
  }
}
