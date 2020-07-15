package org.recap.controller;

import org.checkerframework.dataflow.qual.*;
import org.junit.*;
import org.recap.*;
import org.springframework.beans.factory.annotation.*;

import java.util.*;

public class DataDumpSequenceRestControllerUT extends BaseTestCase {
    @Autowired
    DataDumpSequenceRestController dataDumpSequenceRestController;
    @Test
    public void  testExportDataDump(){
        dataDumpSequenceRestController.getDynamicRouteBuilder();
        dataDumpSequenceRestController.exportDataDump(new Date().toString());
    }
}
