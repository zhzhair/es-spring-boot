package com.example.es.doctor.controller;

import com.example.es.common.controller.BaseController;
import com.example.es.common.dto.BaseResponse;
import com.example.es.config.aspect.annotation.LogForController;
import com.example.es.doctor.service.DoctorGroupByService;
import com.example.es.doctor.service.DoctorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("doctor")
@Api(description = "添加测试数据 -- 医生")
public class DoctorController extends BaseController {

    @Resource
    private DoctorService doctorService;
    @Resource
    private DoctorGroupByService doctorGroupByService;

    @LogForController
    @ApiOperation(value = "删除es中的数据", notes = "删除es中的数据")
    @RequestMapping(value = "/drop", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public BaseResponse<Object> drop() {
        BaseResponse<Object> baseResponse = new BaseResponse<>();
        doctorService.drop();
        baseResponse.setCode(0);
        baseResponse.setMsg("删除数据成功");
        return baseResponse;
    }

    @LogForController
    @ApiOperation(value = "保存医生数据到es", notes = "读取MySQL的数据，并保存数据到es")
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
    @RequestMapping(value = "/findByHospitalNameLikeOrDoctorNameLike", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public BaseResponse<Object> findByHospitalNameLikeOrDoctorNameLike(String context) {
        BaseResponse<Object> baseResponse = new BaseResponse<>();
        baseResponse.setData(doctorService.findByHospitalNameLikeOrDoctorNameLike(context));
        baseResponse.setCode(0);
        baseResponse.setMsg("返回数据成功");
        return baseResponse;
    }

    @LogForController
    @ApiOperation(value = "查询医生数据", notes = "查询医生数据")
    @RequestMapping(value = "/searchDoctor", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public BaseResponse<Object> searchDoctor(String searchContent) {
        BaseResponse<Object> baseResponse = new BaseResponse<>();
        baseResponse.setData(doctorService.searchDoctor(0,20,searchContent));
        baseResponse.setCode(0);
        baseResponse.setMsg("返回数据成功");
        return baseResponse;
    }

    @LogForController
    @ApiOperation(value = "查询各个医院的医生数", notes = "聚合查询各个医院的医生数")
    @RequestMapping(value = "/searchDoctorCountByHospital", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public BaseResponse<Object> searchDoctorCountByHospital() {
        BaseResponse<Object> baseResponse = new BaseResponse<>();
        baseResponse.setData(doctorGroupByService.getGroupByQuery());
        baseResponse.setCode(0);
        baseResponse.setMsg("返回数据成功");
        return baseResponse;
    }
}
