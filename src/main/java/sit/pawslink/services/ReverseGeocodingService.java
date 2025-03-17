package sit.pawslink.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sit.pawslink.dto.OpenStreetMapResponse;

@Service
public class ReverseGeocodingService {
    private final RestTemplate restTemplate;

    public ReverseGeocodingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getAddressFromCoordinates(Double latitude, Double longitude) {
        try {
            String url = String.format(
                    "https://nominatim.openstreetmap.org/reverse?lat=%f&lon=%f&format=json",
                    latitude, longitude
            );

            OpenStreetMapResponse response = restTemplate.getForObject(url, OpenStreetMapResponse.class);

            if (response != null && response.getDisplayName() != null) {
                return response.getDisplayName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Unknown Address";
    }
}
