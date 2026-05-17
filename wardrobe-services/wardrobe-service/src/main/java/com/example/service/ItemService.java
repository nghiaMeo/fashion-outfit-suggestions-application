package com.example.service;

import com.example.dto.request.ItemRequest;
import com.example.dto.response.ItemResponse;
import com.example.dto.response.PageResponse;
import com.example.dto.response.WardrobeStatisticsResponse;

import java.util.List;
import java.util.UUID;

public interface ItemService {

    ItemResponse addItem(ItemRequest itemRequest);

    ItemResponse updateItem(UUID id, ItemRequest itemRequest);

    String deleteItem(UUID id);

    List<ItemResponse> getAllItems();

    PageResponse<ItemResponse> searchItems(String name, String type, String color, int page, int size);

    WardrobeStatisticsResponse getWardrobeStatistics();

    ItemResponse restoreItem(UUID id);

    List<ItemResponse> getTrashItems();

    void hardDeleteItem(UUID id);


}
