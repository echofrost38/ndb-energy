package com.ndb.auction.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.Part;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.ndb.auction.dao.KYBDao;
import com.ndb.auction.models.KYB;

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

    public KYB getByUserId(String userId) {
        return kybDao.getByUserId(userId);
    }

    public List<KYB> getAll() {
        return kybDao.getAll();
    }

    public KYB updateInfo(String userId, String country, String companyName, String regNum) {
        KYB kyb = new KYB();
        kyb.setUserId(userId);
        kyb.setCountry(country);
        kyb.setCompanyName(companyName);
        kyb.setRegNum(regNum);
        return kybDao.addList(kyb);
    }

    public KYB updateFile(String userId, List<Part> fileList) {
        Set<String> files = new HashSet<>();
        for (Part file : fileList) {
            String fileName = file.getName();
            String fullName = FilenameUtils.concat(userId, fileName);
            uploadFileS3(fullName, file);
            files.add(fileName);
        }
        KYB kyb = new KYB();
        kyb.setUserId(userId);
        kyb.setFiles(files);
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
