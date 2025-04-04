package com.example.lab4.service;

import com.example.lab4.model.Location;
import com.example.lab4.model.SunriseSunset;
import com.example.lab4.repository.LocationRepository;
import com.example.lab4.repository.SunriseSunsetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository repository;
    private final SunriseSunsetRepository sunriseSunsetRepository;

    @Transactional(readOnly = true)
    public List<Location> getAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Location> getById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public Location create(Location location, List<Long> sunriseSunsetIds) {
        if (sunriseSunsetIds != null && !sunriseSunsetIds.isEmpty()) {
            List<SunriseSunset> sunriseSunsets = sunriseSunsetRepository.findAllById(sunriseSunsetIds);
            location.getSunriseSunsets().addAll(sunriseSunsets);
        }
        return repository.save(location);
    }

    @Transactional
    public Optional<Location> update(Long id, Location updatedData, List<Long> sunriseSunsetIds) {
        return repository.findById(id).map(location -> {
            location.setName(updatedData.getName());
            location.setCountry(updatedData.getCountry());

            if (sunriseSunsetIds != null) {
                location.getSunriseSunsets().clear();
                List<SunriseSunset> sunriseSunsets = sunriseSunsetRepository.findAllById(sunriseSunsetIds);
                location.getSunriseSunsets().addAll(sunriseSunsets);
            }
            return repository.save(location);
        });
    }

    @Transactional
    public boolean delete(Long id) {
        return repository.findById(id).map(location -> {
            repository.delete(location);
            return true;
        }).orElse(false);
    }

    @Transactional(readOnly = true)
    public List<Location> getLocationsByDate(String date) {
        return repository.findLocationsBySunriseSunsetDate(date);
    }
}