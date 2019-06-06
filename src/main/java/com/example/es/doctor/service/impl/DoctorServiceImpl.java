package com.example.es.doctor.service.impl;

import com.example.es.doctor.dao.mapper.DoctorMapper;
import com.example.es.doctor.domain.Doctor;
import com.example.es.doctor.repository.DoctorRepository;
import com.example.es.doctor.service.DoctorService;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class DoctorServiceImpl implements DoctorService {
    @Resource
    private DoctorMapper doctorMapper;
    @Resource
    private DoctorRepository doctorRepository;
    // 分页参数 -> TODO 代码可迁移到具体项目的公共 common 模块
    private static final Integer pageNumber = 0;
    private static final Integer pageSize = 10;
    private Pageable pageable = PageRequest.of(pageNumber,pageSize);

    @Override
    public void saveDoctor() {
        List<HashMap<String,Object>> list = doctorMapper.getDoctors();
        IntStream.range(0,list.size()).parallel().forEach(i->save(i,list));
    }

    private void save(long i,List<HashMap<String,Object>> list){
        HashMap<String,Object> map = list.get((int)i);
        Doctor doctor = new Doctor();
        doctor.setId(i);
        doctor.setDoctorId(map.get("doctorId").toString());
        doctor.setDoctorName(map.get("doctorName").toString());
        doctor.setDoctorTitle(map.get("doctorTitle").toString());
        doctor.setDoctorDes(map.get("doctorDes").toString());
        doctor.setSpecialty(map.get("specialty").toString());
        doctor.setLabel(map.get("label").toString());
        doctor.setHospitalId(map.get("hospitalId").toString());
        doctor.setHospitalName(map.get("hospitalName").toString());
        doctorRepository.save(doctor);
    }

    @Override
    public void drop() {
        doctorRepository.deleteAll();
    }

    @Override
    public List<Doctor> findByHospitalNameLikeOrDoctorNameLikeOrSpecialtyLikeOrLabelLike(String hospitalName) {
        return doctorRepository.findByHospitalNameLikeOrDoctorNameLikeOrSpecialtyLikeOrLabelLike(hospitalName,hospitalName,hospitalName,hospitalName,pageable).getContent();
    }

    @Override
    public List<Doctor> searchDoctor(Integer pageNumber, Integer pageSize, String searchContent) {
        // 校验分页参数
        if (pageSize == null || pageSize <= 0) {
            pageSize = 20;
        }

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = 0;
        }

        // 构建搜索查询
        SearchQuery searchQuery = this.getDoctorSearchQuery(pageNumber,pageSize,searchContent);

        Page<Doctor> docPage = doctorRepository.search(searchQuery);
        return docPage.getContent();
    }

    /**
     * 根据搜索词构造搜索查询语句
     *
     * 代码流程：
     *      - 权重分查询
     *      - 短语匹配
     *      - 设置权重分最小值
     *      - 设置分页参数
     *
     * @param pageNumber 当前页码
     * @param pageSize 每页大小
     * @param searchContent 搜索内容
     * @return SearchQuery
     */
    private SearchQuery getDoctorSearchQuery(Integer pageNumber, Integer pageSize,String searchContent) {
        // 短语匹配到的搜索词，求和模式累加权重分
        // 权重分查询 https://www.elastic.co/guide/cn/elasticsearch/guide/current/function-score-query.html
        //   - 短语匹配 https://www.elastic.co/guide/cn/elasticsearch/guide/current/phrase-matching.html
        //   - 字段对应权重分设置，可以优化成 enum
        //   - 由于无相关性的分值默认为 1 ，设置权重分最小值为 10
        //搜索全部文档
//        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(searchContent,"specialty", "label", "doctorName", "hospitalName");
//        QueryBuilders.rangeQuery("age").gt(30);
//        QueryBuilder queryBuilder = QueryBuilders.wildcardQuery("name", "*" + searchContent + "*");
//        QueryBuilder queryBuilder1 = QueryBuilders.wildcardQuery("name", "*" + searchContent + "*");
//        QueryBuilder queryBuilder2 = QueryBuilders.wildcardQuery("description", "*beautiful*");
//        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        boolQueryBuilder.must(queryBuilder1).must(queryBuilder2);
//        boolQueryBuilder.must(QueryBuilders.fuzzyQuery("doctorName", "张"));//模糊搜索
//        boolQueryBuilder.should(queryBuilder1).should(queryBuilder2);
//        QueryBuilder queryBuilder = QueryBuilders.matchPhraseQuery("name", searchContent);

//        FieldSortBuilder sort = SortBuilders.fieldSort("doctorId").order(SortOrder.DESC);
//        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
//        nativeSearchQueryBuilder.withQuery(boolQueryBuilder).withSort(sort);
        ScoreFunctionBuilder scoreFunctionBuilder = ScoreFunctionBuilders.weightFactorFunction(999.0f);
        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(queryBuilder,scoreFunctionBuilder)
                .scoreMode(FunctionScoreQuery.ScoreMode.SUM).setMinScore(100.0f);
        // 分页参数
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(functionScoreQueryBuilder).build();
    }

//    private void test(){
//            （1）统计某个字段的数量
//        ValueCountBuilder vcb=  AggregationBuilders.count("count_uid").field("uid");
//（2）去重统计某个字段的数量（有少量误差）
//        CardinalityBuilder cb= AggregationBuilders.cardinality("distinct_count_uid").field("uid");
//（3）聚合过滤
//        FilterAggregationBuilder fab= AggregationBuilders.filter("uid_filter").filter(QueryBuilders.queryStringQuery("uid:001"));
//（4）按某个字段分组
//        TermsBuilder tb=  AggregationBuilders.terms("group_name").field("name");
//（5）求和
//        SumBuilder  sumBuilder=	AggregationBuilders.sum("sum_price").field("price");
//（6）求平均
//        AvgBuilder ab= AggregationBuilders.avg("avg_price").field("price");
//（7）求最大值
//        MaxBuilder mb= AggregationBuilders.max("max_price").field("price");
//（8）求最小值
//        MinBuilder min=	AggregationBuilders.min("min_price").field("price");
//（9）按日期间隔分组
//        DateHistogramBuilder dhb= AggregationBuilders.dateHistogram("dh").field("date");
//（10）获取聚合里面的结果
//        TopHitsBuilder thb=  AggregationBuilders.topHits("top_result");
//（11）嵌套的聚合
//        NestedBuilder nb= AggregationBuilders.nested("negsted_path").path("quests");
//（12）反转嵌套
//        AggregationBuilders.reverseNested("res_negsted").path("kps ");
        /*---------------------
                作者：像雾像雨又像风_
        来源：CSDN
        原文：https://blog.csdn.net/topdandan/article/details/81436141
        版权声明：本文为博主原创文章，转载请附上博文链接！*/
//    }
}
