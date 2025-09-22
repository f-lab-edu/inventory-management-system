package inventory.inbound.controller;

import inventory.common.dto.response.ApiResponse;
import inventory.common.dto.response.PageResponse;
import inventory.inbound.domain.enums.InboundStatus;
import inventory.inbound.service.InboundService;
import inventory.inbound.service.request.CreateInboundRequest;
import inventory.inbound.service.request.UpdateInboundStatusRequest;
import inventory.inbound.service.response.InboundResponse;
import inventory.inbound.service.response.InboundSummaryResponse;
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
@RequestMapping("/api/v1/inbounds")
@RestController
public class InboundController {

    private final InboundService inboundService;

    @PostMapping
    public ResponseEntity<ApiResponse<InboundResponse>> createInbound(
            @Valid @RequestBody CreateInboundRequest request) {
        InboundResponse response = inboundService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, response));
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<InboundResponse>> getInbound(@PathVariable Long id) {
        InboundResponse response = inboundService.findById(id);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<InboundSummaryResponse>>> searchInbounds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) InboundStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<InboundSummaryResponse> inboundPage = inboundService.findAllWithConditions(
                warehouseId, supplierId, status, startDate, endDate, pageable);

        PageResponse<InboundSummaryResponse> pageResponse = PageResponse.of(
                inboundPage.getContent(),
                page,
                size,
                inboundPage.getTotalElements()
        );

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    @PutMapping("{id}/status")
    public ResponseEntity<ApiResponse<InboundResponse>> updateInboundStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateInboundStatusRequest request) {

        InboundResponse response = inboundService.updateStatus(id, request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("{id}/inspection")
    public ResponseEntity<ApiResponse<InboundResponse>> updateInboundStatusToInspection(
            @PathVariable Long id) {
        InboundResponse response = inboundService.updateStatus(id, new UpdateInboundStatusRequest(InboundStatus.INSPECTING));

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelInbound(@PathVariable Long id) {
        inboundService.cancelInbound(id);

        return ResponseEntity.ok().build();
    }

    @PutMapping("{id}/complete")
    public ResponseEntity<ApiResponse<Void>> completeInbound(@PathVariable Long id) {
        inboundService.completeInbound(id);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> deleteInbound(@PathVariable Long id) {
        inboundService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}