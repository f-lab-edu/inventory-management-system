package inventory.outbound.controller;

import inventory.common.dto.response.ApiResponse;
import inventory.outbound.service.OutboundService;
import inventory.outbound.service.request.CreateOutboundRequest;
import inventory.outbound.service.response.CreateOutboundResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/outbounds")
@RestController
public class OutboundController {

    private final OutboundService outboundService;

    @PostMapping
    public ResponseEntity<ApiResponse<CreateOutboundResponse>> createOutbound(
            @Valid @RequestBody CreateOutboundRequest request
    ) {
        CreateOutboundResponse response = outboundService.createOutbound(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, response));
    }
}
