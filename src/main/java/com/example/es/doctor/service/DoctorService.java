package com.example.es.doctor.service;

import com.example.es.doctor.domain.Doctor;

import java.util.List;

public interface DoctorService {

    void saveDoctor();

    void drop();

    List<Doctor> findByHospitalNameLikeOrDoctorNameLike(String hospitalName);

    List<Doctor> searchDoctor(Integer pageNumber, Integer pageSize, String searchContent);

}
