/***************************************************************************

Copyright (c) 2016, EPAM SYSTEMS INC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

****************************************************************************/

package com.epam.dlab.backendapi;

import com.epam.dlab.mongo.MongoServiceFactory;
import com.epam.dlab.rest.client.RESTServiceFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static com.epam.dlab.auth.SecurityRestAuthenticator.SECURITY_SERVICE;

/** Configuration for Self Service.
 */
public class SelfServiceApplicationConfiguration extends Configuration {
    public static final String MONGO = "mongo";
    public static final String PROVISIONING_SERVICE = "provisioningService";

    @Valid
    @JsonProperty
    private boolean mocked;

    @Valid
    @NotNull
    @JsonProperty(MONGO)
    private MongoServiceFactory mongoFactory = new MongoServiceFactory();

    @Valid
    @NotNull
    @JsonProperty(SECURITY_SERVICE)
    private RESTServiceFactory securityFactory;

    @Valid
    @NotNull
    @JsonProperty(PROVISIONING_SERVICE)
    private RESTServiceFactory provisioningFactory = new RESTServiceFactory();

    /** Returns <b>true</b> if service is a mock.
     * @return
     */
    public boolean isMocked() {
        return mocked;
    }

    /** Returns the factory for Mongo database service.
     */
    public MongoServiceFactory getMongoFactory() {
        return mongoFactory;
    }

    /** Returns the factory for security service. 
     */
    public RESTServiceFactory getSecurityFactory() {
        return securityFactory;
    }

    /** Returns the factory for provisioning service. 
     */
    public RESTServiceFactory getProvisioningFactory() {
        return provisioningFactory;
    }

}
