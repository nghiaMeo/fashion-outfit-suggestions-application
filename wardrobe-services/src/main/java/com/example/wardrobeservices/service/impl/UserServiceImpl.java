package com.example.wardrobeservices.service.impl;

import com.example.wardrobeservices.dto.request.UserCreationRequest;
import com.example.wardrobeservices.dto.response.ItemResponse;
import com.example.wardrobeservices.dto.response.UserProfileResponse;
import com.example.wardrobeservices.dto.response.UserResponse;
import com.example.wardrobeservices.entity.Friendship;
import com.example.wardrobeservices.entity.Item;
import com.example.wardrobeservices.entity.User;
import com.example.wardrobeservices.entity.UserPreference;
import com.example.wardrobeservices.entity.enums.FriendshipStatus;
import com.example.wardrobeservices.entity.enums.Role;
import com.example.wardrobeservices.exception.AppException;
import com.example.wardrobeservices.exception.ErrorCode;
import com.example.wardrobeservices.repository.*;
import com.example.wardrobeservices.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserPreferenceRepository userPreferenceRepository;
    private final ItemRepository itemRepository;
    private final FriendshipRepository friendshipRepository;
    private final OutfitRepository outfitRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse register(UserCreationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }

        var user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .displayName(request.getDisplayName())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        user = userRepository.save(user);

        var preference = UserPreference.builder()
                .user(user)
                .build();
        userPreferenceRepository.save(preference);

        return mapToUserResponse(user);
    }

    @Override
    public UserProfileResponse getUserProfile(UUID userId) {
        var currentUser = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        var targetUser = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        var friendship = friendshipRepository.findRelation(currentUser, targetUser);

        var isFriend = friendship.isPresent() && friendship.get().getStatus() == FriendshipStatus.ACCEPTED;

        if (targetUser.isPrivateProfile() && !isFriend && !targetUser.getId().equals(currentUser.getId())) {
            return UserProfileResponse.builder()
                    .id(targetUser.getId())
                    .displayName(targetUser.getDisplayName())
                    .username(targetUser.getUsername())
                    .avatarUrl(targetUser.getAvatarUrl())
                    .isPrivateProfile(true)
                    .friendshipStatus(friendship.map(Friendship::getStatus).orElse(null))
                    .build();
        }

        return UserProfileResponse.builder()
                .id(targetUser.getId())
                .username(targetUser.getUsername())
                .displayName(targetUser.getDisplayName())
                .avatarUrl(targetUser.getAvatarUrl())
                .bio(targetUser.getBio())
                .itemCount(itemRepository.countByUser(targetUser))
                .outfitCount(outfitRepository.countByUser(targetUser))
                .friendCount(friendshipRepository.countAcceptedFriends(targetUser)) // Cần viết thêm hàm count này
                .isPrivateProfile(targetUser.isPrivateProfile())
                .friendshipStatus(friendship.map(Friendship::getStatus).orElse(null))
                .build();
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }




}
