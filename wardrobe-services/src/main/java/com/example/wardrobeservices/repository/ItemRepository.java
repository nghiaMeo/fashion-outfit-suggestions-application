package com.example.wardrobeservices.repository;

import com.example.wardrobeservices.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID    > {
}
