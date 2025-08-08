package org.example;

import org.apache.commons.cli.*;
import org.example.exceptions.TicketsNotFoundException;
import org.example.schema.Ticket;
import org.example.schema.TicketsData;
import org.example.services.TicketsAnalyzer;

import java.io.IOException;
import java.time.Duration;
import java.util.*;


public class Cli {
    public static void main(String[] args) throws ParseException, IOException {
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        Option ticketsFileOption = Option.builder("t")
                .argName("tickets")
                .hasArg()
                .required()
                .desc("Path to the JSON file containing the tickets.")
                .build();

        Option originOption = Option.builder("o")
                .argName("origin")
                .hasArg()
                .required()
                .desc("Origin airport. Case sensitive")
                .build();

        Option destinationOption = Option.builder("d")
                .argName("destination")
                .hasArg()
                .required()
                .desc("Destination airport. Case sensitive")
                .build();

        options.addOption(ticketsFileOption);
        options.addOption(originOption);
        options.addOption(destinationOption);

        CommandLine line = parser.parse(options, args);

        String ticketsFilePath = line.getOptionValue("t");
        String origin = line.getOptionValue("o");
        String destination = line.getOptionValue("d");

        TicketsAnalyzer service = new TicketsAnalyzer();

        TicketsData data = service.readTicketsData(ticketsFilePath);
        List<Ticket> filteredTickets = service.filterTickets(data, origin, destination);

        if (filteredTickets.isEmpty()) {
            throw new TicketsNotFoundException("No tickets found for origin " + origin + " and destination " + destination);
        }

        Map<String, Optional<Duration>> minFlightTimes = service.calculateMinFlightTimes(filteredTickets);
        double avgMedianDifference = service.calculateAvgMedianDifference(filteredTickets);

        System.out.println("Min Flight Times:");

        minFlightTimes.forEach((carrier, minDurationOpt) ->
                minDurationOpt.ifPresent(duration ->
                        System.out.printf("Carrier %s: %s hours, %s minutes\n",
                                carrier, duration.toHours(), duration.toMinutes() % 60)));

        System.out.printf("Average vs. Median Price Difference: %.2f\n", avgMedianDifference);
    }
}