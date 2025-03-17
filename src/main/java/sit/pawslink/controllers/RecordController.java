package sit.pawslink.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.pawslink.dto.ScanRecordRequest;
import sit.pawslink.dto.ScanRecordResponse;
import sit.pawslink.entities.Record;
import sit.pawslink.services.RecordService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/scan-records")
public class RecordController {
    private final RecordService recordService;

    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @PostMapping
    public ResponseEntity<?> createScanRecord(@RequestBody ScanRecordRequest request) {
        try {
            Record scanRecord = recordService.saveRecord(
                    request.getPetId(),
                    request.getLatitude(),
                    request.getLongitude()
            );
            return ResponseEntity.status(201).body(new ScanRecordResponse(scanRecord));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getScanRecordsByUser() {
        try {
            List<Record> scanRecords = recordService.getScanRecordsByUser();
            List<ScanRecordResponse> response = scanRecords.stream()
                    .map(ScanRecordResponse::new)
                    .collect(Collectors.toList());

            return response.isEmpty()
                    ? ResponseEntity.noContent().build()
                    : ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{petId}")
    public ResponseEntity<?> getScanRecordsByPetId(@PathVariable Integer petId) {
        try {
            List<Record> scanRecords = recordService.getScanRecordsByPetId(petId);
            List<ScanRecordResponse> response = scanRecords.stream()
                    .map(ScanRecordResponse::new)
                    .collect(Collectors.toList());
            return response.isEmpty()
                    ? ResponseEntity.noContent().build()
                    : ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{scanId}")
    public ResponseEntity<?> deleteScanRecord(@PathVariable Integer scanId) {
        try {
            recordService.deleteScanRecord(scanId);
            return ResponseEntity.ok("Scan record deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
