package org.n3r.eop.demo;

import java.security.MessageDigest;

import javax.xml.bind.DatatypeConverter;

public class Md5Sign  {
    public String sign(String info, String pubKey) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest((pubKey + info + pubKey).getBytes("UTF-8"));
            return DatatypeConverter.printBase64Binary(digest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public boolean verify(String info, String signed, String pubKey) {
        String computedSign = sign(info, pubKey);
        return signed.equals(computedSign);
    }

}