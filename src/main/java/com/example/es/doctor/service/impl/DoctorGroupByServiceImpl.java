package com.example.es.doctor.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.es.doctor.service.DoctorGroupByService;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class DoctorGroupByServiceImpl implements DoctorGroupByService {

    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public JSONArray getGroupByQuery() {
        TermsAggregationBuilder termsAggregation = AggregationBuilders
                .terms("myTerms").field("hospitalId")
                .order(BucketOrder.count(false)).size(20);//总数降序排，默认查10条
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withIndices("hospital").withTypes("doctor")
                .addAggregation(termsAggregation).build();
        // 执行语句获取聚合结果
        Aggregations aggregations = elasticsearchTemplate.query(nativeSearchQuery, SearchResponse::getAggregations);
        Object o = aggregations.getAsMap().get("myTerms");
        JSONObject jsonObject = JSONObject.parseObject(String.valueOf(o)).getJSONObject("myTerms");
        return jsonObject.getJSONArray("buckets");
    }
}
