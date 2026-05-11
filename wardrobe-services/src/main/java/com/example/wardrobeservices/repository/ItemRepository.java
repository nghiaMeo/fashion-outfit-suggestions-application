package com.example.wardrobeservices.repository;

import com.example.wardrobeservices.entity.Item;
import com.example.wardrobeservices.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ItemRepository extends JpaRepository<Item, UUID> {

    /**
 * Retrieve all non-deleted items belonging to the given user.
 *
 * @param user the owner whose non-deleted items to retrieve
 * @return a list of the user's items where `isDeleted` is `false`; empty if none found
 */
List<Item> findByUserAndIsDeletedFalse(User user);

    /**
 * Retrieve an item by its identifier only if it is not marked as deleted.
 *
 * @param id the UUID of the item to find
 * @return an Optional containing the matching Item if present and not deleted, otherwise an empty Optional
 */
Optional<Item> findByIdAndIsDeletedFalse(UUID id);

    /**
                            * Searches non-deleted items belonging to the given user and returns results as a page.
                            *
                            * Name is matched case-insensitively as a substring; color is matched exactly.
                            * If `name` or `color` is `null`, that filter is ignored. The `type` parameter is present but not used by the query.
                            *
                            * @param user     the owner of the items to search
                            * @param name     optional substring to match against item names (case-insensitive)
                            * @param type     optional type parameter (currently ignored by the query)
                            * @param color    optional exact color to filter by
                            * @param pageable paging and sorting information
                            * @return         a page of items for the user that satisfy the provided filters
                            */
                           @Query("SELECT i FROM Item i WHERE i.user= :user AND i.isDeleted = false " +
            "AND (:name IS NULL OR LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%')))" +
            "AND (:color IS NULL OR i.color = :color)")
    Page<Item> searchItems(@Param("user") User user,
                           @Param("name") String name,
                           @Param("type") String type,
                           @Param("color") String color,
                           Pageable pageable);

    /**
     * Retrieves counts of items grouped by item type for the given user, excluding deleted items.
     *
     * @param user the owner whose items will be grouped and counted
     * @return a list of Object arrays where each array contains two elements: the item type at index 0 and the count (Long) at index 1
     */
    @Query("SELECT i.type, COUNT(i) FROM Item i WHERE i.user = :user AND i.isDeleted = false GROUP BY i.type")
    List<Objects[]> countItemsGroupByType(@Param("user") User user);

    /**
 * Count items belonging to the specified user that are not marked as deleted.
 *
 * @param user the owner whose non-deleted items will be counted
 * @return the number of items for the given user where `isDeleted` is false
 */
long countByUserAndIsDeletedFalse(User user);

    /**
 * Finds a deleted Item by its UUID.
 *
 * @param id the UUID of the item to find
 * @return an Optional containing the Item if it exists and is marked deleted, otherwise an empty Optional
 */
Optional<Item> findByIdAndIsDeletedTrue(UUID id);

    /**
 * Fetches all items belonging to the specified user that are marked as deleted.
 *
 * @param user the owner whose deleted items should be retrieved
 * @return a list of the user's items with `isDeleted = true`
 */
List<Item> findByUserAndIsDeletedTrue(User user);

}
