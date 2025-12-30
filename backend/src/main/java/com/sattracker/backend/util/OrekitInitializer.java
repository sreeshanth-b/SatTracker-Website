package com.sattracker.backend.util;

import jakarta.annotation.PostConstruct;
import org.orekit.data.*;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class OrekitInitializer {

    @PostConstruct
    public void init() {
        try {
            // ✅ ABSOLUTE PATH (FIX)
            File orekitData = new File(
                System.getProperty("user.home")
                + "/Documents/SatTracker/orekit-data"
            );

            if (!orekitData.exists()) {
                throw new RuntimeException(
                    "orekit-data folder not found at: "
                    + orekitData.getAbsolutePath()
                );
            }

            DataProvidersManager manager =
                    DataContext.getDefault().getDataProvidersManager();

            manager.addProvider(new DirectoryCrawler(orekitData));

            System.out.println("✅ Orekit data loaded from: "
                    + orekitData.getAbsolutePath());

        } catch (Exception e) {
            throw new RuntimeException(
                "Failed to load Orekit data", e
            );
        }
    }
}
