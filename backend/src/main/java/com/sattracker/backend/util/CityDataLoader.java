package com.sattracker.backend.util;

import com.sattracker.backend.model.GroundStation;
import com.sattracker.backend.repository.GroundStationRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
public class CityDataLoader {

    private final GroundStationRepository repository;

    public CityDataLoader(GroundStationRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void loadCities() {
        try {

            if (repository.count() > 0) {
                System.out.println("Ground stations already loaded");
                return;
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            getClass()
                                    .getClassLoader()
                                    .getResourceAsStream("data/worldcities.csv"),
                            StandardCharsets.UTF_8
                    )
            );

            String line;
            reader.readLine(); // skip header

            int count = 0;

            while ((line = reader.readLine()) != null && count < 5000) {

                // SAFE CSV split (handles commas inside quotes)
                String[] cols = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                if (cols.length < 5) continue;

                GroundStation gs = new GroundStation();
                gs.setName(cols[0].replace("\"", ""));
                gs.setLatitude(Double.parseDouble(cols[2].replace("\"", "")));
                gs.setLongitude(Double.parseDouble(cols[3].replace("\"", "")));
                gs.setCountry(cols[4].replace("\"", ""));
                gs.setAltitude(0);

                repository.save(gs);
                count++;
            }

            System.out.println("Loaded " + count + " cities into DB");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load city data");
        }
    }
}
