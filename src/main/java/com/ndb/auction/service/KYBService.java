package com.ndb.auction.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.Part;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.ndb.auction.dao.KYBDao;
import com.ndb.auction.models.user.UserKyb;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KYBService extends BaseService {

    private AmazonS3 s3;
    private static final String BUCKET_NAME = "auctionupload";

    @Autowired
    private KYBDao kybDao;

    public KYBService(AmazonS3 s3) {
        this.s3 = s3;
    }

    public UserKyb getByUserId(int userId) {
        return kybDao.getByUserId(userId);
    }

    public List<UserKyb> getAll() {
        return kybDao.getAll();
    }

    public UserKyb updateInfo(int userId, String country, String companyName, String regNum) {
        UserKyb kyb = new UserKyb();
        kyb.setId(userId);
        kyb.setCountry(country);
        kyb.setCompanyName(companyName);
        kyb.setRegNum(regNum);
        return kybDao.addList(kyb);
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

        return kybDao.addList(kyb);
    }

    private boolean uploadFileS3(String key, Part file) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            s3.putObject(BUCKET_NAME, key, file.getInputStream(), metadata);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
