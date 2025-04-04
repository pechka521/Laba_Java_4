package com.example.lab4.service;

import com.example.lab4.model.Location;
import com.example.lab4.model.SunriseSunset;
import com.example.lab4.repository.LocationRepository;
import com.example.lab4.repository.SunriseSunsetRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationService {

    private static final Logger logger = LoggerFactory.getLogger(LocationService.class);

    private final LocationRepository repository;
    private final SunriseSunsetRepository sunriseSunsetRepository;
    private final Map<String, List<Location>> locationCache;

    @Transactional(readOnly = true)
    public List<Location> getAll() {
        String cacheKey = "all_locations";
        if (locationCache.containsKey(cacheKey)) {
            logger.debug("Returning cached locations for key: {}", cacheKey);
            return locationCache.get(cacheKey);
        }
        logger.debug("Cache miss, querying database for all locations");
        List<Location> locations = repository.findAll();
        locationCache.put(cacheKey, locations);
        return locations;
    }

    @Transactional(readOnly = true)
    public Optional<Location> getById(Long id) {
        String cacheKey = "location_" + id;
        if (locationCache.containsKey(cacheKey)) {
            logger.debug("Returning cached location for key: {}", cacheKey);
            return Optional.ofNullable(locationCache.get(cacheKey).get(0));
        }
        logger.debug("Cache miss, querying database for location ID: {}", id);
        Optional<Location> location = repository.findById(id);
        location.ifPresent(l -> locationCache.put(cacheKey, List.of(l)));
        return location;
    }

    @Transactional
    public Location create(Location location, List<Long> sunriseSunsetIds) {
        if (sunriseSunsetIds != null && !sunriseSunsetIds.isEmpty()) {
            List<SunriseSunset> sunriseSunsets = sunriseSunsetRepository.findAllById(sunriseSunsetIds);
            location.getSunriseSunsets().addAll(sunriseSunsets);
        }
        Location saved = repository.save(location);
        locationCache.clear();
        logger.debug("Cache cleared after creating location");
        return saved;
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
            Location saved = repository.save(location);
            locationCache.clear();
            logger.debug("Cache cleared after updating location ID: {}", id);
            return saved;
        });
    }

    @Transactional
    public boolean delete(Long id) {
        return repository.findById(id).map(location -> {
            repository.delete(location);
            locationCache.clear();
            logger.debug("Cache cleared after deleting location ID: {}", id);
            return true;
        }).orElse(false);
    }

    @Transactional(readOnly = true)
    public List<Location> getLocationsByDate(String date) {
        String cacheKey = "locations_date_" + date;

        if (locationCache.containsKey(cacheKey)) {
            logger.debug("Returning cached locations for key: {}", cacheKey);
            return locationCache.get(cacheKey);
        }

        logger.debug("Cache miss, querying database for locations by date: {}", date);
        List<Location> locations = repository.findLocationsBySunriseSunsetDate(date);
        locationCache.put(cacheKey, locations);
        return locations;
    }
}