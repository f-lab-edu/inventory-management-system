package inventory.outbound.controller;

import inventory.common.dto.response.ApiResponse;
import inventory.outbound.service.OutboundService;
import inventory.outbound.service.request.CreateOutboundRequest;
import inventory.outbound.service.response.OutboundResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
