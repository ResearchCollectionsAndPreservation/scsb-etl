package org.recap.controller.version;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by hemalathas on 17/10/16.
 */
@Controller
public class VersionNumberController {

    @Value("${version.number}")
    private String versionNumber;

    /**
     * Sets application version number.
     *
     * @param versionNumber the version number
     */
    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    /**
     * API to get application version number.
     *
     * @return the version number
     */
    @GetMapping(value = "/getVersionNumber")
    @ResponseBody
    public String getVersionNumber() {
        return versionNumber;
    }


}
