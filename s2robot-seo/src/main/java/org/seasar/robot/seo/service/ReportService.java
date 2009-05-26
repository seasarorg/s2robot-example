/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.robot.seo.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.seasar.robot.entity.AccessResult;
import org.seasar.robot.seo.Constants;
import org.seasar.robot.service.DataService;
import org.seasar.robot.util.AccessResultCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 *
 */
public class ReportService {
    private static final Logger logger = LoggerFactory
            .getLogger(ReportService.class);

    @Resource
    protected DataService dataService;

    public String charsetName = "UTF-8";

    public void report(String path, String sessionId) {
        File file = new File(path);
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file), charsetName));
            final BufferedWriter writer = bw;
            dataService.iterate(sessionId, new AccessResultCallback() {
                public void iterate(AccessResult accessResult) {
                    // create a result
                    StringBuilder buf = new StringBuilder();
                    buf.append(accessResult.getUrl());
                    buf.append(',');
                    buf.append(getLinkStatus(accessResult));

                    try {
                        writer.write(buf.toString());
                        writer.newLine();
                    } catch (IOException e) {
                        logger.warn("I/O Exception: " + accessResult.getUrl(),
                                e);
                    }
                }
            });

            bw.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(bw);
        }
    }

    protected String getLinkStatus(AccessResult accessResult) {
        if (accessResult.getHttpStatusCode() == Constants.HTTP_STATUS_OK) {
            return "OK";
        } else {
            return "FAILED";
        }
    }
}
