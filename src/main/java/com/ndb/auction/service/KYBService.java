package com.ndb.auction.service;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.Part;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.ndb.auction.dao.oracle.user.UserKybDao;
import com.ndb.auction.models.user.UserKyb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KYBService extends BaseService {

    private static final String BUCKET_NAME = "auctionupload";

    @Autowired
    private UserKybDao userKybDao;

    public KYBService() {
    }

    public UserKyb getByUserId(int userId) {
        return userKybDao.selectById(userId);
    }

    public List<UserKyb> getAll() {
        return userKybDao.selectAll(null);
    }

    public UserKyb updateInfo(int userId, String country, String companyName, String regNum) {
        UserKyb kyb = new UserKyb();
        kyb.setId(userId);
        kyb.setCountry(country);
        kyb.setCompanyName(companyName);
        kyb.setRegNum(regNum);
        userKybDao.insertOrUpdate(kyb);
        return kyb;
    }

    public UserKyb updateFile(int userId, List<Part> fileList) {
        int count = fileList.size();
        UserKyb kyb = new UserKyb();
        kyb.setId(userId);
        if (count == 0)
            return kyb;
        {
            Part file = fileList.get(0);
            String fileName = file.getName();
            String key = "kyb-" + userId + "-" + fileName;
            uploadFileS3(key, file);
            kyb.setAttach1Key(key);
            kyb.setAttach1Filename(fileName);
        }
        if (count > 1) {
            Part file = fileList.get(1);
            String fileName = file.getName();
            String key = "kyb-" + userId + "-" + fileName;
            uploadFileS3(key, file);
            kyb.setAttach2Key(key);
            kyb.setAttach2Filename(fileName);
        }

        userKybDao.insertOrUpdate(kyb);
        return kyb;
    }

    private boolean uploadFileS3(String key, Part file) {
        // try {
        //     ObjectMetadata metadata = new ObjectMetadata();
        //     metadata.setContentLength(file.getSize());
        //     // s3.putObject(BUCKET_NAME, key, file.getInputStream(), metadata);
        //     return true;
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
        return false;
    }

}
