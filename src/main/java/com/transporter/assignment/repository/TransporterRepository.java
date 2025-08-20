package com.transporter.assignment.repository;

import com.transporter.assignment.model.Transporter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Transporter entity.
 * Provides data access operations for transporters.
 */
@Repository
public interface TransporterRepository extends JpaRepository<Transporter, Long> {

    /**
     * Finds a transporter by name.
     *
     * @param name the transporter name
     * @return the transporter if found, empty otherwise
     */
    Optional<Transporter> findByName(String name);

    /**
     * Finds all transporters that have at least one quote.
     *
     * @return list of transporters with quotes
     */
    @Query("SELECT DISTINCT t FROM Transporter t JOIN t.laneQuotes")
    List<Transporter> findTransportersWithQuotes();

    /**
     * Finds all transporters that have quotes for a specific lane.
     *
     * @param laneId the lane ID
     * @return list of transporters that can service the lane
     */
    @Query("SELECT DISTINCT t FROM Transporter t JOIN t.laneQuotes lq WHERE lq.lane.id = :laneId")
    List<Transporter> findTransportersForLane(@Param("laneId") Long laneId);

    /**
     * Finds all transporters that have quotes for any of the specified lanes.
     *
     * @param laneIds the list of lane IDs
     * @return list of transporters that can service at least one of the lanes
     */
    @Query("SELECT DISTINCT t FROM Transporter t JOIN t.laneQuotes lq WHERE lq.lane.id IN :laneIds")
    List<Transporter> findTransportersWithQuotesForLanes(@Param("laneIds") List<Long> laneIds);

    /**
     * Finds transporters that can service all specified lanes.
     *
     * @param laneIds the list of lane IDs
     * @return list of transporters that can service all specified lanes
     */
    @Query("SELECT t FROM Transporter t WHERE " +
           "(SELECT COUNT(DISTINCT lq.lane.id) FROM LaneQuote lq WHERE lq.transporter = t AND lq.lane.id IN :laneIds) = :laneCount")
    List<Transporter> findTransportersForAllLanes(@Param("laneIds") List<Long> laneIds, @Param("laneCount") long laneCount);

    /**
     * Counts the number of transporters with quotes.
     *
     * @return count of transporters with quotes
     */
    @Query("SELECT COUNT(DISTINCT t) FROM Transporter t JOIN t.laneQuotes")
    long countTransportersWithQuotes();

    /**
     * Finds transporters ordered by the number of lanes they can service.
     *
     * @return list of transporters ordered by lane count (descending)
     */
    @Query("SELECT t FROM Transporter t LEFT JOIN t.laneQuotes lq " +
           "GROUP BY t ORDER BY COUNT(lq) DESC")
    List<Transporter> findTransportersOrderedByLaneCount();
}
