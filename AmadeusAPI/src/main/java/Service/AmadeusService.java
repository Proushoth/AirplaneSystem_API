package Service;

import Data.Flight;
import Data.FlightRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class AmadeusService {

    private static final String AMADEUS_API_URL = "https://test.api.amadeus.com/v2/shopping/flight-offers";

    @Autowired
    private FlightRepository flightRepository;

    @Value("${amadeus.api.access-token}")
    private String accessToken;

    public List<Flight> searchFlights(String type, String origin, String destination, String departureDate,
                                      String returnDate, double price) {
        // Validate request parameters
        if (StringUtils.isEmpty(type) || StringUtils.isEmpty(origin) || StringUtils.isEmpty(destination)
                || StringUtils.isEmpty(departureDate) || StringUtils.isEmpty(returnDate)) {
            throw new IllegalArgumentException("Missing required request parameters");
        }

        // Log the incoming request details
        System.out.println("Received request: type=" + type + ", origin=" + origin + ", destination=" + destination
                + ", departureDate=" + departureDate + ", returnDate=" + returnDate + ", price=" + price);

        // Create RestTemplate and set the LoggingRequestInterceptor
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(new Debug.LoggingRequestInterceptor()));

        // Build the URL using UriComponentsBuilder
        String url = UriComponentsBuilder.fromUriString(AMADEUS_API_URL)
                .queryParam("type", type)
                .queryParam("originLocationCode", origin)
                .queryParam("destinationLocationCode", destination)
                .queryParam("departureDate", departureDate)
                .queryParam("returnDate", returnDate)
                .queryParam("price", price)
                .build().toUriString();

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Make the HTTP request
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            // Check for API errors
            if (response.getStatusCode().is4xxClientError()) {
                throw new RestClientException("API returned a client error: " + response.getStatusCode());
            }

            // Parse the JSON response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(response.getBody());

            // Extract flight data from JSON response and create Flight objects
            List<Flight> flights = new ArrayList<>();
            if (jsonNode.has("data")) {
                for (JsonNode flightNode : jsonNode.get("data")) {
                    Flight flight = new Flight();
                    flight.setType(flightNode.get("type").asText());
                    flight.setOrigin(flightNode.get("origin").get("iataCode").asText());
                    flight.setDestination(flightNode.get("destination").get("iataCode").asText());
                    flight.setDepartureDate(flightNode.get("departureDate").asText());
                    flight.setReturnDate(flightNode.get("returnDate").asText());
                    flight.setPrice(flightNode.get("price").get("total").asDouble());
                    flights.add(flight);
                }

                // Storing flight data in the database
                for (Flight flight : flights) {
                    flightRepository.saveFlightData(flight.getType(), flight.getOrigin(), flight.getDestination(),
                            flight.getDepartureDate(), flight.getReturnDate(), flight.getPrice());
                }
            }

            // Return the list of flights
            return flights;

        } catch (JsonProcessingException e) {
            // Handle JSON processing exception
            e.printStackTrace(); // Replace with appropriate error handling
            return Collections.emptyList();
        }
    }
}
