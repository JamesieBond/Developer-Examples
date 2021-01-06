package com.tenx.fraudamlmanager.payments.service.impl;

import com.tenx.fraudamlmanager.payments.model.api.DirectCreditPayment;
import com.tenx.fraudamlmanager.payments.model.api.DirectDebitPayment;
import com.tenx.fraudamlmanager.payments.model.api.FpsInboundPayment;
import com.tenx.fraudamlmanager.payments.model.api.FpsOutboundPayment;
import com.tenx.fraudamlmanager.payments.model.api.FraudCheckResponse;
import com.tenx.fraudamlmanager.payments.model.api.OnUsPayment;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.DirectCreditBacsPayment;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.DirectDebitBacsPayment;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.DomesticInPayment;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.DomesticOutPayment;
import com.tenx.fraudamlmanager.payments.model.transactionmonitoring.FraudAMLSanctionsCheckResponse;
import com.tenx.fraudamlmanager.payments.service.mappers.DirectCreditBacsPaymentToDirectCreditPaymentMapper;
import com.tenx.fraudamlmanager.payments.service.mappers.DirectDebitBacsPaymentToDirectDebitPaymentMapper;
import com.tenx.fraudamlmanager.payments.service.mappers.DomesticInPaymentToFpsInboundPaymentMapper;
import com.tenx.fraudamlmanager.payments.service.mappers.DomesticOutPaymentToFpsOutboundMapper;
import com.tenx.fraudamlmanager.payments.service.mappers.FraudCheckResponseToFraudCheckResponseOldMapper;
import com.tenx.fraudamlmanager.payments.service.mappers.OnUsPaymentOldToOnUsPaymentMapper;
import com.tenx.fraudamlmanager.payments.service.mappers.OnUsPaymentToDomesticInPaymentMapper;
import com.tenx.fraudamlmanager.payments.service.mappers.OnUsPaymentToDomesticOutPaymentMapper;
import org.springframework.stereotype.Service;

@Service
public class PaymentMappingService {
    public com.tenx.fraudamlmanager.payments.model.transactionmonitoring.OnUsPayment mapOnUsPayment(OnUsPayment payment) {
        return OnUsPaymentOldToOnUsPaymentMapper.MAPPER.toOnUsPayment(payment);
    }

    public FraudCheckResponse mapToOldFraudCheckResponse(FraudAMLSanctionsCheckResponse fraudAMLSanctionsCheckResponse) {
        return FraudCheckResponseToFraudCheckResponseOldMapper.MAPPER.toFraudCheckResponseOld(fraudAMLSanctionsCheckResponse);
    }

    public DomesticOutPayment mapToFpsOutboundPayment(FpsOutboundPayment outboundPayment) {
        return DomesticOutPaymentToFpsOutboundMapper.MAPPER.toFpsOutboundPayment(outboundPayment);
    }

    public DomesticInPayment mapToFpsInboundPayment(FpsInboundPayment inboundPayment) {
        return DomesticInPaymentToFpsInboundPaymentMapper.MAPPER.toFpsInboundPayment(inboundPayment);
    }

    public DirectDebitBacsPayment mapToDiectDebitPayment(DirectDebitPayment directDebitPayment) {
        return DirectDebitBacsPaymentToDirectDebitPaymentMapper.MAPPER.toDirectDebitPayment(directDebitPayment);
    }

    public DirectCreditBacsPayment mapToDirectCreditPayment(DirectCreditPayment directCreditPayment) {
        return DirectCreditBacsPaymentToDirectCreditPaymentMapper.MAPPER.toDirectCreditPayment(directCreditPayment);
    }


    public DomesticOutPayment mapOnUsPaymentToDomesticOutPayment(com.tenx.fraudamlmanager.payments.model.transactionmonitoring.OnUsPayment onUsPayment){
        return OnUsPaymentToDomesticOutPaymentMapper.MAPPER.toDomesticOut(onUsPayment);
    }

    public DomesticInPayment mapOnUsPaymentToDomesticInPayment(com.tenx.fraudamlmanager.payments.model.transactionmonitoring.OnUsPayment onUsPayment){
        return OnUsPaymentToDomesticInPaymentMapper.MAPPER.toDomesticIn(onUsPayment);
    }

}
