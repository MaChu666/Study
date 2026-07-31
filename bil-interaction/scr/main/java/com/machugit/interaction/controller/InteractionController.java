package com.machugit.interaction.controller;

import com.machugit.interaction.service.InteractionService;
import com.machugit.entity.vo.ResponseVO;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

@RestController
@RequestMapping("/interact")
public class InteractionController {
    @Resource
    private InteractionService interactionService;

    @PostMapping("/addCoin")
    public ResponseVO addCoin(@RequestParam String userId, @RequestParam String videoId, @RequestParam Integer count) {
        interactionService.addCoin(userId, videoId, count);
        return ResponseVO.ok();
    }

    @PostMapping("/play")
    public ResponseVO recordPlay(@RequestParam String videoId) {
        interactionService.recordPlay(videoId);
        return ResponseVO.ok();
    }

    @PostMapping("/like")
    public ResponseVO like(@RequestParam String userId, @RequestParam String videoId) {
        interactionService.like(userId, videoId);
        return ResponseVO.ok();
    }
}