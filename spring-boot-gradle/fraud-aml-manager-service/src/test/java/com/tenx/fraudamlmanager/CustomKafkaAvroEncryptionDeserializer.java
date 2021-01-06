package com.tenx.fraudamlmanager;

import com.tenxbanking.individual.event.IndividualEventV1;
import com.tenxbanking.party.event.CustomerEventV3;
import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.avro.Schema;

public class CustomKafkaAvroEncryptionDeserializer extends KafkaAvroDeserializer {

  private static SchemaRegistryClient getMockClient(final Schema schema$) {
    return new MockSchemaRegistryClient() {
      @Override
      public synchronized Schema getById(int id) {
        return schema$;
      }
    };
  }

  @Override
  public Object deserialize(String topic, byte[] bytes) {

    switch (topic) {
      case "individual-party-v2-enc":
        this.schemaRegistry = getMockClient(IndividualEventV1.SCHEMA$);
        break;
      case "party-event-v3-enc":
        this.schemaRegistry = getMockClient(CustomerEventV3.SCHEMA$);
        break;
      default:
        throw new IllegalArgumentException(topic + " not found");
    }

    return super.deserialize(topic, bytes);
  }
}
