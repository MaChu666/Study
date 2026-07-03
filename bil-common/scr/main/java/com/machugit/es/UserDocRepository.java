package com.machugit.es;

import com.machugit.entity.es.UserDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDocRepository extends ElasticsearchRepository<UserDoc, String> {

    List<UserDoc> findByUseNameContainingOrPersonProfileContainingOrSchoolContaining(
            String name, String profile, String school);

    List<UserDoc> findByUseNameContaining(String useName);
}