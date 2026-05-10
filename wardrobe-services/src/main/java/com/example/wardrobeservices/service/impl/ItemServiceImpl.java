package com.example.wardrobeservices.service.impl;

import com.example.wardrobeservices.dto.request.ItemRequest;
import com.example.wardrobeservices.dto.response.ItemResponse;
import com.example.wardrobeservices.entity.Item;
import com.example.wardrobeservices.entity.User;
import com.example.wardrobeservices.exception.AppException;
import com.example.wardrobeservices.exception.ErrorCode;
import com.example.wardrobeservices.repository.ItemRepository;
import com.example.wardrobeservices.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

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

    @Override
    public List<ItemResponse> getAllItems() {
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder
                .getContext().getAuthentication()).getPrincipal();

        var items = itemRepository.findByUserAndIsDeletedFalse(currentUser);

        return items.stream().map(this::mapToItemResponse).toList();
    }

    @Override
    public ItemResponse getItemByName(String name) {
        return null;
    }

    @Override
    public ItemResponse getItemByType(String type) {
        return null;
    }


    @Override
    public ItemResponse getGetItemBySeason(String season) {
        return null;
    }

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
