package com.tenx.fraudamlmanager;

import com.tenx.fraud.payments.fps.FPSFraudCheckResponse;
import com.tenx.fraud.payments.fpsin.FPSInboundPaymentFraudCheck;
import com.tenx.fraud.payments.fpsout.FPSOutboundPaymentFraudCheck;
import com.tenx.fraud.payments.onus.FPSOutboundReturnPaymentFraudCheck;
import com.tenx.fraud.payments.onus.ONUSPaymentFraudCheck;
import com.tenx.payment.configuration.directdebit.event.v1.DirectDebitEvent;
import com.tenx.security.forgerockfacade.resource.AccountResetNotification;
import com.tenx.security.forgerockfacade.resource.CustomerRegistration;
import com.tenx.security.forgerockfacade.resource.Login;
import com.tenx.security.forgerockfacade.resource.StepUp;
import com.tenxbanking.events.lib.CustomerCreditTransferInitiationCompletedEvent;
import com.tenxbanking.individual.event.IndividualEventV1;
import com.tenxbanking.iso.lib.IsoCreditTransferFraudCheckRequestV01;
import com.tenxbanking.party.event.CustomerEventV3;
import com.tenxbanking.party.event.business.BusinessEventV2;
import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.avro.Schema;

public class CustomKafkaAvroDeserializer extends KafkaAvroDeserializer {

  @Override
  public Object deserialize(String topic, byte[] bytes) {

    switch (topic) {
      case "individual-party-v2":
        this.schemaRegistry = getMockClient(IndividualEventV1.SCHEMA$);
        break;
      case "kafka-identity-customer-registration-v1":
        this.schemaRegistry = getMockClient(CustomerRegistration.SCHEMA$);
        break;
      case "payments-notifications-topic":
        this.schemaRegistry = getMockClient(DirectDebitEvent.SCHEMA$);
        break;
      case "identity-account-reset-notification-v1":
        this.schemaRegistry = getMockClient(AccountResetNotification.SCHEMA$);
        break;
      case "identity-step-up-v1":
        this.schemaRegistry = getMockClient(StepUp.SCHEMA$);
        break;
      case "identity-login-v1":
        this.schemaRegistry = getMockClient(Login.SCHEMA$);
        break;
      case "fps-fraud-check-request-v2":
        this.schemaRegistry = getDomesticSchemaRegistry(KafkaTestBase.eventId);
        break;
      case "fps-fraud-check-response-v2":
        this.schemaRegistry = getMockClient(FPSFraudCheckResponse.SCHEMA$);
        break;
      case "party-event-business-v2":
        this.schemaRegistry = getMockClient(BusinessEventV2.SCHEMA$);
        break;
      case "party-event-v3":
        this.schemaRegistry = getMockClient(CustomerEventV3.SCHEMA$);
        break;
      case "payments-core-credit-transfer-fraud-check-request-v1":
        this.schemaRegistry = getMockClient(IsoCreditTransferFraudCheckRequestV01.SCHEMA$);
        break;
      case "payments-cct-initiation-completed-event-v1":
        this.schemaRegistry = getMockClient(CustomerCreditTransferInitiationCompletedEvent.SCHEMA$);
        break;
      default:
        throw new IllegalArgumentException(topic + " not found");
    }

      return super.deserialize(topic, bytes);
  }

    private static SchemaRegistryClient getMockClient(final Schema schema$) {
        return new MockSchemaRegistryClient() {
            @Override
            public synchronized Schema getById(int id) {
                return schema$;
            }
        };
    }

    private SchemaRegistryClient getDomesticSchemaRegistry(String eventId) {

        switch (eventId) {
            case "domesticInEventV2":
                return getMockClient(FPSInboundPaymentFraudCheck.SCHEMA$);
            case "domesticOutEventV2":
                return getMockClient(FPSOutboundPaymentFraudCheck.SCHEMA$);
            case "domesticOutReturnEventV2":
                return getMockClient(FPSOutboundReturnPaymentFraudCheck.SCHEMA$);
            case "onUsEventV2":
                return getMockClient(ONUSPaymentFraudCheck.SCHEMA$);
            default:
                throw new IllegalArgumentException(eventId + " not found");
        }
    }
}
