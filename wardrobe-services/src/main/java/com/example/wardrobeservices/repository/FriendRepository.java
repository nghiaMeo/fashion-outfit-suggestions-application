package com.example.wardrobeservices.repository;

import com.example.wardrobeservices.entity.Friend;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FriendRepository extends CrudRepository<Friend, UUID> {
}

