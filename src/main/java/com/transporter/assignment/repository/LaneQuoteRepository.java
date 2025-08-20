package com.transporter.assignment.repository;

import com.transporter.assignment.model.Lane;
import com.transporter.assignment.model.LaneQuote;
import com.transporter.assignment.model.Transporter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for LaneQuote entity.
 * Provides data access operations for lane quotes.
 */
@Repository
public interface LaneQuoteRepository extends JpaRepository<LaneQuote, Long> {

    /**
     * Finds a quote by transporter and lane.
     *
     * @param transporter the transporter
     * @param lane        the lane
     * @return the quote if found, empty otherwise
     */
    Optional<LaneQuote> findByTransporterAndLane(Transporter transporter, Lane lane);

    /**
     * Finds all quotes for a specific transporter.
     *
     * @param transporterId the transporter ID
     * @return list of quotes for the transporter
     */
    @Query("SELECT lq FROM LaneQuote lq WHERE lq.transporter.id = :transporterId")
    List<LaneQuote> findByTransporterId(@Param("transporterId") Long transporterId);

    /**
     * Finds all quotes for a specific lane.
     *
     * @param laneId the lane ID
     * @return list of quotes for the lane
     */
    @Query("SELECT lq FROM LaneQuote lq WHERE lq.lane.id = :laneId")
    List<LaneQuote> findByLaneId(@Param("laneId") Long laneId);

    /**
     * Finds the lowest quote for a specific lane.
     *
     * @param laneId the lane ID
     * @return the lowest quote for the lane, empty if no quotes exist
     */
    @Query("SELECT lq FROM LaneQuote lq WHERE lq.lane.id = :laneId ORDER BY lq.quote ASC LIMIT 1")
    Optional<LaneQuote> findLowestQuoteForLane(@Param("laneId") Long laneId);

    /**
     * Finds all quotes for the specified lanes.
     *
     * @param laneIds the list of lane IDs
     * @return list of quotes for the specified lanes
     */
    @Query("SELECT lq FROM LaneQuote lq WHERE lq.lane.id IN :laneIds")
    List<LaneQuote> findQuotesForLanes(@Param("laneIds") List<Long> laneIds);

    /**
     * Finds all quotes from the specified transporters.
     *
     * @param transporterIds the list of transporter IDs
     * @return list of quotes from the specified transporters
     */
    @Query("SELECT lq FROM LaneQuote lq WHERE lq.transporter.id IN :transporterIds")
    List<LaneQuote> findQuotesForTransporters(@Param("transporterIds") List<Long> transporterIds);

    /**
     * Finds quotes for specific transporters and lanes combination.
     *
     * @param transporterIds the list of transporter IDs
     * @param laneIds        the list of lane IDs
     * @return list of quotes matching the criteria
     */
    @Query("SELECT lq FROM LaneQuote lq WHERE lq.transporter.id IN :transporterIds AND lq.lane.id IN :laneIds")
    List<LaneQuote> findQuotesForTransportersAndLanes(@Param("transporterIds") List<Long> transporterIds,
                                                      @Param("laneIds") List<Long> laneIds);

    /**
     * Deletes a quote by transporter and lane.
     *
     * @param transporter the transporter
     * @param lane        the lane
     */
    @Modifying
    @Transactional
    void deleteByTransporterAndLane(Transporter transporter, Lane lane);

    /**
     * Counts the number of quotes for a specific lane.
     *
     * @param laneId the lane ID
     * @return count of quotes for the lane
     */
    @Query("SELECT COUNT(lq) FROM LaneQuote lq WHERE lq.lane.id = :laneId")
    long countQuotesForLane(@Param("laneId") Long laneId);

    /**
     * Counts the number of quotes for a specific transporter.
     *
     * @param transporterId the transporter ID
     * @return count of quotes for the transporter
     */
    @Query("SELECT COUNT(lq) FROM LaneQuote lq WHERE lq.transporter.id = :transporterId")
    long countQuotesForTransporter(@Param("transporterId") Long transporterId);

    /**
     * Finds all quotes ordered by quote amount (ascending).
     *
     * @return list of quotes ordered by price
     */
    @Query("SELECT lq FROM LaneQuote lq ORDER BY lq.quote ASC")
    List<LaneQuote> findAllOrderedByQuote();

    /**
     * Finds quotes for a lane ordered by quote amount (ascending).
     *
     * @param laneId the lane ID
     * @return list of quotes for the lane ordered by price
     */
    @Query("SELECT lq FROM LaneQuote lq WHERE lq.lane.id = :laneId ORDER BY lq.quote ASC")
    List<LaneQuote> findByLaneIdOrderedByQuote(@Param("laneId") Long laneId);
}
