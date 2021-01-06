package com.tenx.fraudamlmanager.infrastructure.transactionmonitoring;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import com.tenx.fraudamlmanager.payments.client.exceptions.TransactionMonitoringException;
import feign.Response;
import feign.codec.ErrorDecoder;
import java.io.IOException;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class TransactionMonitoringErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == HttpStatus.BAD_REQUEST.value()) {
            try {
                String errorResponse = convertResponseToString(response);
                return new TransactionMonitoringException(response.status(), errorResponse);

            } catch (IOException ioexception) {
                return ioexception;
            }

        } else {
            return new TransactionMonitoringException(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.reason());
        }
    }

    private String convertResponseToString(Response response) throws IOException {
        ByteSource byteSource = new ByteSource() {
            @Override
            public InputStream openStream() throws IOException {
                return response.body().asInputStream();
            }
        };
        return byteSource.asCharSource(Charsets.UTF_8).read();
    }
}