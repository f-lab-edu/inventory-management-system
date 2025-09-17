package inventory.inbound.controller;

import inventory.common.dto.response.ApiResponse;
import inventory.common.dto.response.PageResponse;
import inventory.inbound.service.request.CreateInboundRequest;
import inventory.inbound.service.request.UpdateInboundStatusRequest;
import inventory.inbound.service.response.InboundResponse;
import inventory.inbound.domain.Inbound;
import inventory.inbound.service.InboundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<ApiResponse<PageResponse<InboundResponse>>> searchInbounds(
            @RequestParam(defaultValue = "0") int currentPageNumber,
            @RequestParam(defaultValue = "50") int pageSize) {

        List<Inbound> inbounds = inboundService.findAll();

        int startIndex = currentPageNumber * pageSize;
        int endIndex = Math.min(startIndex + pageSize, inbounds.size());
        List<Inbound> pagedInbounds = inbounds.subList(startIndex, endIndex);

        // TODO: InboundResponse.from() 메서드가 추가 파라미터를 필요로 하므로 
        // 각 Inbound에 대해 필요한 정보를 조회해야 함
        // 현재는 빈 리스트로 처리
        List<InboundResponse> responses = List.of();

        PageResponse<InboundResponse> pageResponse = PageResponse.of(
                responses, currentPageNumber, pageSize, inbounds.size());

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    @PutMapping("{id}/status")
    public ResponseEntity<ApiResponse<InboundResponse>> updateInboundStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateInboundStatusRequest request) {

        InboundResponse response = inboundService.updateStatus(id, request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> deleteInbound(@PathVariable Long id) {
        inboundService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}