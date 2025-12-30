package com.sattracker.backend.service;

import com.sattracker.backend.model.Tle;
import com.sattracker.backend.repository.TleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class TleImportService {

    private final TleRepository tleRepository;

    public TleImportService(TleRepository tleRepository) {
        this.tleRepository = tleRepository;
    }

    // Import from uploaded file
    @Transactional
    public int loadFromFile(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            return loadAndSave(reader);
        } catch (Exception e) {
            throw new RuntimeException("File import error: " + e.getMessage(), e);
        }
    }

    // Import from remote URL
    @Transactional
    public int loadFromUrl(String url) {
        try {
            URL remote = new URL(url);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(remote.openStream()))) {
                return loadAndSave(reader);
            }
        } catch (Exception e) {
            throw new RuntimeException("URL import error: " + e.getMessage(), e);
        }
    }

    private int loadAndSave(BufferedReader reader) throws Exception {
        List<Tle> batch = new ArrayList<>();
        int count = 0;

        String name;
        while ((name = reader.readLine()) != null) {
            String line1 = reader.readLine();
            String line2 = reader.readLine();

            if (line1 == null || line2 == null) break;

            batch.add(new Tle(name.trim(), line1.trim(), line2.trim()));
            count++;

            if (batch.size() >= 1000) {
                tleRepository.saveAll(batch);
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            tleRepository.saveAll(batch);
        }

        return count;
    }

    // helper functions
    public List<Tle> getAll() { return tleRepository.findAll(); }
    public List<Tle> getLimited(int n) { return tleRepository.findLimited(n); }
    public List<Tle> findByName(String name) { return tleRepository.findByName(name); }
    public List<Tle> search(String fragment) { return tleRepository.findByNameContainingIgnoreCase(fragment); }
    public long count() { return tleRepository.count(); }
}
