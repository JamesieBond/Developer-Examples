package com.tenx.fraudamlmanager.customer.paymentinfo;


import com.tenxbanking.events.lib.CustomerCreditTransferInitiationCompletedEvent;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.commons.io.IOUtils;

public final class CustomerCreditTransferHelper {

  public static CustomerCreditTransferInitiationCompletedEvent readEventFromFile() throws IOException {

    InputStream jsonInput = CustomerCreditTransferHelper.class
        .getResourceAsStream("/payloads/payments-customer-credit-transfer-initiation-completed-event.json");
    DataInputStream din = new DataInputStream(jsonInput);

    FileInputStream schemaFis = new FileInputStream(
        "src/main/avro/CustomerCreditTransferInitiationCompletedEvent.avsc");
    String schemaStr = IOUtils.toString(schemaFis, "UTF-8");
    Schema schema = Schema.parse(schemaStr);

    Decoder decoder = DecoderFactory.get().jsonDecoder(schema, din);

    SpecificDatumReader<CustomerCreditTransferInitiationCompletedEvent> reader = new SpecificDatumReader<>(
        CustomerCreditTransferInitiationCompletedEvent.class);
    return reader.read(null, decoder);

  }
}
