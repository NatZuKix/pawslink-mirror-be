package sit.pawslink.dto;

import lombok.Data;
import sit.pawslink.entities.Record;
import java.time.LocalDateTime;

@Data
public class ScanRecordResponse {
    private Integer scanId;
    private Integer petId;          // แสดงเฉพาะ petId
    private String petName;         // ชื่อสัตว์เลี้ยง
    private String petSpecies;      // สายพันธุ์
    private String petBreed;
    private String petProfileUrl;   // URL ของโปรไฟล์สัตว์เลี้ยง
    private String petImageUrl;     // รูปภาพสัตว์เลี้ยง
    private Double latitude;
    private Double longitude;
    private String address;
    private String scannedAt;
    private Integer ownerId;

    public ScanRecordResponse(Record scanRecord) {
        this.scanId = scanRecord.getScanId();
        this.petId = scanRecord.getPet().getPetId();
        this.petName = scanRecord.getPet().getName();
        this.petSpecies = scanRecord.getPet().getSpecies();
        this.petBreed = scanRecord.getPet().getBreed();
        this.petProfileUrl = scanRecord.getPet().getProfileUrl();
        this.petImageUrl = scanRecord.getPet().getImageUrl();
        this.latitude = scanRecord.getLatitude();
        this.longitude = scanRecord.getLongitude();
        this.address = scanRecord.getAddress();
        this.scannedAt = scanRecord.getScannedAt().toString();
        this.ownerId = scanRecord.getPet().getUser().getUserId();
    }
}
