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
package org.seasar.robot.seo.transformer;

import org.seasar.robot.entity.ResponseData;
import org.seasar.robot.entity.ResultData;
import org.seasar.robot.transformer.impl.FileTransformer;

/**
 * @author shinsuke
 *
 */
public class CustomTransformer extends FileTransformer {

    @Override
    public ResultData transform(ResponseData responseData) {
        ResultData resultData = super.transform(responseData);
        // 単純に小リンクたちのログを出力する
        // S2Rotot的にはTransformerを実装してResultDataで保持するのが良い
        for (String childUrl : resultData.getChildUrlSet()) {
            System.out.println(responseData.getUrl() + " -> " + childUrl);
        }
        return resultData;
    }

}
