package apap.ti._5.vehicle_rental_2306245592_be.service;

import apap.ti._5.vehicle_rental_2306245592_be.dto.ProvinceDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class LocationService {
    private static final String PROVINCE_API_URL = "https://wilayah.id/api/provinces.json";
    private final RestTemplate restTemplate;
    private List<String> provinces = new ArrayList<>();
    private boolean isInitialized = false;

    public LocationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void initializeProvinces() {
        if (isInitialized) {
            return;
        }
        try {
            System.out.println("Fetching provinces from API...");
            ProvinceDTO response = restTemplate.getForObject(PROVINCE_API_URL, ProvinceDTO.class);
            if (response != null && response.getData() != null) {
                for (ProvinceDTO.Province province : response.getData()) {
                    provinces.add(province.getName());
                }
                isInitialized = true;
                System.out.println("✅ " + provinces.size() + " provinces loaded successfully");
            }
        } catch (Exception e) {
            System.err.println("❌ Error fetching provinces: " + e.getMessage());
            // Fallback data jika API gagal
            addFallbackProvinces();
        }
    }

    public String getRandomProvince() {
        if (!isInitialized || provinces.isEmpty()) {
            initializeProvinces();
        }
        Random random = new Random();
        return provinces.get(random.nextInt(provinces.size()));
    }

    private void addFallbackProvinces() {
        String[] fallbackProvinces = {
            "Aceh", "Bali", "Banten", "Bengkulu", "Daerah Istimewa Yogyakarta",
            "DKI Jakarta", "Gorontalo", "Jambi", "Jawa Barat", "Jawa Tengah",
            "Jawa Timur", "Kalimantan Barat", "Kalimantan Selatan", "Kalimantan Tengah",
            "Kalimantan Timur", "Kalimantan Utara", "Kepulauan Bangka Belitung",
            "Kepulauan Riau", "Lampung", "Maluku", "Maluku Utara", "Nusa Tenggara Barat",
            "Nusa Tenggara Timur", "Papua", "Papua Barat", "Papua Barat Daya",
            "Papua Pegunungan", "Papua Selatan", "Papua Tengah", "Riau", "Sulawesi Barat",
            "Sulawesi Selatan", "Sulawesi Tengah", "Sulawesi Tenggara", "Sulawesi Utara",
            "Sumatera Barat", "Sumatera Selatan", "Sumatera Utara"
        };
        for (String province : fallbackProvinces) {
            provinces.add(province);
        }
        isInitialized = true;
        System.out.println("⚠️ Using fallback provinces list (" + provinces.size() + " provinces)");
    }
}