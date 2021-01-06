package com.tenx.fraudamlmanager.infrastructure.transactionmanager;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteSource;
import com.tenx.fraudamlmanager.infrastructure.transactionmonitoring.exceptions.TransactionManagerException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.http.HttpStatus;

@Slf4j
public class TransactionManagerErrorDecoder implements ErrorDecoder {

  private static final String FAILED_TO_READ_RESPONSE = "Failed to read response";
  private static final String SOMETHING_WRONG_WITH_TM_RETRY_CANCELLED = "Something wrong with TM, retry cancelled";
  private static final String TRANSACTION_MANAGER_DECODER_IMPOSSIBLE_TO_PARSE_THE_JSON_ERROR = "Transaction Manager decoder - Impossible to parse the JSON error";
  private static final String THE_CALL_TO_TM_WILL_BE_RETRIED_BECAUSE_THE_CODE_RETURNED_IS_DIFFERENT_FROM_2320 = "The call to TM will be retried because the code returned is different from 2320";
  private static final String SOMETHING_WRONG_WITH_TM_RETRYING = "Something wrong with TM, retrying...";

  @Override
  public Exception decode(String methodKey, Response response) {
    Exception exception =
        new TransactionManagerException(
            HttpStatus.INTERNAL_SERVER_ERROR.value(), response.reason());

    if (response.status() == HttpStatus.BAD_REQUEST.value()) {
      exception = handleBadRequest(response);
    } else if (response.status() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
      exception = handleInternalServerError(response);
    }

    return exception;
  }

  private TransactionManagerException handleBadRequest(Response response) {
    try {
      String errorResponse = convertResponseToString(response);
      return new TransactionManagerException(
          HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse);

    } catch (IOException ioexception) {
      return new TransactionManagerException(
          HttpStatus.INTERNAL_SERVER_ERROR.value(), FAILED_TO_READ_RESPONSE, ioexception);
    }
  }

  private Exception handleInternalServerError(Response response) {
    Exception exception;

    try {
      throwRetryableExceptionIfNo2320ResponseFound(response);
      log.info(SOMETHING_WRONG_WITH_TM_RETRY_CANCELLED);
      exception = new TransactionManagerException(406, response.reason());
    } catch (JSONException e) {
      log.warn(TRANSACTION_MANAGER_DECODER_IMPOSSIBLE_TO_PARSE_THE_JSON_ERROR);
      exception = new TransactionManagerException(response.status(), FAILED_TO_READ_RESPONSE, e);
    } catch (IOException ioexception) {
      exception = new TransactionManagerException(response.status(), FAILED_TO_READ_RESPONSE, ioexception);
    } catch (RetryableException e) {
      log.debug(THE_CALL_TO_TM_WILL_BE_RETRIED_BECAUSE_THE_CODE_RETURNED_IS_DIFFERENT_FROM_2320);
      log.info(SOMETHING_WRONG_WITH_TM_RETRYING);
      exception = e;
    }

    return exception;
  }

  /**
   * To retry the request if the error code received from Transaction Manager is not 2320
   */
  private void throwRetryableExceptionIfNo2320ResponseFound(Response response)
      throws IOException {
    String errorResponse = convertResponseToString(response);
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    ErrorResponse er = mapper.readValue(errorResponse, ErrorResponse.class);
    er.getErrors().stream()
        .filter(Error::is2320Response)
        .findFirst()
        .orElseThrow(
            () -> new RetryableException(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse,
                response.request().httpMethod(), null, null,
                response.request()));
  }

  private String convertResponseToString(Response response) throws IOException {
    ByteSource byteSource =
        new ByteSource() {
          @Override
          public InputStream openStream() throws IOException {
            return response.body().asInputStream();
          }
        };
    return byteSource.asCharSource(StandardCharsets.UTF_8).read();
  }
}
