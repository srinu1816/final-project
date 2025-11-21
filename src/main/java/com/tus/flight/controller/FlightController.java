package com.tus.flight.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tus.flight.model.Flight;
import com.tus.flight.repo.FlightRepository;

@RestController
@RequestMapping("/api/v1")
public class FlightController {

    private static final Logger logger = LoggerFactory.getLogger(FlightController.class);
    
    @Autowired
    private FlightRepository flightRepository;

    /**
     * Health check endpoint for Docker and monitoring
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        logger.info("Health check endpoint accessed - Service is healthy");
        return ResponseEntity.ok("Flight Service is running successfully! Status: OK");
    }

    /**
     * Get all flights with comprehensive logging
     */
    @GetMapping("/flights")
    public ResponseEntity<?> getAllFlights() {
        try {
            logger.info("GET /api/v1/flights - Request received to fetch all flights");
            
            long startTime = System.currentTimeMillis();
            List<Flight> flights = flightRepository.findAll();
            long endTime = System.currentTimeMillis();
            
            logger.info("GET /api/v1/flights - Successfully retrieved {} flights in {} ms", 
                       flights.size(), (endTime - startTime));
            logger.debug("Flight details: {}", flights);
            
            return ResponseEntity.ok(flights);
            
        } catch (Exception e) {
            logger.error("GET /api/v1/flights - Error occurred while fetching flights: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving flights: " + e.getMessage());
        }
    }

    /**
     * Get flight by ID with error handling
     */
    @GetMapping("/flights/{id}")
    public ResponseEntity<?> getFlightById(@RequestParam Long id) {
        try {
            logger.info("GET /api/v1/flights/{} - Request received", id);
            
            if (id == null || id <= 0) {
                logger.warn("GET /api/v1/flights/{} - Invalid flight ID provided", id);
                return ResponseEntity.badRequest().body("Invalid flight ID");
            }
            
            Flight flight = flightRepository.findById(id).orElse(null);
            
            if (flight == null) {
                logger.warn("GET /api/v1/flights/{} - Flight not found", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Flight with ID " + id + " not found");
            }
            
            logger.info("GET /api/v1/flights/{} - Flight found: {}", id, flight.getFlightNumber());
            return ResponseEntity.ok(flight);
            
        } catch (Exception e) {
            logger.error("GET /api/v1/flights/{} - Error occurred: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving flight: " + e.getMessage());
        }
    }

    /**
     * Search flights by departure city
     */
    @GetMapping("/flights/search")
    public ResponseEntity<?> getFlightsByDepartureCity(@RequestParam String departureCity) {
        try {
            logger.info("GET /api/v1/flights/search - Searching flights from departure city: {}", departureCity);
            
            if (departureCity == null || departureCity.trim().isEmpty()) {
                logger.warn("GET /api/v1/flights/search - Empty departure city provided");
                return ResponseEntity.badRequest().body("Departure city cannot be empty");
            }
            
            // This would require a custom repository method
            // For now, we'll filter manually (you should add this method to your repository)
            List<Flight> allFlights = flightRepository.findAll();
            List<Flight> filteredFlights = allFlights.stream()
                    .filter(flight -> departureCity.equalsIgnoreCase(flight.getDepartureCity()))
                    .toList();
            
            logger.info("GET /api/v1/flights/search - Found {} flights from {}", 
                       filteredFlights.size(), departureCity);
            
            return ResponseEntity.ok(filteredFlights);
            
        } catch (Exception e) {
            logger.error("GET /api/v1/flights/search - Error searching flights from {}: {}", 
                        departureCity, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error searching flights: " + e.getMessage());
        }
    }

    /**
     * Get flights statistics
     */
    @GetMapping("/flights/stats")
    public ResponseEntity<?> getFlightStatistics() {
        try {
            logger.info("GET /api/v1/flights/stats - Retrieving flight statistics");
            
            List<Flight> allFlights = flightRepository.findAll();
            
            // Create statistics object
            FlightStatistics stats = new FlightStatistics();
            stats.setTotalFlights(allFlights.size());
            stats.setTotalAirlines(allFlights.stream()
                    .map(Flight::getOperatingAirlines)
                    .distinct()
                    .count());
            stats.setTotalDepartureCities(allFlights.stream()
                    .map(Flight::getDepartureCity)
                    .distinct()
                    .count());
            stats.setTotalArrivalCities(allFlights.stream()
                    .map(Flight::getArrivalCity)
                    .distinct()
                    .count());
            
            logger.info("GET /api/v1/flights/stats - Statistics generated: {} total flights", 
                       stats.getTotalFlights());
            
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            logger.error("GET /api/v1/flights/stats - Error generating statistics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating flight statistics: " + e.getMessage());
        }
    }

    /**
     * Test endpoint for generating different log levels
     */
    @GetMapping("/logs/test")
    public ResponseEntity<String> testLogging() {
        logger.trace("This is a TRACE level log - Flight Service");
        logger.debug("This is a DEBUG level log - Flight Service");
        logger.info("This is an INFO level log - Flight Service");
        logger.warn("This is a WARN level log - Flight Service");
        logger.error("This is an ERROR level log - Flight Service");
        
        // Log some structured data for ELK
        logger.info("Flight API test endpoint accessed - User: anonymous, IP: 127.0.0.1, Action: log_test");
        
        return ResponseEntity.ok("Logging test completed. Check your logs and ELK stack.");
    }

    /**
     * Inner class for flight statistics
     */
    public static class FlightStatistics {
        private int totalFlights;
        private long totalAirlines;
        private long totalDepartureCities;
        private long totalArrivalCities;

        // Getters and Setters
        public int getTotalFlights() { return totalFlights; }
        public void setTotalFlights(int totalFlights) { this.totalFlights = totalFlights; }
        
        public long getTotalAirlines() { return totalAirlines; }
        public void setTotalAirlines(long totalAirlines) { this.totalAirlines = totalAirlines; }
        
        public long getTotalDepartureCities() { return totalDepartureCities; }
        public void setTotalDepartureCities(long totalDepartureCities) { this.totalDepartureCities = totalDepartureCities; }
        
        public long getTotalArrivalCities() { return totalArrivalCities; }
        public void setTotalArrivalCities(long totalArrivalCities) { this.totalArrivalCities = totalArrivalCities; }
    }
}
