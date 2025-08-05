package io.inventory.warehouse.command.service;

import io.inventory.warehouse.command.dto.request.WarehouseCreateRequest;
import io.inventory.warehouse.command.repository.WarehouseRepository;
import io.inventory.warehouse.query.dto.WarehouseDetailResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("local")
@Transactional
@SpringBootTest
class WarehouseCommandServiceTest {

    @Autowired
    private WarehouseCommandService warehouseCommandService;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Test
    @DisplayName("창고 생성 시 DB에 저장되고 DTO가 올바르게 반환되어야 한다")
    void createWarehouse_savesAndReturnsDto() {
        WarehouseCreateRequest request = new WarehouseCreateRequest(
                "서울창고",
                12345,
                "서울시 강남구",
                "상세주소 101호",
                "01012345678"
        );

        WarehouseDetailResponse response = warehouseCommandService.createWarehouse(request);

        assertThat(response.warehouseId()).isNotNull();
        assertThat(response.name()).isEqualTo("서울창고");
        assertThat(response.postcode()).isEqualTo(12345);
        assertThat(response.baseAddress()).isEqualTo("서울시 강남구");
        assertThat(response.detailAddress()).isEqualTo("상세주소 101호");
    }

}