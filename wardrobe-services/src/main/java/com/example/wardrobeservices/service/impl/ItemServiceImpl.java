package com.example.wardrobeservices.service.impl;

import com.example.wardrobeservices.dto.request.ItemRequest;
import com.example.wardrobeservices.dto.response.ItemResponse;
import com.example.wardrobeservices.dto.response.PageResponse;
import com.example.wardrobeservices.dto.response.WardrobeStatisticsResponse;
import com.example.wardrobeservices.entity.Item;
import com.example.wardrobeservices.entity.User;
import com.example.wardrobeservices.exception.AppException;
import com.example.wardrobeservices.exception.ErrorCode;
import com.example.wardrobeservices.repository.ItemRepository;
import com.example.wardrobeservices.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    /**
     * Persists a new Item for the currently authenticated user and returns its response representation.
     *
     * @param itemRequest the DTO containing the item's properties (name, type, color, season, brand, occasion, imageUrl)
     * @return an ItemResponse representing the newly created item, including its generated identifiers and timestamps
     */
    @Override
    @Transactional
    public ItemResponse addItem(ItemRequest itemRequest) {
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder
                .getContext().getAuthentication()).getPrincipal();

        var item = Item.builder()
                .name(itemRequest.getName())
                .type(itemRequest.getType())
                .color(itemRequest.getColor())
                .season(itemRequest.getSeason())
                .brand(itemRequest.getBrand())
                .occasion(itemRequest.getOccasion())
                .imageUrl(itemRequest.getImageUrl())
                .user(currentUser)
                .aiItemId(UUID.randomUUID())
                .build();

        var savedItem = itemRepository.save(item);

        return mapToItemResponse(savedItem);
    }

    /**
     * Updates an existing item owned by the current authenticated user with values from the request.
     *
     * @param id the UUID of the item to update
     * @param itemRequest the new field values to apply to the item
     * @return an ItemResponse representing the updated item
     * @throws AppException with ErrorCode.ITEM_NOT_FOUND if no item exists with the given id
     * @throws AppException with ErrorCode.UNAUTHORIZED if the current user does not own the item
     */
    @Override
    public ItemResponse updateItem(UUID id, ItemRequest itemRequest) {
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder
                .getContext().getAuthentication()).getPrincipal();

        var item = itemRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_FOUND));

        if (!item.getUser().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        item.setName(itemRequest.getName());
        item.setType(itemRequest.getType());
        item.setColor(itemRequest.getColor());
        item.setSeason(itemRequest.getSeason());
        item.setBrand(itemRequest.getBrand());
        item.setOccasion(itemRequest.getOccasion());
        item.setImageUrl(itemRequest.getImageUrl());

        item.setUpdatedAt(Instant.now());

        var updatedItem = itemRepository.save(item);

        return mapToItemResponse(updatedItem);
    }

    /**
     * Soft-deletes the item identified by the given id if it belongs to the authenticated user.
     *
     * @param id the UUID of the item to soft-delete
     * @return a confirmation message containing the deleted item's id
     * @throws AppException with ErrorCode.ITEM_NOT_FOUND if no active item with the given id exists
     * @throws AppException with ErrorCode.UNAUTHORIZED if the authenticated user does not own the item
     */
    @Override
    public String deleteItem(UUID id) {
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder
                .getContext().getAuthentication()).getPrincipal();
        var item = itemRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_FOUND));

        if (!item.getUser().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        item.setDeleted(true);
        itemRepository.save(item);

        return "item has delete id: " + item.getId();
    }

    /**
     * Retrieve all non-deleted items belonging to the current authenticated user.
     *
     * @return a list of ItemResponse objects representing each non-deleted item owned by the current user
     */
    @Override
    public List<ItemResponse> getAllItems() {
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder
                .getContext().getAuthentication()).getPrincipal();

        var items = itemRepository.findByUserAndIsDeletedFalse(currentUser);

        return items.stream().map(this::mapToItemResponse).toList();
    }

    /**
     * Searches the current user's items by optional name, type, and color filters and returns a paginated result.
     *
     * @param name  optional substring to match item names (nullable)
     * @param type  optional item type filter (nullable)
     * @param color optional item color filter (nullable)
     * @param page  1-based page index to return
     * @param size  number of items per page
     * @return      a PageResponse containing the requested page of ItemResponse objects and pagination metadata
     */
    @Override
    public PageResponse<ItemResponse> searchItems(String name, String type, String color, int page, int size) {
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder
                .getContext().getAuthentication()).getPrincipal();
        // create Object from pageable
        var pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

        var itemPage = itemRepository.searchItems(currentUser, name, type, color, pageable);

        var itemResponses = itemPage.getContent().stream().map(this::mapToItemResponse).toList();
        return PageResponse.<ItemResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(itemPage.getTotalPages())
                .totalElements(itemPage.getNumberOfElements())
                .data(itemResponses)
                .build();
    }

    /**
     * Builds statistics for the currently authenticated user's wardrobe.
     *
     * The response includes the total number of non-deleted items and a mapping
     * from item type name to the count of non-deleted items of that type.
     *
     * @return a {@link WardrobeStatisticsResponse} containing `totalItems` and
     *         `itemsByType` (type name -> count)
     */
    @Override
    public WardrobeStatisticsResponse getWardrobeStatistics() {
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        var totalItems = itemRepository.countByUserAndIsDeletedFalse(currentUser);

        var results = itemRepository.countItemsGroupByType(currentUser);


        var itemsByType = results.stream()
                .collect(Collectors.toMap(
                        result -> result[0].toString(),
                        result -> Long.valueOf(String.valueOf(result[1]))
                ));

        return WardrobeStatisticsResponse.builder()
                .totalItems(totalItems)
                .itemsByType(itemsByType)
                .build();
    }

    /**
     * Restores a previously deleted item identified by its UUID for the current authenticated user.
     *
     * @param id the UUID of the item to restore
     * @return an ItemResponse representing the restored item
     * @throws AppException with ErrorCode.ITEM_NOT_FOUND if no deleted item with the given id exists
     * @throws AppException with ErrorCode.UNAUTHORIZED if the item is not owned by the current user
     */
    @Override
    public ItemResponse restoreItem(UUID id) {
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        Item item = itemRepository.findByIdAndIsDeletedTrue(id)
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_FOUND));

        if (!item.getUser().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        item.setDeleted(false);
        item.setUpdatedAt(Instant.now());
        Item restoredItem = itemRepository.save(item);
        return mapToItemResponse(restoredItem);

    }

    /**
     * Retrieve the current user's non-deleted items and map them to ItemResponse objects.
     *
     * @return the list of the current user's non-deleted items as ItemResponse objects
     */
    @Override
    public List<ItemResponse> getTrashItems() {
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        var trashItems = itemRepository.findByUserAndIsDeletedFalse(currentUser);

        return trashItems.stream().map(this::mapToItemResponse).toList();
    }

    /**
     * Permanently deletes the item identified by the given id that belongs to the current authenticated user.
     *
     * @param id the UUID of the item to permanently delete
     * @throws AppException with ErrorCode.ITEM_NOT_FOUND if no item exists with the provided id
     * @throws AppException with ErrorCode.UNAUTHORIZED if the current user does not own the item
     */
    @Override
    @Transactional
    public void hardDeleteItem(UUID id) {
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        var item = itemRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_FOUND));

        if (!item.getUser().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        itemRepository.delete(item);

    }


    /**
     * Builds an ItemResponse DTO from the given Item entity.
     *
     * @param item the source Item entity to map
     * @return an ItemResponse populated with id, name, type, color, season, brand, occasion, imageUrl, and createdAt
     */
    private ItemResponse mapToItemResponse(Item item) {
        return ItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .type(item.getType())
                .color(item.getColor())
                .season(item.getSeason())
                .brand(item.getBrand())
                .occasion(item.getOccasion())
                .imageUrl(item.getImageUrl())
                .createdAt(item.getCreatedAt())
                .build();
    }
}
