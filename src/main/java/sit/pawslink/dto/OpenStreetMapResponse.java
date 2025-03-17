package sit.pawslink.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenStreetMapResponse {
    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("address")
    private Address address;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Address {
        private String city;
        private String country;
    }
}
