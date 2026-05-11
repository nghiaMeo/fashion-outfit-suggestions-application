package com.example.wardrobeservices.controller;

import com.example.wardrobeservices.dto.request.ItemRequest;
import com.example.wardrobeservices.dto.response.ApiResponse;
import com.example.wardrobeservices.dto.response.ItemResponse;
import com.example.wardrobeservices.dto.response.PageResponse;
import com.example.wardrobeservices.dto.response.WardrobeStatisticsResponse;
import com.example.wardrobeservices.service.CloudinaryService;
import com.example.wardrobeservices.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final CloudinaryService cloudinaryService;

    /**
     * Creates a new item using the provided item data and image.
     *
     * @param itemRequest the item data to create; the uploaded file's URL will be set on this request before creation
     * @param file the image file to upload and associate with the new item
     * @return an ApiResponse containing the created ItemResponse
     */
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ItemResponse> addItem(@RequestPart("data") @Valid ItemRequest itemRequest,
                                             @RequestPart("file") MultipartFile file) {

        var imageUrl = cloudinaryService.upload(file);
        itemRequest.setImageUrl(imageUrl);

        return ApiResponse.<ItemResponse>builder()
                .result(itemService.addItem(itemRequest))
                .build();
    }

    /**
     * Update an existing item identified by its UUID.
     *
     * Applies the fields from the provided request to the item and returns the updated representation.
     *
     * @param id the UUID of the item to update
     * @param itemRequest the new item data to apply
     * @return an ApiResponse containing the updated ItemResponse
     */
    @PutMapping("/{id}")
    public ApiResponse<ItemResponse> updateItem(@PathVariable UUID id,
                                                @RequestBody @Valid ItemRequest itemRequest) {

        return ApiResponse.<ItemResponse>builder()
                .result(itemService.updateItem(id, itemRequest))
                .build();
    }

    /**
     * Retrieve all items.
     *
     * @return a list of ItemResponse DTOs representing all items
     */
    @GetMapping("/all-items")
    public ApiResponse<List<ItemResponse>> getAllItems() {
        return ApiResponse.<List<ItemResponse>>builder()
                .result(itemService.getAllItems()).build();
    }

    /**
     * Delete the item identified by the given UUID.
     *
     * @param id the UUID of the item to delete
     * @return a message describing the outcome of the deletion
     */
    @DeleteMapping("/delete-item/{id}")
    public ApiResponse<String> deleteItem(@PathVariable UUID id) {
        return ApiResponse.<String>builder()
                .result(itemService.deleteItem(id))
                .build();
    }

    /**
     * Searches items using optional filters and returns a paginated page of matching items.
     *
     * @param name  optional name filter to match item names
     * @param type  optional type filter to match item types
     * @param color optional color filter to match item colors
     * @param page  page number to return, starting at 1
     * @param size  number of items per page
     * @return      a PageResponse<ItemResponse> containing matching items and pagination metadata
     */
    @GetMapping("/search-items")
    public ApiResponse<PageResponse<ItemResponse>> searchItems(@RequestParam(required = false) String name,
                                                               @RequestParam(required = false) String type,
                                                               @RequestParam(required = false) String color,
                                                               @RequestParam(defaultValue = "1") int page,
                                                               @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<ItemResponse>>builder()
                .result(itemService.searchItems(name, type, color, page, size))
                .build();
    }

    /**
     * Retrieve aggregated wardrobe statistics.
     *
     * @return an ApiResponse whose result is a WardrobeStatisticsResponse containing aggregated statistics for the wardrobe
     */
    @GetMapping("/statistics")
    public ApiResponse<WardrobeStatisticsResponse> getStatistics() {
        return ApiResponse.<WardrobeStatisticsResponse>builder()
                .result(itemService.getWardrobeStatistics())
                .build();
    }

    /**
     * Restores a soft-deleted item identified by its UUID and returns the restored item.
     *
     * @param id the UUID of the item to restore
     * @return the restored ItemResponse
     */
    @PostMapping("/restore/{id}")
    public ApiResponse<ItemResponse> restoreItem(@PathVariable UUID id) {
        return ApiResponse.<ItemResponse>builder()
                .result(itemService.restoreItem(id))
                .build();
    }

    /**
     * Retrieves items that are currently in the trash.
     *
     * @return an ApiResponse containing a list of ItemResponse objects representing trashed items
     */
    @GetMapping("/trash")
    public ApiResponse<List<ItemResponse>> getTrashItems() {
        return ApiResponse.<List<ItemResponse>>builder()
                .result(itemService.getTrashItems())
                .build();
    }

    /**
     * Permanently deletes the item identified by the given UUID.
     *
     * @param id UUID of the item to delete permanently
     * @return confirmation message "Item has been deleted"
     */
    @DeleteMapping("/hard-delete/{id}")
    public ApiResponse<String> deleteHardItem(@PathVariable UUID id) {
        itemService.deleteItem(id);
        return ApiResponse.<String>builder()
                .result("Item has been deleted")
                .build();
    }
}
