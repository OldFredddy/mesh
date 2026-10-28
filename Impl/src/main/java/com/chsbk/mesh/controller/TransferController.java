package com.chsbk.mesh.controller;

import com.chsbk.mesh.model.TransferRequest;
import com.chsbk.mesh.service.TransferService;
import com.chsbk.mesh.transport.MeshEndpoints;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(MeshEndpoints.TRANSFERS_ROOT)
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    public ResponseEntity<Void> transfer(@AuthenticationPrincipal UserDetails principal,
                                         @Valid @RequestBody TransferRequest request) {
        long senderUserId = Long.parseLong(principal.getUsername());
        transferService.transfer(senderUserId, request.to(), request.amount());
        return ResponseEntity.ok().build();
    }
}
