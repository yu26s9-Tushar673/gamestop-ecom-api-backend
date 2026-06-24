package org.yearup.service;

import org.springframework.stereotype.Service;
import org.yearup.exception.ResourceNotFoundException;
import org.yearup.models.Profile;
import org.yearup.repository.ProfileRepository;

@Service
public class ProfileService
{
    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository)
    {
        this.profileRepository = profileRepository;
    }

    public Profile create(Profile profile)
    {
        return profileRepository.save(profile);
    }

    public Profile getByUserId(int userId) {
        return profileRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User Not Found: " + userId));
    }
    
    public Profile update(int userId, Profile updatedProfile) {
        updatedProfile.setUserId(userId);
        return profileRepository.save(updatedProfile);
    }
}
