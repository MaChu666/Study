package com.machugit.es;

import com.machugit.entity.es.UserDoc;
import com.machugit.entity.es.VideoDoc;
import com.machugit.entity.po.UserInfo;
import com.machugit.entity.po.VideoInfo;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EsSearchService {

    private static final Logger logger = LoggerFactory.getLogger(EsSearchService.class);

    private static final String VIDEO_INDEX = "bil_video";
    private static final String USER_INDEX = "bil_user";

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    private volatile boolean esAvailable = true;

    // ==================== Video Search ====================

    public List<VideoDoc> searchVideo(String keyword, int from, int size) {
        if (!esAvailable) return Collections.emptyList();
        try {
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                    .should(QueryBuilders.matchQuery("videoName", keyword).boost(3.0f))
                    .should(QueryBuilders.matchQuery("tags", keyword).boost(2.0f))
                    .should(QueryBuilders.matchQuery("introduction", keyword).boost(1.0f))
                    .should(QueryBuilders.fuzzyQuery("videoName", keyword).boost(1.5f))
                    .minimumShouldMatch(1)
                    .must(QueryBuilders.termQuery("status", 1))
                    .must(QueryBuilders.termQuery("isDeleted", 0));

            NativeSearchQuery query = new NativeSearchQueryBuilder()
                    .withQuery(boolQuery)
                    .withSorts(SortBuilders.scoreSort().order(SortOrder.DESC),
                               SortBuilders.fieldSort("playCount").order(SortOrder.DESC))
                    .withPageable(PageRequest.of(from / size, size))
                    .build();

            SearchHits<VideoDoc> hits = elasticsearchRestTemplate.search(query, VideoDoc.class,
                    IndexCoordinates.of(VIDEO_INDEX));
            esAvailable = true;
            return hits.getSearchHits().stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.warn("ES searchVideo failed, falling back to MySQL: {}", e.getMessage());
            esAvailable = false;
            return Collections.emptyList();
        }
    }

    public List<String> suggestVideo(String keyword, int size) {
        if (!esAvailable) return Collections.emptyList();
        try {
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                    .should(QueryBuilders.matchPhrasePrefixQuery("videoName", keyword).boost(3.0f))
                    .should(QueryBuilders.matchQuery("videoName", keyword).boost(2.0f))
                    .minimumShouldMatch(1)
                    .must(QueryBuilders.termQuery("status", 1))
                    .must(QueryBuilders.termQuery("isDeleted", 0));

            NativeSearchQuery query = new NativeSearchQueryBuilder()
                    .withQuery(boolQuery)
                    .withSorts(SortBuilders.scoreSort().order(SortOrder.DESC))
                    .withPageable(PageRequest.of(0, size))
                    .build();

            SearchHits<VideoDoc> hits = elasticsearchRestTemplate.search(query, VideoDoc.class,
                    IndexCoordinates.of(VIDEO_INDEX));
            esAvailable = true;
            return hits.getSearchHits().stream()
                    .map(h -> h.getContent().getVideoName())
                    .distinct()
                    .limit(size)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.warn("ES suggestVideo failed: {}", e.getMessage());
            esAvailable = false;
            return Collections.emptyList();
        }
    }

    // ==================== User Search ====================

    public List<UserDoc> searchUser(String keyword, int from, int size) {
        if (!esAvailable) return Collections.emptyList();
        try {
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                    .should(QueryBuilders.matchQuery("useName", keyword).boost(3.0f))
                    .should(QueryBuilders.matchQuery("personProfile", keyword).boost(1.0f))
                    .should(QueryBuilders.matchQuery("school", keyword).boost(1.5f))
                    .should(QueryBuilders.fuzzyQuery("useName", keyword).boost(1.5f))
                    .minimumShouldMatch(1);

            NativeSearchQuery query = new NativeSearchQueryBuilder()
                    .withQuery(boolQuery)
                    .withSorts(SortBuilders.scoreSort().order(SortOrder.DESC))
                    .withPageable(PageRequest.of(from / size, size))
                    .build();

            SearchHits<UserDoc> hits = elasticsearchRestTemplate.search(query, UserDoc.class,
                    IndexCoordinates.of(USER_INDEX));
            esAvailable = true;
            return hits.getSearchHits().stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.warn("ES searchUser failed: {}", e.getMessage());
            esAvailable = false;
            return Collections.emptyList();
        }
    }

    // ==================== Indexing ====================

    @Async
    public void indexVideo(VideoInfo video) {
        if (!esAvailable || video == null || video.getVideoId() == null) return;
        try {
            VideoDoc doc = VideoDoc.from(video);
            if (video.getUserName() != null) doc.setUserName(video.getUserName());
            if (video.getUserAvatar() != null) doc.setUserAvatar(video.getUserAvatar());
            IndexQuery indexQuery = new IndexQueryBuilder()
                    .withId(doc.getVideoId())
                    .withObject(doc)
                    .build();
            elasticsearchRestTemplate.index(indexQuery, IndexCoordinates.of(VIDEO_INDEX));
            logger.debug("Indexed video: {}", video.getVideoId());
        } catch (Exception e) {
            logger.warn("Failed to index video {}: {}", video.getVideoId(), e.getMessage());
        }
    }

    @Async
    public void indexUser(UserInfo user) {
        if (!esAvailable || user == null || user.getUserId() == null) return;
        try {
            UserDoc doc = UserDoc.from(user);
            IndexQuery indexQuery = new IndexQueryBuilder()
                    .withId(doc.getUserId())
                    .withObject(doc)
                    .build();
            elasticsearchRestTemplate.index(indexQuery, IndexCoordinates.of(USER_INDEX));
            logger.debug("Indexed user: {}", user.getUserId());
        } catch (Exception e) {
            logger.warn("Failed to index user {}: {}", user.getUserId(), e.getMessage());
        }
    }
}