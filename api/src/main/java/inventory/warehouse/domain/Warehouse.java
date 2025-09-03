package inventory.warehouse.domain;

import lombok.Builder;

@Builder
public class Warehouse {

    private Long warehouseId;

    private String name;

    private String postcode;

    private String baseAddress;

    private String detailAddress;

    private String managerName;

    private String managerContact;
}
