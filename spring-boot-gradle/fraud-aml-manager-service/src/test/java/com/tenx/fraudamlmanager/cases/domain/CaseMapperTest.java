package com.tenx.fraudamlmanager.cases.domain;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.tenx.fraudamlmanager.cases.domain.external.ExternalCase;
import com.tenx.fraudamlmanager.cases.v2.domain.CaseV2;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class CaseMapperTest {

  private static Stream<Arguments> finCrimeCheckStatusesForProcessCase() {
    return Stream.of(
        Arguments.of(FinCrimeCheckCaseStatus.REJECTED),
        Arguments.of(FinCrimeCheckCaseStatus.BLOCKED),
        Arguments.of(FinCrimeCheckCaseStatus.PASSED),
        Arguments.of(FinCrimeCheckCaseStatus.CANCELLED),
        Arguments.of(FinCrimeCheckCaseStatus.REFERRED)
    );
  }

  @ParameterizedTest
  @MethodSource("finCrimeCheckStatusesForProcessCase")
  public void testExternalCaseUpdate(FinCrimeCheckCaseStatus status) {
    FinCrimeCheckCase finCrimeCheckCase = new FinCrimeCheckCase("transactionId", status);
    finCrimeCheckCase.getExternalCases()
        .add(new CaseDetails("sourceSystem", "FRAUD_EXCEPTION", "sourceCaseId", "comments", true));

    CaseV2 caseV2 = new CaseV2();
    caseV2.getAttributes().add(new CaseAttribute("test", "test"));

    if (!(FinCrimeCheckCaseStatus.PASSED.equals(status) ||
        FinCrimeCheckCaseStatus.REJECTED.equals(status) ||
        FinCrimeCheckCaseStatus.CANCELLED.equals(status) ||
        FinCrimeCheckCaseStatus.BLOCKED.equals(status))) {

      assertThrows(IllegalArgumentException.class,
          () -> CaseMapper.MAPPER.toExternalCaseForUpdate(caseV2, finCrimeCheckCase, "caseId"));

    } else {

      ExternalCase externalCaseUpdate =
          CaseMapper.MAPPER.toExternalCaseForUpdate(caseV2, finCrimeCheckCase, "caseId");
      assertThat(externalCaseUpdate.getBpmSystemCaseId(), equalTo("caseId"));
      assertThat(externalCaseUpdate.getStatus(), equalTo(status.name()));
      if (FinCrimeCheckCaseStatus.PASSED.equals(status)) {
        assertThat(externalCaseUpdate.getOutcome(), equalTo("PAYMENT SENT TO BENEFICIARY"));
      } else if (FinCrimeCheckCaseStatus.REJECTED.equals(status)
          || FinCrimeCheckCaseStatus.CANCELLED.equals(status)) {
        assertThat(externalCaseUpdate.getOutcome(), equalTo("PAYMENT SENT TO ORIGINATOR"));
      } else if (FinCrimeCheckCaseStatus.BLOCKED.equals(status)) {
        assertThat(externalCaseUpdate.getOutcome(), equalTo("PAYMENT HELD INDEFINITELY"));
      }
    }

  }

}
