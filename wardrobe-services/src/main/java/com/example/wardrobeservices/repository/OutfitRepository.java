package com.example.wardrobeservices.repository;

import com.example.wardrobeservices.entity.Outfit;
import com.example.wardrobeservices.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutfitRepository extends JpaRepository<Outfit, UUID> {

    long countByUser(User user);

    List<Outfit> findByUser(User user);

    @Query("SELECT o FROM Outfit o WHERE o.user = :user " +
            "AND (:occasion IS NULL OR o.occasion = :occasion)" +
            "AND (:isFavorite IS NULL OR o.isFavorite = :isFavorite )" )
    List<Outfit> searchOutfits(
            @Param("user") User user,
            @Param("occasion") String occasion,
            @Param("isFavorite") Boolean isFavorite
    );
}

