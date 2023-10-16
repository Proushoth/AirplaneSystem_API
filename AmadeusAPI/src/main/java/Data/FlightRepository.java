package Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FlightRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void saveFlightData(String type, String origin, String destination, String departureDate, String returnDate, double price) {
        String sql = "INSERT INTO flights (type, origin, destination, departure_date, return_date, price) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, type, origin, destination, departureDate, returnDate, price);
    }
}