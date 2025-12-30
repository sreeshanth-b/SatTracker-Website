package com.sattracker.backend.controller;

import com.sattracker.backend.model.Tle;
import com.sattracker.backend.service.TleImportService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/tle")
@CrossOrigin("*")
public class TleController {

    private final TleImportService tleImportService;

    public TleController(TleImportService tleImportService) {
        this.tleImportService = tleImportService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        int count = tleImportService.loadFromFile(file);
        return "Imported " + count + " TLE(s) from file";
    }

    @GetMapping("/load")
    public String load(@RequestParam("url") String url) {
        int count = tleImportService.loadFromUrl(url);
        return "Imported " + count + " TLE(s) from URL";
    }

    @GetMapping("/limit/{n}")
    public List<Tle> getLimited(@PathVariable int n) {
        return tleImportService.getLimited(n);
    }

    @GetMapping("/all")
    public List<Tle> getAll() {
        return tleImportService.getAll();
    }

    @GetMapping("/search")
    public List<Tle> search(@RequestParam String query) {
        return tleImportService.search(query);
    }

    @GetMapping("/by-name")
    public List<Tle> byName(@RequestParam String name) {
        return tleImportService.findByName(name);
    }

    @GetMapping("/count")
    public long count() {
        return tleImportService.count();
    }
}
