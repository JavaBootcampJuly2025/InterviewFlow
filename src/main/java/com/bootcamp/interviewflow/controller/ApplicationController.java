package com.bootcamp.interviewflow.controller;

import com.bootcamp.interviewflow.dto.CreateApplicationDTO;
import com.bootcamp.interviewflow.model.Application;
import com.bootcamp.interviewflow.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService service;

    public ApplicationController(ApplicationService service) {
        this.service = service;
    }


    @PostMapping
    public ResponseEntity<Application> create(@RequestBody @Valid CreateApplicationDTO dto) {
        Application created = service.createFromDto(dto);
        return ResponseEntity.ok(created);
    }


    @GetMapping
    public ResponseEntity<List<Application>> findAll() {
        List<Application> all = service.findAll();
        return ResponseEntity.ok(all);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
