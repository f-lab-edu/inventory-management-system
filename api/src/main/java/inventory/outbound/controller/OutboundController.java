package inventory.outbound.controller;

import inventory.common.dto.response.ApiResponse;
import inventory.common.dto.response.PageResponse;
import inventory.outbound.domain.enums.OutboundStatus;
import inventory.outbound.service.OutboundService;
import inventory.outbound.service.request.CreateOutboundRequest;
import inventory.outbound.service.response.OutboundResponse;
import inventory.outbound.service.response.OutboundSummaryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RequiredArgsConstructor
@RequestMapping("/api/v1/outbounds")
@RestController
public class OutboundController {

    private final OutboundService outboundService;

    @PostMapping
    public ResponseEntity<ApiResponse<OutboundResponse>> createOutbound(
            @Valid @RequestBody CreateOutboundRequest request
    ) {
        OutboundResponse response = outboundService.createOutbound(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, response));
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<OutboundResponse>> getOutbound(@PathVariable Long id) {
        OutboundResponse response = outboundService.findById(id);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OutboundSummaryResponse>>> searchOutbounds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String orderNumber,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) OutboundStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<OutboundSummaryResponse> outboundPage = outboundService.searchOutbounds(
                orderNumber, warehouseId, status, startDate, endDate, pageable);

        PageResponse<OutboundSummaryResponse> pageResponse = PageResponse.of(
                outboundPage.getContent(),
                outboundPage.getNumber(),
                outboundPage.getSize(),
                outboundPage.getTotalElements()
        );

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    @PostMapping("{id}/picking")
    public ResponseEntity<ApiResponse<Void>> startPicking(@PathVariable Long id) {
        outboundService.startPicking(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("{id}/complete")
    public ResponseEntity<ApiResponse<Void>> completeOutbound(@PathVariable Long id) {
        outboundService.completeOutbound(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelOutbound(@PathVariable Long id) {
        outboundService.cancelOutbound(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOutbound(@PathVariable Long id) {
        outboundService.deleteOutbound(id);
        return ResponseEntity.noContent().build();
    }
}
