package com.transporter.assignment.service.impl;

import com.transporter.assignment.dto.*;
import com.transporter.assignment.model.Lane;
import com.transporter.assignment.model.LaneQuote;
import com.transporter.assignment.model.Transporter;
import com.transporter.assignment.repository.LaneQuoteRepository;
import com.transporter.assignment.repository.LaneRepository;
import com.transporter.assignment.repository.TransporterRepository;
import com.transporter.assignment.service.InputDataException;
import com.transporter.assignment.service.InputDataService;
import com.transporter.assignment.service.InputDataStatistics;
import com.transporter.assignment.util.ResponseUtil;
import com.transporter.assignment.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of InputDataService
 */
@Service
@Transactional
public class InputDataServiceImpl implements InputDataService {

    // repository stuff
    private final LaneRepository laneRepository;
    private final TransporterRepository transporterRepository;
    private final LaneQuoteRepository laneQuoteRepository;

    @Autowired
    public InputDataServiceImpl(LaneRepository laneRepository,
                               TransporterRepository transporterRepository,
                               LaneQuoteRepository laneQuoteRepository) {
        this.laneRepository = laneRepository;
        this.transporterRepository = transporterRepository;
        this.laneQuoteRepository = laneQuoteRepository;
    }

    @Override
    public InputDataResponse saveInputData(InputDataRequest request) throws InputDataException {
        try {
            // check if request is valid first
            InputDataResponse validation = validateInputData(request);
            if (!"success".equals(validation.getStatus())) {
                return validation;
            }

            // delete old data
            clearInputData();

            // Save lanes
            List<Lane> lanes = new ArrayList<>();
            for (LaneDto laneDto : request.getLanes()) {
                Lane lane = new Lane(laneDto.getId(), laneDto.getOrigin(), laneDto.getDestination());
                lanes.add(lane);
            }
            laneRepository.saveAll(lanes);

            // Save transporters and quotes
            for (TransporterDto transporterDto : request.getTransporters()) {
                Transporter transporter = new Transporter(transporterDto.getId(), transporterDto.getName());
                transporterRepository.save(transporter);

                // Save quotes for this transporter
                for (LaneQuoteDto quoteDto : transporterDto.getLaneQuotes()) {
                    Lane lane = laneRepository.findById(quoteDto.getLaneId())
                            .orElseThrow(() -> new InputDataException("Lane not found: " + quoteDto.getLaneId()));
                    
                    LaneQuote quote = new LaneQuote(transporter, lane, quoteDto.getQuote());
                    laneQuoteRepository.save(quote);
                }
            }

            return ResponseUtil.inputDataSuccess("Input data saved successfully. " +
                    "Lanes: " + request.getLanes().size() + ", " +
                    "Transporters: " + request.getTransporters().size());

        } catch (Exception e) {
            throw new InputDataException("Failed to save input data: " + e.getMessage(), e);
        }
    }

    @Override
    public InputDataResponse clearInputData() {
        try {
            laneQuoteRepository.deleteAll();
            transporterRepository.deleteAll();
            laneRepository.deleteAll();
            return ResponseUtil.inputDataSuccess("All input data cleared successfully.");
        } catch (Exception e) {
            return ResponseUtil.inputDataError("Failed to clear input data: " + e.getMessage());
        }
    }

    @Override
    public InputDataResponse validateInputData(InputDataRequest request) {
        List<String> errors = ValidationUtil.validateInputData(request);

        if (!errors.isEmpty()) {
            return ResponseUtil.inputDataError("Validation failed: " + String.join("; ", errors));
        }

        return ResponseUtil.inputDataSuccess("Input data validation passed.");
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasInputData() {
        return laneRepository.count() > 0 && transporterRepository.count() > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public InputDataStatistics getInputDataStatistics() {
        long laneCount = laneRepository.count();
        long transporterCount = transporterRepository.count();
        long quoteCount = laneQuoteRepository.count();
        long lanesWithQuotes = laneRepository.countLanesWithQuotes();
        long transportersWithQuotes = transporterRepository.countTransportersWithQuotes();

        return new InputDataStatistics(laneCount, transporterCount, quoteCount, 
                                     lanesWithQuotes, transportersWithQuotes);
    }
}
