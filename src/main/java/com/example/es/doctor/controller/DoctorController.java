package com.example.es.doctor.controller;

import com.example.es.common.controller.BaseController;
import com.example.es.common.dto.BaseResponse;
import com.example.es.config.aspect.annotation.LogForController;
import com.example.es.doctor.domain.Doctor;
import com.example.es.doctor.service.DoctorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("doctor")
@Api(description = "添加测试数据 -- 医生")
public class DoctorController extends BaseController {

    @Resource
    private DoctorService doctorService;

    @LogForController
    @ApiOperation(value = "删除数据", notes = "删除数据")
    @RequestMapping(value = "/drop", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public BaseResponse<Object> drop() {
        BaseResponse<Object> baseResponse = new BaseResponse<>();
        doctorService.drop();
        baseResponse.setCode(0);
        baseResponse.setMsg("删除数据成功");
        return baseResponse;
    }

    @LogForController
    @ApiOperation(value = "保存医生数据", notes = "保存医生数据")
    @RequestMapping(value = "/saveDoctor", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public BaseResponse<Object> saveDoctor() {
        BaseResponse<Object> baseResponse = new BaseResponse<>();
        doctorService.saveDoctor();
        baseResponse.setCode(0);
        baseResponse.setMsg("添加数据成功");
        return baseResponse;
    }

    @LogForController
    @ApiOperation(value = "查询医生数据", notes = "查询医生数据")
    @RequestMapping(value = "/findDoctors", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public BaseResponse<Object> findDoctors(String context) {
        BaseResponse<Object> baseResponse = new BaseResponse<>();
        List<Doctor> list = doctorService.findByHospitalNameLikeOrDoctorNameLikeOrSpecialtyLikeOrLabelLike(context);
        baseResponse.setData(list);
        baseResponse.setCode(0);
        baseResponse.setMsg("返回数据成功");
        return baseResponse;
    }

    @LogForController
    @ApiOperation(value = "查询医生数据", notes = "查询医生数据")
    @RequestMapping(value = "/searchDoctor", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public BaseResponse<Object> searchDoctor(String searchContent) {
        BaseResponse<Object> baseResponse = new BaseResponse<>();
        List<Doctor> list = doctorService.searchDoctor(0,20,searchContent);
        baseResponse.setData(list);
        baseResponse.setCode(0);
        baseResponse.setMsg("返回数据成功");
        return baseResponse;
    }
}
