package io.inventory.supplier.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class SupplierTest {

    @Test
    void 연락처와_주소를_변경할_수_있다() {
        // given
        Supplier supplier = Supplier.builder()
                .name("테스트 공급업체")
                .contact("01012345678")
                .postcode(12345)
                .baseAddress("서울특별시")
                .detailAddress("청파로 40")
                .build();

        // when
        supplier.updateContactInfo("01023456789", 54321, "서울시 송파구", "테스트로 10");

        // then
        Assertions.assertThat(supplier.getContact())
                .isEqualTo("01023456789");
    }
}