package io.inventory.stock.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class StockTest {

    @Test
    void 재고상태가_IN_STOCK에서_LOW_STOCK을_거쳐_OUT_OF_STOCK으로_변경된다() {
        // given
        Stock stock = Stock.builder()
                .productId(1L)
                .warehouseId(1L)
                .locationInWarehouse("A-01")
                .actualStock(20)
                .allocatedStock(0)
                .safetyUnitStock(10)
                .stockStatus(StockStatus.IN_STOCK)
                .build();

        // when & then
        // 1) 충분한 재고 - IN_STOCK
        stock.decreaseStock(5); // 20 → 15
        assertThat(stock.getStockStatus()).isEqualTo(StockStatus.IN_STOCK);

        // 2) 안전재고 미만 - LOW_STOCK
        stock.decreaseStock(10); // 15 → 5
        assertThat(stock.getStockStatus()).isEqualTo(StockStatus.LOW_STOCK);

        // 3) 재고 없음 - OUT_OF_STOCK
        stock.decreaseStock(5); // 5 → 0
        assertThat(stock.getStockStatus()).isEqualTo(StockStatus.OUT_OF_STOCK);
    }

    @Test
    void 재고를_증가시키면_상태가_변경된다() {
        // given
        Stock stock = Stock.builder()
                .productId(1L)
                .warehouseId(1L)
                .locationInWarehouse("A-01")
                .actualStock(0)
                .allocatedStock(0)
                .safetyUnitStock(10)
                .stockStatus(StockStatus.OUT_OF_STOCK)
                .build();

        // when & then
        // 1) 재고를 추가했지만 안전재고 미만 - LOW_STOCK
        stock.increaseStock(5);
        assertThat(stock.getStockStatus()).isEqualTo(StockStatus.LOW_STOCK);

        // 2) 재고를 더 추가해서 안전재고 이상 - IN_STOCK
        stock.increaseStock(10);
        assertThat(stock.getStockStatus()).isEqualTo(StockStatus.IN_STOCK);
    }

    @Test
    void 음수나_0의_수량을_입력하면_예외가_발생한다() {
        // given
        Stock stock = Stock.builder()
                .productId(1L)
                .warehouseId(1L)
                .actualStock(10)
                .allocatedStock(0)
                .safetyUnitStock(5)
                .stockStatus(StockStatus.IN_STOCK)
                .build();

        // when & then
        assertThatThrownBy(() -> stock.increaseStock(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("수량은 0보다 커야 합니다.");

        assertThatThrownBy(() -> stock.decreaseStock(-5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("수량은 0보다 커야 합니다.");
    }

    @Test
    void 출고가능재고보다_많이_출고하면_예외가_발생한다() {
        // given
        Stock stock = Stock.builder()
                .productId(1L)
                .warehouseId(1L)
                .actualStock(5)
                .allocatedStock(0)
                .safetyUnitStock(3)
                .stockStatus(StockStatus.LOW_STOCK)
                .build();

        // when & then
        assertThatThrownBy(() -> stock.decreaseStock(10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고가 부족합니다.");
    }
}