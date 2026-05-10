package com.example.wardrobeservices.service;

import com.example.wardrobeservices.dto.request.ItemRequest;
import com.example.wardrobeservices.dto.response.ItemResponse;

import java.util.List;
import java.util.UUID;

public interface ItemService {

    ItemResponse addItem(ItemRequest itemRequest);

    ItemResponse updateItem(UUID id, ItemRequest itemRequest);

    String deleteItem(UUID id);

    List<ItemResponse> getAllItems();

    ItemResponse getItemByName(String name);

    ItemResponse getItemByType(String type);

    ItemResponse getGetItemBySeason(String season);

}
