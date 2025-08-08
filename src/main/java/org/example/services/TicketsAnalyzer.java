package org.example.services;

import com.google.gson.Gson;
import org.example.schema.Ticket;
import org.example.schema.TicketsData;

import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class TicketsAnalyzer {
    public TicketsData readTicketsData(String filePath) throws IOException {
        try (FileReader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            return gson.fromJson(reader, TicketsData.class);
        }
    }

    private Duration calculateFlightDuration(Ticket ticket) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy H:mm");

        // Handle cases where time might be a single digit hour
        String departureTime = ticket.departure_time;
        if(departureTime.indexOf(':') == 1) {
            departureTime = "0" + departureTime;
        }

        String arrivalTime = ticket.arrival_time;
        if(arrivalTime.indexOf(':') == 1) {
            arrivalTime = "0" + arrivalTime;
        }

        LocalDateTime departure = LocalDateTime.parse(ticket.departure_date + " " + departureTime, formatter);
        LocalDateTime arrival = LocalDateTime.parse(ticket.arrival_date + " " + arrivalTime, formatter);

        return Duration.between(departure, arrival);
    }

    public List<Ticket> filterTickets(TicketsData ticketsData, String origin, String destination) {
        return ticketsData.tickets.stream()
                .filter(ticket -> origin.equals(ticket.origin_name) && destination.equals(ticket.destination_name))
                .toList();
    }

    public Map<String, Optional<Duration>> calculateMinFlightTimes(List<Ticket> tickets) {
        return tickets.stream()
                .collect(Collectors.groupingBy(ticket -> ticket.carrier,
                        Collectors.mapping(this::calculateFlightDuration,
                                Collectors.minBy(Comparator.naturalOrder()))));
    }

    public double calculateAvgMedianDifference(List<Ticket> tickets) {
        List<Integer> prices = tickets.stream()
                .map(ticket -> ticket.price)
                .sorted()
                .toList();

        double averagePrice = prices.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);

        double medianPrice;
        int size = prices.size();
        if (size % 2 == 1) {
            // If the size is odd, the median is the middle element.
            medianPrice = prices.get(size / 2);
        } else {
            // If the size is even, the median is the average of the two middle elements.
            medianPrice = (prices.get(size / 2 - 1) + prices.get(size / 2)) / 2.0;
        }

        return averagePrice - medianPrice;
    }
}
