package com.tenx.fraudamlmanager.paymentsv3.onus.api;

import com.tenx.fraudamlmanager.paymentsv3.onus.infrastructure.OnUsPaymentV3;
import org.springframework.stereotype.Component;

@Component
public class OnUsPaymentMapper {

    public OnUsPaymentV3 toOnUsPayment(OnUsPaymentRequestV3 onUsPaymentRequestV3) {
        return OnUsMapper.MAPPER.toOnUs(onUsPaymentRequestV3);
    }

}
