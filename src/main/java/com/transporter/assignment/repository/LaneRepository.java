package com.transporter.assignment.repository;

import com.transporter.assignment.model.Lane;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Lane entity.
 * Provides data access operations for lanes.
 */
@Repository
public interface LaneRepository extends JpaRepository<Lane, Long> {

    /**
     * Finds a lane by origin and destination.
     *
     * @param origin      the origin city
     * @param destination the destination city
     * @return the lane if found, empty otherwise
     */
    Optional<Lane> findByOriginAndDestination(String origin, String destination);

    /**
     * Finds all lanes with the specified origin.
     *
     * @param origin the origin city
     * @return list of lanes from the origin
     */
    List<Lane> findByOrigin(String origin);

    /**
     * Finds all lanes with the specified destination.
     *
     * @param destination the destination city
     * @return list of lanes to the destination
     */
    List<Lane> findByDestination(String destination);

    /**
     * Finds all lanes that have at least one quote from any transporter.
     *
     * @return list of lanes with quotes
     */
    @Query("SELECT DISTINCT l FROM Lane l JOIN l.laneQuotes")
    List<Lane> findLanesWithQuotes();

    /**
     * Finds all lanes that do not have any quotes.
     *
     * @return list of lanes without quotes
     */
    @Query("SELECT l FROM Lane l WHERE l.id NOT IN (SELECT DISTINCT lq.lane.id FROM LaneQuote lq)")
    List<Lane> findLanesWithoutQuotes();

    /**
     * Counts the number of lanes with quotes.
     *
     * @return count of lanes with quotes
     */
    @Query("SELECT COUNT(DISTINCT l) FROM Lane l JOIN l.laneQuotes")
    long countLanesWithQuotes();
}
