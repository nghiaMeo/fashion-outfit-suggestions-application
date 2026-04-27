package com.example.wardrobeservices.repository;

import com.example.wardrobeservices.entity.Friend;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface FriendRepository  extends CrudRepository<Friend, UUID> {}
