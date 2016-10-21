/******************************************************************************************************

 Copyright (c) 2016 EPAM Systems Inc.

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 *****************************************************************************************************/

package com.epam.dlab.backendapi.dao;

import static com.epam.dlab.backendapi.dao.MongoCollections.SETTINGS;
import static com.epam.dlab.backendapi.dao.MongoSetting.AWS_REGION;
import static com.epam.dlab.backendapi.dao.MongoSetting.SERIVICE_BASE_NAME;
import static com.mongodb.client.model.Filters.eq;
import static org.apache.commons.lang3.StringUtils.EMPTY;

public class SettingsDAO extends BaseDAO {
    private static final String VALUE = "value";

    public String getServiceBaseName() {
        return getSetting(SERIVICE_BASE_NAME);
    }

    public String getAwsRegion() {
        return getSetting(AWS_REGION);
    }

    private String getSetting(MongoSetting setting) {
        return mongoService.getCollection(SETTINGS).find(eq(ID, setting.getId())).first().getOrDefault(VALUE, EMPTY).toString();
    }
}
