package com.example.wardrobeservices.service.impl;

import com.example.wardrobeservices.dto.request.UserCreationRequest;
import com.example.wardrobeservices.dto.response.ItemResponse;
import com.example.wardrobeservices.dto.response.UserResponse;
import com.example.wardrobeservices.entity.Item;
import com.example.wardrobeservices.entity.User;
import com.example.wardrobeservices.entity.UserPreference;
import com.example.wardrobeservices.entity.enums.Role;
import com.example.wardrobeservices.exception.AppException;
import com.example.wardrobeservices.exception.ErrorCode;
import com.example.wardrobeservices.repository.UserPreferenceRepository;
import com.example.wardrobeservices.repository.UserRepository;
import com.example.wardrobeservices.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserPreferenceRepository userPreferenceRepository;
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

        // 2. KHỞI TẠO ĐỐI TƯỢNG (MAPPING)
        // Dùng Builder pattern để nặn ra đối tượng User từ dữ liệu gửi lên
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
