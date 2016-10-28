/******************************************************************************************************

 Copyright (c) 2016 EPAM Systems Inc.

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 *****************************************************************************************************/

package com.epam.dlab.backendapi.core.guice;

import com.epam.dlab.backendapi.SelfServiceApplicationConfiguration;
import com.epam.dlab.backendapi.health.HealthChecker;
import com.epam.dlab.backendapi.health.MongoHealthChecker;
import com.epam.dlab.backendapi.health.ProvisioningHealthChecker;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.dropwizard.setup.Environment;

import static com.epam.dlab.backendapi.health.HealthChecks.MONGO_HEALTH_CHECKER;
import static com.epam.dlab.backendapi.health.HealthChecks.PROVISIONING_HEALTH_CHECKER;

abstract class BaseModule extends AbstractModule {
    protected SelfServiceApplicationConfiguration configuration;
    protected Environment environment;

    public BaseModule(SelfServiceApplicationConfiguration configuration, Environment environment) {
        this.configuration = configuration;
        this.environment = environment;
    }

    @Provides
    @Singleton
    @Named(MONGO_HEALTH_CHECKER)
    public HealthChecker mongoHealthChecker() {
        return new MongoHealthChecker();
    }

    @Provides
    @Singleton
    @Named(PROVISIONING_HEALTH_CHECKER)
    public HealthChecker provisioningHealthChecker() {
        return new ProvisioningHealthChecker();
    }
}
