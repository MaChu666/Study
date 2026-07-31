package com.machugit.interaction.service;

public interface InteractionService {
    void addCoin(String userId, String videoId, Integer count);
    void recordPlay(String videoId);
    void like(String userId, String videoId);
}