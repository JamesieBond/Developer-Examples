package com.tenx.fraudamlmanager.cards.infrastructure;

import com.tenx.fraudamlmanager.cards.domain.PartyInfoStoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class PartyInfoStoreServiceImplTest {

  @MockBean
  PartyInfoEntityRepository partyInfoEntityRepository;

  private PartyInfoStoreService partyInfoStoreService;

  @BeforeEach
  public void initTest() {
    partyInfoStoreService = new PartyInfoStoreServiceImpl(partyInfoEntityRepository);
  }

  @Test
  public void testStorePartyInfo() {
    partyInfoStoreService.storePartyInfo("partyKey", "postCode");
    Mockito.verify(partyInfoEntityRepository, Mockito.times(1))
        .save(new PartyInfoEntity("partyKey", "postCode"));

  }

}
