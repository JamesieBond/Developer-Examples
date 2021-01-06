package com.tenx.fraudamlmanager.subscriptions.api;

public class SubscriptionException extends Exception {

    public SubscriptionException(String s) {
        super(s);
    }

    public SubscriptionException(String s, Throwable t) {
        super(s, t);
    }
}
