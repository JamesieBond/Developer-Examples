package com.tenx.fraudamlmanager.cases.update.domain;

import static com.tenx.fraudamlmanager.cases.v2.domain.CaseV2.CaseType.FRAUD_EXCEPTION;

import com.tenx.fraudamlmanager.cases.v2.domain.CaseStatus;
import com.tenx.fraudamlmanager.paymentsv2.fincrimecheckresult.domain.FinCrimeCheckResultResponseCodeV2;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCaseUpdate {

	private String outcome;

	private final List<Attribute> attributes = new ArrayList<>();

	private String status;

	private String caseType;

	private String partyKey;

	private boolean isBlocked(String outcome) {
		return FinCrimeCheckResultResponseCodeV2.BLOCKED.name().equalsIgnoreCase(outcome);
	}

	protected boolean isFraudException() {
		return FRAUD_EXCEPTION.name().equalsIgnoreCase(caseType);
	}

	protected boolean isClosedWithOutcome() {
		return CaseStatus.CLOSED.name().equalsIgnoreCase(status) && !Strings.isEmpty(outcome);
	}

	protected String extractValueForTransactionId() {

		Optional<Attribute> transactionIdOutcome = attributes.stream()
						.filter(Attribute::isAttributeNameTransactionId)
						.findFirst();

		return transactionIdOutcome
						.map(Attribute::getAttributeValue)
						.orElse("");
	}

	protected boolean isApplicableForFinCrimeCheckWithProvisionalOutcomeAsBlocked() {
		if (attributes.stream().anyMatch(Attribute::isAttributeNameTransactionId)) {
			updateOutcomeForProvisionalBlockedOutcomes();
			return isBlocked(outcome);
		}
		return false;
	}

	private void updateOutcomeForProvisionalBlockedOutcomes() {

		Optional<Attribute> provisionalOutcome = attributes.stream()
						.filter(Attribute::isAttributeNameProvisionalOutcome)
						.findFirst();

		provisionalOutcome
						.map(Attribute::getAttributeValue)
						.ifPresent(provisionalOutcomeValue -> {
											if (isBlocked(provisionalOutcomeValue)) {
												setOutcome(provisionalOutcomeValue);
											}
										}
						);
	}

}
