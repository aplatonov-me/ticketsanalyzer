package com.example;

import org.example.TicketsAnalyzer;
import org.example.exceptions.TicketsNotFoundException;
import org.example.schema.Ticket;
import org.example.schema.TicketsData;
import org.junit.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TicketsAnalyzerTest {
    @Test
    public void doTest() throws IOException {
        TicketsAnalyzer service = new TicketsAnalyzer();

        String ticketsFilePath = "src/test/resources/tickets.json";
        String origin = "Владивосток";
        String destination = "Тель-Авив";

        TicketsData data = service.readTicketsData(ticketsFilePath);
        List<Ticket> filteredTickets = service.filterTickets(data, origin, destination);

        if (filteredTickets.isEmpty()) {
            throw new TicketsNotFoundException("No tickets found for origin " + origin + " and destination " + destination);
        }

        Map<String, Optional<Duration>> minFlightTimes = service.calculateMinFlightTimes(filteredTickets);

        /*
        Carrier TK: 5 hours, 50 minutes
        Carrier S7: 6 hours, 30 minutes
        Carrier SU: 6 hours, 0 minutes
        Carrier BA: 8 hours, 5 minutes
        * */

        assert minFlightTimes.size() == 4;

        assert minFlightTimes.get("TK").get().toMinutes() == 350;
        assert minFlightTimes.get("S7").get().toMinutes() == 390;
        assert minFlightTimes.get("SU").get().toMinutes() == 360;
        assert minFlightTimes.get("BA").get().toMinutes() == 485;

        double avgMedianDifference = service.calculateAvgMedianDifference(filteredTickets);

        assert avgMedianDifference == 460;
    }
}
