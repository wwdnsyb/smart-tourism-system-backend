package com.example.smarttourism.service;

import com.example.smarttourism.entity.ScenicSpot;

import java.util.List;
import java.util.Optional;

public interface ScenicSpotService {

    ScenicSpot create(ScenicSpot scenicSpot);

    ScenicSpot update(Long id, ScenicSpot scenicSpot);

    void deleteById(Long id);

    Optional<ScenicSpot> findById(Long id);

    List<ScenicSpot> findAll();
}

