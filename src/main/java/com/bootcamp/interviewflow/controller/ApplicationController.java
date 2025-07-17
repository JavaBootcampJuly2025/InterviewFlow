package com.bootcamp.interviewflow.controller;

import com.bootcamp.interviewflow.dto.CreateApplicationRequest;
import com.bootcamp.interviewflow.model.Application;
import com.bootcamp.interviewflow.service.ApplicationServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationServiceImpl service;

    @PostMapping
    public ResponseEntity<Application> create(@RequestBody @Valid CreateApplicationRequest dto) {
        Application created = service.create(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<Application>> findAll() {
        List<Application> all = service.findAll();
        return ResponseEntity.ok(all);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Application with id " + id + " deleted");
    }
}
