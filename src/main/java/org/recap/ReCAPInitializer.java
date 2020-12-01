package org.recap;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * Created by peris on 7/15/16.
 */

@Component
public class ReCAPInitializer {

    public ReCAPInitializer(@Value("${etl.data.load.directory}") String etlLoadDir) throws IOException { FileUtils.forceMkdir(new File(etlLoadDir));
    }
}
