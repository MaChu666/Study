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
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EsSearchService {

    private static final Logger logger = LoggerFactory.getLogger(EsSearchService.class);

    private static final String VIDEO_INDEX = "bil_video";
    private static final String USER_INDEX = "bil_user";

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    // ==================== Video Search ====================

    /**
     * 全文搜索视频（姓名、标签、简介多字段匹配）
     */
    public List<VideoDoc> searchVideo(String keyword, int from, int size) {
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
        return hits.getSearchHits().stream()
                .map(org.springframework.data.elasticsearch.core.SearchHit::getContent)
                .collect(Collectors.toList());
    }

    /**
     * 视频搜索建议/联想（只按名称前缀匹配）
     */
    public List<String> suggestVideo(String keyword, int size) {
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
        return hits.getSearchHits().stream()
                .map(h -> h.getContent().getVideoName())
                .distinct()
                .limit(size)
                .collect(Collectors.toList());
    }

    // ==================== User Search ====================

    /**
     * 搜索用户
     */
    public List<UserDoc> searchUser(String keyword, int from, int size) {
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
        return hits.getSearchHits().stream()
                .map(org.springframework.data.elasticsearch.core.SearchHit::getContent)
                .collect(Collectors.toList());
    }

    // ==================== Indexing ====================

    /**
     * 索引视频文档（新增或更新）
     */
    @Async
    public void indexVideo(VideoInfo video) {
        if (video == null || video.getVideoId() == null) return;
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
            logger.error("Failed to index video {}: {}", video.getVideoId(), e.getMessage());
        }
    }

    /**
     * 索引用户文档（新增或更新）
     */
    @Async
    public void indexUser(UserInfo user) {
        if (user == null || user.getUserId() == null) return;
        try {
            UserDoc doc = UserDoc.from(user);
            IndexQuery indexQuery = new IndexQueryBuilder()
                    .withId(doc.getUserId())
                    .withObject(doc)
                    .build();
            elasticsearchRestTemplate.index(indexQuery, IndexCoordinates.of(USER_INDEX));
            logger.debug("Indexed user: {}", user.getUserId());
        } catch (Exception e) {
            logger.error("Failed to index user {}: {}", user.getUserId(), e.getMessage());
        }
    }

    /**
     * 从 ES 中删除视频
     */
    public void deleteVideo(String videoId) {
        try {
            elasticsearchRestTemplate.delete(videoId, IndexCoordinates.of(VIDEO_INDEX));
        } catch (Exception e) {
            logger.error("Failed to delete video from ES {}: {}", videoId, e.getMessage());
        }
    }

    /**
     * 从 ES 中删除用户
     */
    public void deleteUser(String userId) {
        try {
            elasticsearchRestTemplate.delete(userId, IndexCoordinates.of(USER_INDEX));
        } catch (Exception e) {
            logger.error("Failed to delete user from ES {}: {}", userId, e.getMessage());
        }
    }

    // ==================== Statistics ====================

    /**
     * 获取视频发布趋势统计（按日期聚合）
     */
    public long countVideos() {
        try {
            return elasticsearchRestTemplate.count(
                    new NativeSearchQueryBuilder()
                            .withQuery(QueryBuilders.matchAllQuery())
                            .build(),
                    VideoDoc.class,
                    IndexCoordinates.of(VIDEO_INDEX));
        } catch (Exception e) {
            logger.error("Failed to count videos from ES: {}", e.getMessage());
            return 0;
        }
    }
}