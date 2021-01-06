package com.tenx.fraudamlmanager.paymentsv3.domestic.infrastructure;

import com.tenx.fraudamlmanager.paymentsv3.domestic.domain.DomesticInPaymentV3;
import com.tenx.fraudamlmanager.paymentsv3.domestic.domain.DomesticOutPaymentV3;
import com.tenx.fraudamlmanager.paymentsv3.domestic.domain.DomesticOutReturnPaymentV3;
import com.tenx.fraudamlmanager.paymentsv3.domestic.infrastructure.tma.DomesticInPaymentTMARequestV3;
import com.tenx.fraudamlmanager.paymentsv3.domestic.infrastructure.tma.DomesticOutPaymentTMARequestV3;
import com.tenx.fraudamlmanager.paymentsv3.domestic.infrastructure.tma.DomesticOutReturnPaymentTMARequestV3;
import javax.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DomesticPaymentTMAMapper {

    DomesticPaymentTMAMapper MAPPER = Mappers.getMapper(DomesticPaymentTMAMapper.class);

    DomesticOutReturnPaymentTMARequestV3 toDomesticOutReturnTMARequest(DomesticOutReturnPaymentV3 domesticOutPaymentRequestV3);

    DomesticOutPaymentTMARequestV3 toDomesticOutTMARequest(@Valid DomesticOutPaymentV3 domesticOutPaymentRequestV3);

    DomesticInPaymentTMARequestV3 toDomesticInTMARequest(DomesticInPaymentV3 domesticInPaymentRequestV3);
}
