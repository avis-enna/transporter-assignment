package com.transporter.assignment.service.impl;

import com.transporter.assignment.dto.*;
import com.transporter.assignment.model.Lane;
import com.transporter.assignment.model.LaneQuote;
import com.transporter.assignment.model.Transporter;
import com.transporter.assignment.repository.LaneQuoteRepository;
import com.transporter.assignment.repository.LaneRepository;
import com.transporter.assignment.repository.TransporterRepository;
import com.transporter.assignment.service.InputDataException;
import com.transporter.assignment.service.InputDataStatistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * Unit tests for InputDataServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class InputDataServiceImplTest {

    @Mock
    private LaneRepository laneRepository;

    @Mock
    private TransporterRepository transporterRepository;

    @Mock
    private LaneQuoteRepository laneQuoteRepository;

    @InjectMocks
    private InputDataServiceImpl inputDataService;

    private InputDataRequest validRequest;

    @BeforeEach
    void setUp() {
        // Create valid test data
        List<LaneDto> lanes = List.of(
                new LaneDto(1L, "Mumbai", "Delhi"),
                new LaneDto(2L, "Chennai", "Bangalore")
        );

        List<TransporterDto> transporters = List.of(
                new TransporterDto(1L, "Transporter T1", List.of(
                        new LaneQuoteDto(1L, new BigDecimal("5000")),
                        new LaneQuoteDto(2L, new BigDecimal("7000"))
                )),
                new TransporterDto(2L, "Transporter T2", List.of(
                        new LaneQuoteDto(1L, new BigDecimal("5500")),
                        new LaneQuoteDto(2L, new BigDecimal("6500"))
                ))
        );

        validRequest = new InputDataRequest(lanes, transporters);
    }

    @Test
    void shouldSaveValidInputData() throws InputDataException {
        // Given
        when(laneRepository.saveAll(anyList())).thenReturn(List.of());
        when(transporterRepository.save(any(Transporter.class))).thenReturn(new Transporter());
        when(laneRepository.findById(1L)).thenReturn(Optional.of(new Lane(1L, "Mumbai", "Delhi")));
        when(laneRepository.findById(2L)).thenReturn(Optional.of(new Lane(2L, "Chennai", "Bangalore")));
        when(laneQuoteRepository.save(any(LaneQuote.class))).thenReturn(new LaneQuote());

        // When
        InputDataResponse response = inputDataService.saveInputData(validRequest);

        // Then
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getMessage()).contains("Input data saved successfully");
        
        verify(laneQuoteRepository).deleteAll();
        verify(transporterRepository).deleteAll();
        verify(laneRepository).deleteAll();
        verify(laneRepository).saveAll(anyList());
        verify(transporterRepository, times(2)).save(any(Transporter.class));
        verify(laneQuoteRepository, times(4)).save(any(LaneQuote.class));
    }

    @Test
    void shouldValidateInputDataSuccessfully() {
        // When
        InputDataResponse response = inputDataService.validateInputData(validRequest);

        // Then
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getMessage()).contains("validation passed");
    }

    @Test
    void shouldDetectDuplicateLaneIds() {
        // Given
        List<LaneDto> duplicateLanes = List.of(
                new LaneDto(1L, "Mumbai", "Delhi"),
                new LaneDto(1L, "Chennai", "Bangalore") // Duplicate ID
        );
        InputDataRequest invalidRequest = new InputDataRequest(duplicateLanes, validRequest.getTransporters());

        // When
        InputDataResponse response = inputDataService.validateInputData(invalidRequest);

        // Then
        assertThat(response.getStatus()).isEqualTo("error");
        assertThat(response.getMessage()).contains("Duplicate lane ID: 1");
    }

    @Test
    void shouldDetectDuplicateTransporterIds() {
        // Given
        List<TransporterDto> duplicateTransporters = List.of(
                new TransporterDto(1L, "T1", List.of(new LaneQuoteDto(1L, new BigDecimal("5000")))),
                new TransporterDto(1L, "T2", List.of(new LaneQuoteDto(2L, new BigDecimal("6000")))) // Duplicate ID
        );
        InputDataRequest invalidRequest = new InputDataRequest(validRequest.getLanes(), duplicateTransporters);

        // When
        InputDataResponse response = inputDataService.validateInputData(invalidRequest);

        // Then
        assertThat(response.getStatus()).isEqualTo("error");
        assertThat(response.getMessage()).contains("Duplicate transporter ID: 1");
    }

    @Test
    void shouldDetectQuotesForNonExistentLanes() {
        // Given - Create transporters with quotes for all existing lanes plus a non-existent lane
        List<TransporterDto> invalidTransporters = List.of(
                new TransporterDto(1L, "T1", List.of(
                        new LaneQuoteDto(1L, new BigDecimal("5000")),
                        new LaneQuoteDto(2L, new BigDecimal("6000")), // Cover all existing lanes
                        new LaneQuoteDto(999L, new BigDecimal("7000")) // Non-existent lane
                ))
        );
        InputDataRequest invalidRequest = new InputDataRequest(validRequest.getLanes(), invalidTransporters);

        // When
        InputDataResponse response = inputDataService.validateInputData(invalidRequest);

        // Then
        assertThat(response.getStatus()).isEqualTo("error");
        assertThat(response.getMessage()).contains("non-existent lane: 999");
    }

    @Test
    void shouldDetectLanesWithoutQuotes() {
        // Given
        List<LaneDto> lanes = List.of(
                new LaneDto(1L, "Mumbai", "Delhi"),
                new LaneDto(2L, "Chennai", "Bangalore"),
                new LaneDto(3L, "Pune", "Hyderabad") // No quotes for this lane
        );
        InputDataRequest invalidRequest = new InputDataRequest(lanes, validRequest.getTransporters());

        // When
        InputDataResponse response = inputDataService.validateInputData(invalidRequest);

        // Then
        assertThat(response.getStatus()).isEqualTo("error");
        assertThat(response.getMessage()).contains("Lane 3 has no quotes");
    }

    @Test
    void shouldDetectDuplicateQuotes() {
        // Given
        List<TransporterDto> invalidTransporters = List.of(
                new TransporterDto(1L, "T1", List.of(
                        new LaneQuoteDto(1L, new BigDecimal("5000")),
                        new LaneQuoteDto(1L, new BigDecimal("5500")) // Duplicate quote for same lane
                ))
        );
        InputDataRequest invalidRequest = new InputDataRequest(validRequest.getLanes(), invalidTransporters);

        // When
        InputDataResponse response = inputDataService.validateInputData(invalidRequest);

        // Then
        assertThat(response.getStatus()).isEqualTo("error");
        assertThat(response.getMessage()).contains("Duplicate quote");
    }

    @Test
    void shouldClearInputDataSuccessfully() {
        // When
        InputDataResponse response = inputDataService.clearInputData();

        // Then
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getMessage()).contains("cleared successfully");
        
        verify(laneQuoteRepository).deleteAll();
        verify(transporterRepository).deleteAll();
        verify(laneRepository).deleteAll();
    }

    @Test
    void shouldHandleClearDataError() {
        // Given
        doThrow(new RuntimeException("Database error")).when(laneQuoteRepository).deleteAll();

        // When
        InputDataResponse response = inputDataService.clearInputData();

        // Then
        assertThat(response.getStatus()).isEqualTo("error");
        assertThat(response.getMessage()).contains("Failed to clear input data");
    }

    @Test
    void shouldCheckIfInputDataExists() {
        // Given
        when(laneRepository.count()).thenReturn(5L);
        when(transporterRepository.count()).thenReturn(3L);

        // When
        boolean hasData = inputDataService.hasInputData();

        // Then
        assertThat(hasData).isTrue();
    }

    @Test
    void shouldReturnFalseWhenNoInputData() {
        // Given
        when(laneRepository.count()).thenReturn(0L);

        // When
        boolean hasData = inputDataService.hasInputData();

        // Then
        assertThat(hasData).isFalse();
    }

    @Test
    void shouldGetInputDataStatistics() {
        // Given
        when(laneRepository.count()).thenReturn(5L);
        when(transporterRepository.count()).thenReturn(3L);
        when(laneQuoteRepository.count()).thenReturn(12L);
        when(laneRepository.countLanesWithQuotes()).thenReturn(5L);
        when(transporterRepository.countTransportersWithQuotes()).thenReturn(3L);

        // When
        InputDataStatistics stats = inputDataService.getInputDataStatistics();

        // Then
        assertThat(stats.getLaneCount()).isEqualTo(5L);
        assertThat(stats.getTransporterCount()).isEqualTo(3L);
        assertThat(stats.getQuoteCount()).isEqualTo(12L);
        assertThat(stats.getLanesWithQuotes()).isEqualTo(5L);
        assertThat(stats.getTransportersWithQuotes()).isEqualTo(3L);
        assertThat(stats.getCoveragePercentage()).isEqualTo(100.0);
    }

    @Test
    void shouldThrowExceptionWhenSaveFails() {
        // Given
        when(laneRepository.saveAll(anyList())).thenThrow(new RuntimeException("Database error"));

        // When/Then
        assertThatThrownBy(() -> inputDataService.saveInputData(validRequest))
                .isInstanceOf(InputDataException.class)
                .hasMessageContaining("Failed to save input data");
    }

    @Test
    void shouldThrowExceptionWhenLaneNotFoundDuringSave() throws InputDataException {
        // Given
        when(laneRepository.saveAll(anyList())).thenReturn(List.of());
        when(transporterRepository.save(any(Transporter.class))).thenReturn(new Transporter());
        when(laneRepository.findById(1L)).thenReturn(Optional.empty()); // Lane not found

        // When/Then
        assertThatThrownBy(() -> inputDataService.saveInputData(validRequest))
                .isInstanceOf(InputDataException.class)
                .hasMessageContaining("Lane not found: 1");
    }
}
