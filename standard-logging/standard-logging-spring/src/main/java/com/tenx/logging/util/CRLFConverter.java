package com.tenx.logging.util;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.CompositeConverter;

public class CRLFConverter extends CompositeConverter<ILoggingEvent> {

    @Override
    protected String transform(ILoggingEvent event, String in) {
        String clean = in.replace('\n', '_').replace('\r', '_');

        return clean;
    }

    /**
     * Override start method because the superclass ReplacingCompositeConverter
     * requires at least two options and this class has none.
     */
    @Override
    public void start() {
        started = true;
    }

}
