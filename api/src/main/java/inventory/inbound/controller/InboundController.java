package inventory.inbound.controller;

import inventory.common.dto.response.ApiResponse;
import inventory.common.dto.response.PageResponse;
import inventory.inbound.controller.request.CreateInboundRequest;
import inventory.inbound.controller.request.UpdateInboundStatusRequest;
import inventory.inbound.controller.response.InboundResponse;
import inventory.inbound.service.InboundService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

        List<InboundResponse> inbounds = inboundService.findAll();

        int startIndex = currentPageNumber * pageSize;
        int endIndex = Math.min(startIndex + pageSize, inbounds.size());
        List<InboundResponse> pagedInbounds = inbounds.subList(startIndex, endIndex);

        PageResponse<InboundResponse> pageResponse = PageResponse.of(
                pagedInbounds, currentPageNumber, pageSize, inbounds.size());

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