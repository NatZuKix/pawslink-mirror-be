package sit.pawslink.dto;

import lombok.Data;

@Data
public class ScanRecordRequest {
    private Integer petId;
    private Double latitude;
    private Double longitude;
}
