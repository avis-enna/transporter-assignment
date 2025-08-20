package com.transporter.assignment.testutil;

import com.transporter.assignment.dto.*;
import com.transporter.assignment.model.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Centralized test data builder to eliminate duplication across test classes.
 * Provides fluent API for creating test entities and DTOs.
 */
public class TestDataBuilder {
    
    private static final Random RANDOM = new Random(42); // Fixed seed for reproducible tests
    
    // Lane builders
    public static LaneBuilder lane() {
        return new LaneBuilder();
    }
    
    public static class LaneBuilder {
        private Long id = 1L;
        private String origin = "Mumbai";
        private String destination = "Delhi";
        
        public LaneBuilder withId(Long id) {
            this.id = id;
            return this;
        }
        
        public LaneBuilder withOrigin(String origin) {
            this.origin = origin;
            return this;
        }
        
        public LaneBuilder withDestination(String destination) {
            this.destination = destination;
            return this;
        }
        
        public Lane build() {
            return new Lane(id, origin, destination);
        }
        
        public LaneDto buildDto() {
            return new LaneDto(id, origin, destination);
        }
    }
    
    // Transporter builders
    public static TransporterBuilder transporter() {
        return new TransporterBuilder();
    }
    
    public static class TransporterBuilder {
        private Long id = 1L;
        private String name = "Transporter T1";
        private List<LaneQuote> quotes = new ArrayList<>();
        private List<LaneQuoteDto> quoteDtos = new ArrayList<>();
        
        public TransporterBuilder withId(Long id) {
            this.id = id;
            return this;
        }
        
        public TransporterBuilder withName(String name) {
            this.name = name;
            return this;
        }
        
        public TransporterBuilder withQuote(Long laneId, BigDecimal quote) {
            this.quoteDtos.add(new LaneQuoteDto(laneId, quote));
            return this;
        }
        
        public TransporterBuilder withQuotes(List<LaneQuoteDto> quotes) {
            this.quoteDtos.addAll(quotes);
            return this;
        }
        
        public Transporter build() {
            return new Transporter(id, name);
        }
        
        public TransporterDto buildDto() {
            return new TransporterDto(id, name, quoteDtos);
        }
    }
    
    // LaneQuote builders
    public static LaneQuoteBuilder laneQuote() {
        return new LaneQuoteBuilder();
    }
    
    public static class LaneQuoteBuilder {
        private Transporter transporter = transporter().build();
        private Lane lane = lane().build();
        private BigDecimal quote = new BigDecimal("5000");
        
        public LaneQuoteBuilder withTransporter(Transporter transporter) {
            this.transporter = transporter;
            return this;
        }
        
        public LaneQuoteBuilder withLane(Lane lane) {
            this.lane = lane;
            return this;
        }
        
        public LaneQuoteBuilder withQuote(BigDecimal quote) {
            this.quote = quote;
            return this;
        }
        
        public LaneQuote build() {
            return new LaneQuote(transporter, lane, quote);
        }
        
        public LaneQuoteDto buildDto() {
            return new LaneQuoteDto(lane.getId(), quote);
        }
    }
    
    // Assignment builders
    public static AssignmentBuilder assignment() {
        return new AssignmentBuilder();
    }
    
    public static class AssignmentBuilder {
        private Long laneId = 1L;
        private Long transporterId = 1L;
        private BigDecimal cost = new BigDecimal("5000");
        
        public AssignmentBuilder withLaneId(Long laneId) {
            this.laneId = laneId;
            return this;
        }
        
        public AssignmentBuilder withTransporterId(Long transporterId) {
            this.transporterId = transporterId;
            return this;
        }
        
        public AssignmentBuilder withCost(BigDecimal cost) {
            this.cost = cost;
            return this;
        }
        
        public Assignment build() {
            return new Assignment(laneId, transporterId, cost);
        }
        
        public AssignmentDto buildDto() {
            return new AssignmentDto(laneId, transporterId, cost);
        }
    }
    
    // Predefined test scenarios
    public static class Scenarios {
        
        public static InputDataRequest simpleScenario() {
            List<LaneDto> lanes = List.of(
                lane().withId(1L).withOrigin("Mumbai").withDestination("Delhi").buildDto(),
                lane().withId(2L).withOrigin("Chennai").withDestination("Bangalore").buildDto()
            );
            
            List<TransporterDto> transporters = List.of(
                transporter().withId(1L).withName("T1")
                    .withQuote(1L, new BigDecimal("5000"))
                    .withQuote(2L, new BigDecimal("7000"))
                    .buildDto(),
                transporter().withId(2L).withName("T2")
                    .withQuote(1L, new BigDecimal("5500"))
                    .withQuote(2L, new BigDecimal("6500"))
                    .buildDto()
            );
            
            return new InputDataRequest(lanes, transporters);
        }
        
        public static InputDataRequest assignmentTestCase1() {
            List<LaneDto> lanes = List.of(
                lane().withId(1L).withOrigin("Mumbai").withDestination("Delhi").buildDto(),
                lane().withId(2L).withOrigin("Delhi").withDestination("Bangalore").buildDto(),
                lane().withId(3L).withOrigin("Chennai").withDestination("Kolkata").buildDto(),
                lane().withId(4L).withOrigin("Pune").withDestination("Hyderabad").buildDto(),
                lane().withId(5L).withOrigin("Ahmedabad").withDestination("Jaipur").buildDto()
            );
            
            List<TransporterDto> transporters = List.of(
                transporter().withId(1L).withName("Transporter T1")
                    .withQuote(1L, new BigDecimal("20835"))
                    .withQuote(2L, new BigDecimal("10512"))
                    .withQuote(3L, new BigDecimal("22105"))
                    .withQuote(4L, new BigDecimal("42481"))
                    .withQuote(5L, new BigDecimal("19862"))
                    .buildDto(),
                transporter().withId(2L).withName("Transporter T2")
                    .withQuote(1L, new BigDecimal("48844"))
                    .withQuote(2L, new BigDecimal("31326"))
                    .withQuote(3L, new BigDecimal("18640"))
                    .withQuote(4L, new BigDecimal("45828"))
                    .withQuote(5L, new BigDecimal("18297"))
                    .buildDto(),
                transporter().withId(3L).withName("Transporter T3")
                    .withQuote(1L, new BigDecimal("39020"))
                    .withQuote(2L, new BigDecimal("20648"))
                    .withQuote(3L, new BigDecimal("31438"))
                    .withQuote(4L, new BigDecimal("36447"))
                    .withQuote(5L, new BigDecimal("12789"))
                    .buildDto(),
                transporter().withId(4L).withName("Transporter T4")
                    .withQuote(1L, new BigDecimal("14400"))
                    .withQuote(2L, new BigDecimal("44514"))
                    .withQuote(3L, new BigDecimal("14316"))
                    .withQuote(4L, new BigDecimal("10678"))
                    .withQuote(5L, new BigDecimal("13032"))
                    .buildDto(),
                transporter().withId(5L).withName("Transporter T5")
                    .withQuote(1L, new BigDecimal("11601"))
                    .withQuote(2L, new BigDecimal("19760"))
                    .withQuote(3L, new BigDecimal("40870"))
                    .withQuote(4L, new BigDecimal("20635"))
                    .withQuote(5L, new BigDecimal("26421"))
                    .buildDto(),
                transporter().withId(6L).withName("Transporter T6")
                    .withQuote(1L, new BigDecimal("35095"))
                    .withQuote(2L, new BigDecimal("12494"))
                    .withQuote(3L, new BigDecimal("17808"))
                    .withQuote(4L, new BigDecimal("36210"))
                    .withQuote(5L, new BigDecimal("39444"))
                    .buildDto(),
                transporter().withId(7L).withName("Transporter T7")
                    .withQuote(1L, new BigDecimal("26070"))
                    .withQuote(2L, new BigDecimal("41098"))
                    .withQuote(3L, new BigDecimal("20932"))
                    .withQuote(4L, new BigDecimal("16897"))
                    .withQuote(5L, new BigDecimal("27938"))
                    .buildDto()
            );
            
            return new InputDataRequest(lanes, transporters);
        }
        
        public static InputDataRequest infeasibleScenario() {
            List<LaneDto> lanes = List.of(
                lane().withId(1L).withOrigin("Chandigarh").withDestination("Shimla").buildDto(),
                lane().withId(2L).withOrigin("Agra").withDestination("Kanpur").buildDto(),
                lane().withId(3L).withOrigin("Varanasi").withDestination("Gorakhpur").buildDto(),
                lane().withId(4L).withOrigin("Amritsar").withDestination("Ludhiana").buildDto(),
                lane().withId(5L).withOrigin("Coimbatore").withDestination("Madurai").buildDto()
            );
            
            // Only provide quotes for lanes 1, 2, and 3 - leaving lanes 4 and 5 uncovered
            List<TransporterDto> transporters = List.of(
                transporter().withId(1L).withName("Transporter X")
                    .withQuote(1L, new BigDecimal("1000"))
                    .withQuote(2L, new BigDecimal("1500"))
                    .buildDto(),
                transporter().withId(2L).withName("Transporter Y")
                    .withQuote(2L, new BigDecimal("1600"))
                    .withQuote(3L, new BigDecimal("2000"))
                    .buildDto(),
                transporter().withId(3L).withName("Transporter Z")
                    .withQuote(1L, new BigDecimal("1200"))
                    .withQuote(3L, new BigDecimal("1800"))
                    .buildDto()
            );
            
            return new InputDataRequest(lanes, transporters);
        }
        
        public static InputDataRequest generateRandomScenario(int laneCount, int transporterCount, double coverageRatio) {
            List<LaneDto> lanes = new ArrayList<>();
            for (int i = 1; i <= laneCount; i++) {
                lanes.add(lane().withId((long) i)
                    .withOrigin("Origin" + i)
                    .withDestination("Destination" + i)
                    .buildDto());
            }
            
            List<TransporterDto> transporters = new ArrayList<>();
            for (int t = 1; t <= transporterCount; t++) {
                TransporterBuilder builder = transporter().withId((long) t).withName("Transporter T" + t);
                
                // Each transporter covers a percentage of lanes based on coverageRatio
                for (int i = 1; i <= laneCount; i++) {
                    if (RANDOM.nextDouble() < coverageRatio) {
                        int quote = 1000 + RANDOM.nextInt(49000);
                        builder.withQuote((long) i, new BigDecimal(quote));
                    }
                }
                
                transporters.add(builder.buildDto());
            }
            
            return new InputDataRequest(lanes, transporters);
        }
    }
}
