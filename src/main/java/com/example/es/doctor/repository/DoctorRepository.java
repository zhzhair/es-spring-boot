package com.example.es.doctor.repository;


import com.example.es.doctor.domain.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * ES 操作类
 */
public interface DoctorRepository extends ElasticsearchRepository<Doctor, Long> {

    Page<Doctor> findByHospitalNameLikeOrDoctorNameLike(String hospitalName,String doctorName,String specialty,String label, Pageable pageable);
}
