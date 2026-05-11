package com.example.wardrobeservices.service;

import com.example.wardrobeservices.dto.request.ChangePasswordRequest;
import com.example.wardrobeservices.dto.request.ForgotPasswordRequest;
import com.example.wardrobeservices.dto.request.LoginRequest;
import com.example.wardrobeservices.dto.request.RefreshTokenRequest;
import com.example.wardrobeservices.dto.request.ResetPasswordRequest;
import com.example.wardrobeservices.dto.response.AuthResponse;


public interface AuthService {
    
    AuthResponse login(LoginRequest request);
    
    AuthResponse refreshToken(RefreshTokenRequest request);
    
    /**
 * Invalidates the provided refresh token and associated access token and terminates the corresponding session.
 *
 * @param request     the refresh token request used to identify and revoke the session
 * @param accessToken the access token associated with the session to be invalidated
 */
void logout(RefreshTokenRequest request, String accessToken);

    /**
 * Changes the user's password using the information in the request.
 *
 * @param request container with the data required to change the password (for example: identifier of the account, current password or verification token, and the new password)
 */
void changePassword(ChangePasswordRequest request);
    /**
 * Initiates the password reset process for the account identified in the request.
 *
 * @param request contains the identifier (for example, email or username) and any metadata required to start the reset flow; triggers sending of reset instructions or tokens to the user.
 */
void forgotPassword(ForgotPasswordRequest request);
    /**
 * Completes a password reset for the account identified in the request by applying the new password provided.
 *
 * @param request contains the data required to finalize a password reset (for example: reset token, new password, and any verification data)
 */
void resetPassword(ResetPasswordRequest request);
}

