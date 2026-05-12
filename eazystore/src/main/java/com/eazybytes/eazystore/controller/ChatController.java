package com.eazybytes.eazystore.controller;

import com.eazybytes.eazystore.dto.ChatRequestDto;
import com.eazybytes.eazystore.dto.ChatResponseDto;
import com.eazybytes.eazystore.service.IChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final IChatService chatService;

    @PostMapping
    public ResponseEntity<ChatResponseDto> chat(@Valid @RequestBody ChatRequestDto request) {
        String reply = chatService.ask(request.getMessage());
        return ResponseEntity.ok(new ChatResponseDto(reply));
    }
}
