package com.example.wardrobeservices.service;

import com.example.wardrobeservices.dto.request.ItemRequest;
import com.example.wardrobeservices.dto.response.ItemResponse;
import com.example.wardrobeservices.dto.response.PageResponse;
import com.example.wardrobeservices.dto.response.WardrobeStatisticsResponse;

import java.util.List;
import java.util.UUID;

public interface ItemService {

    /**
 * Create a new wardrobe item from the provided request data.
 *
 * @param itemRequest details of the item to create
 * @return the created ItemResponse containing the persisted item data (including its assigned identifier)
 */
ItemResponse addItem(ItemRequest itemRequest);

    /**
 * Update an existing wardrobe item identified by the given id using values from the provided request.
 *
 * @param id the UUID of the item to update
 * @param itemRequest the new values for the item
 * @return the updated item representation
 */
ItemResponse updateItem(UUID id, ItemRequest itemRequest);

    /**
 * Deletes the item with the given UUID.
 *
 * @param id the UUID of the item to delete
 * @return a status message describing the outcome of the deletion
 */
String deleteItem(UUID id);

    /**
 * Retrieve all wardrobe items.
 *
 * @return a list of ItemResponse representing every item
 */
List<ItemResponse> getAllItems();

    /**
 * Searches items using optional name, type, and color filters and returns a paginated response.
 *
 * Any filter parameter may be null or empty to disable that filter. Pagination is controlled
 * by the `page` and `size` parameters.
 *
 * @param name  optional substring to match item names
 * @param type  optional item type to filter by
 * @param color optional item color to filter by
 * @param page  page index for pagination
 * @param size  number of items per page
 * @return      a PageResponse of ItemResponse objects matching the provided filters for the requested page
 */
PageResponse<ItemResponse> searchItems(String name, String type, String color, int page, int size);

    /**
 * Retrieve aggregated statistics for the wardrobe.
 *
 * <p>Provides summary metrics such as total item count, counts per type, color distribution,
 * and counts of active versus trashed items.</p>
 *
 * @return a {@link WardrobeStatisticsResponse} containing aggregated wardrobe metrics
 */
WardrobeStatisticsResponse getWardrobeStatistics();

    /**
 * Restore a previously deleted item identified by its UUID.
 *
 * @param id the UUID of the item to restore
 * @return the restored ItemResponse
 */
ItemResponse restoreItem(UUID id);

    /**
 * Retrieves all wardrobe items currently in the trash (soft-deleted).
 *
 * @return a list of ItemResponse representing items that are marked as deleted/in the trash
 */
List<ItemResponse> getTrashItems();

    /**
 * Permanently removes the item identified by the given id from the system.
 *
 * This operation is irreversible and deletes the item from persisted storage.
 *
 * @param id the UUID of the item to permanently delete
 */
void hardDeleteItem(UUID id);


}
