package Controller;

import Data.Flight;
import Service.AmadeusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class AmadeusController {

    @Autowired
    private AmadeusService amadeusService;

    @GetMapping("/search-flights")
    public ResponseEntity<List<Flight>> searchFlights(
            @RequestParam("type") String type,
            @RequestParam("origin") String origin,
            @RequestParam("destination") String destination,
            @RequestParam("departureDate") String departureDate,
            @RequestParam("returnDate") String returnDate,
            @RequestParam("price") double price) {

        List<Flight> flights = amadeusService.searchFlights(type, origin, destination, departureDate, returnDate, price);
        return new ResponseEntity<>(flights, HttpStatus.OK);
    }
}
