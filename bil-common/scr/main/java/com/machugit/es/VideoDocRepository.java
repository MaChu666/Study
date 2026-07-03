package com.machugit.es;

import com.machugit.entity.es.VideoDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoDocRepository extends ElasticsearchRepository<VideoDoc, String> {

    List<VideoDoc> findByVideoNameContainingOrTagsContainingOrIntroductionContaining(
            String name, String tags, String intro);

    List<VideoDoc> findByUserId(String userId);

    void deleteByVideoId(String videoId);
}