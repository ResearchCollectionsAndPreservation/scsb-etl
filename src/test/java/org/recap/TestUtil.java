package org.recap;

import org.recap.model.jpa.ImsLocationEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestUtil {

    private static final Logger logger = LoggerFactory.getLogger(TestUtil.class);

    private TestUtil() {}


    public static  ImsLocationEntity getImsLocationEntity (int id,String imsLocationCode,String imsLocationName) {
        ImsLocationEntity imsLocationEntity=new ImsLocationEntity();
        imsLocationEntity.setId(id);
        imsLocationEntity.setImsLocationCode(imsLocationCode);
        imsLocationEntity.setImsLocationName(imsLocationName);
        return imsLocationEntity;
    }
}
