package com.bocse.multumesc.uploader;

import com.amazonaws.auth.PropertiesFileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.Region;

import java.io.File;

/**
 * Created by bocse on 05.01.2016.
 */
public class S3Uploader {

    private final AmazonS3 s3client;
    private final String bucketName;

    public S3Uploader(String propertiesFile, String bucketName) {
        s3client = new AmazonS3Client(new PropertiesFileCredentialsProvider(propertiesFile));
        s3client.setRegion(com.amazonaws.regions.Region.getRegion(Regions.EU_CENTRAL_1));
        this.bucketName = bucketName;
    }


    public boolean upload(String keyName, File localFile) {

        try {
            System.out.println("Uploading a new object to S3 from a file "+ localFile.getName()+ " to "+keyName);

            s3client.putObject(new PutObjectRequest(
                    bucketName, keyName, localFile));
        } finally {

        }
        return true;
    }
}
