package com.bootcamp.interviewflow.controller;

import com.bootcamp.interviewflow.dto.ApplicationListDTO;
import com.bootcamp.interviewflow.dto.CreateApplicationRequest;
import com.bootcamp.interviewflow.model.Application;
import com.bootcamp.interviewflow.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/applications")
    public ResponseEntity<Application> create(@RequestBody @Valid CreateApplicationRequest dto) {
        Application created = applicationService.create(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @GetMapping("/applications")
    public ResponseEntity<List<Application>> findAll() {
        List<Application> all = applicationService.findAll();
        return ResponseEntity.ok(all);
    }

    @GetMapping(path = "/users/{userId}/applications")
    public ResponseEntity<List<ApplicationListDTO>> getUserApplications(@PathVariable Long userId) {
        return ResponseEntity.ok(applicationService.findAllByUserId(userId));
    }

    @DeleteMapping("/applications/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        applicationService.delete(id);
        return ResponseEntity.ok("Application with id " + id + " deleted");
    }
}
