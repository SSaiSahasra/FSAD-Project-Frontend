package com.conference.platform.controller;

import com.conference.platform.dto.ApiResponse;
import com.conference.platform.dto.PaperSubmissionDto;
import com.conference.platform.entity.SubmissionStatus;
import com.conference.platform.service.PaperSubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/submissions")
@Tag(name = "Submissions", description = "Endpoints for managing paper submissions")
@SecurityRequirement(name = "bearerAuth")
public class SubmissionController {

    private final PaperSubmissionService submissionService;

    public SubmissionController(PaperSubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @Operation(summary = "Submit a research paper")
    @PostMapping
    public ResponseEntity<ApiResponse<PaperSubmissionDto>> submitPaper(
            @RequestBody PaperSubmissionDto submissionDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                submissionService.submitPaper(submissionDto, userDetails.getUsername()),
                "Paper submitted successfully"
        ));
    }

    @Operation(summary = "Get user's personal submissions")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<PaperSubmissionDto>>> getMySubmissions(
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                submissionService.getMySubmissions(userDetails.getUsername(), pageable)
        ));
    }

    @Operation(summary = "Get all submissions (Admin/Reviewer only)")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    public ResponseEntity<ApiResponse<Page<PaperSubmissionDto>>> getAllSubmissions(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                submissionService.getAllSubmissions(pageable)
        ));
    }

    @Operation(summary = "Update submission status (Admin only)")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaperSubmissionDto>> updateStatus(
            @PathVariable Long id,
            @RequestParam SubmissionStatus status
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                submissionService.updateStatus(id, status),
                "Status updated successfully"
        ));
    }

    @Operation(summary = "Assign reviewer to submission (Admin only)")
    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaperSubmissionDto>> assignReviewer(
            @PathVariable Long id,
            @RequestParam Long reviewerId
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                submissionService.assignReviewer(id, reviewerId),
                "Reviewer assigned successfully"
        ));
    }
}
