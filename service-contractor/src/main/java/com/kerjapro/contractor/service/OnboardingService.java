package com.kerjapro.contractor.service;

import com.kerjapro.contractor.dto.request.CreateProfileRequest;
import com.kerjapro.contractor.dto.response.SubcontractorProfileDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OnboardingService {

    private final SubcontractorProfileService profileService;

    @Transactional
    public SubcontractorProfileDto onboardSubcontractor(String userId, CreateProfileRequest request) {
        return profileService.createProfile(userId, request);
    }
}
