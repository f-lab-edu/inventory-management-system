package inventory.outbound.controller;

import inventory.outbound.service.OutboundService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class OutboundController {

    private final OutboundService outboundService;

}
