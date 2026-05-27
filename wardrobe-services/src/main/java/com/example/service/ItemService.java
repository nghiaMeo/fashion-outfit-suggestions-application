package com.example.service;

import com.example.dto.ItemRequest;
import com.example.dto.ItemResponse;
import com.example.dto.PageResponse;
import com.example.dto.WardrobeStatisticsResponse;

import java.util.List;
import java.util.UUID;

public interface ItemService {

    ItemResponse addItem(ItemRequest itemRequest);

    ItemResponse updateItem(UUID id, ItemRequest itemRequest);

    String deleteItem(UUID id);

    List<ItemResponse> getAllItems();

    PageResponse<ItemResponse> searchItems(String name, String type, String color, String tag, int page, int size);

    WardrobeStatisticsResponse getWardrobeStatistics();

    ItemResponse restoreItem(UUID id);

    List<ItemResponse> getTrashItems();

    void hardDeleteItem(UUID id);


}
