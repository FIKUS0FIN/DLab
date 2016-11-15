/******************************************************************************************************

 Copyright (c) 2016 EPAM Systems Inc.

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 *****************************************************************************************************/

package com.epam.dlab.auth;

import io.dropwizard.auth.Authorizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityAuthorizer implements Authorizer<UserInfo> {
    private final static Logger LOGGER = LoggerFactory.getLogger(SecurityRestAuthenticator.class);

    /*
    Add annotations to the service resource class:
@PermitAll. All authenticated users will have access to the method.
@RolesAllowed("awsUser"). Access will be granted to the users with the specified roles.
@DenyAll. No access will be granted to anyone.
    */

    @Override
    public boolean authorize(UserInfo userInfo, String role) {
        LOGGER.debug("authorize user = {} with role = {}", userInfo.getName(), role);
        if(role == null) {
            return true;
        }

        boolean authorized = false;

        switch(role.toUpperCase()) {
            case "AWSUSER":
                authorized = userInfo.isAwsUser();
                break;
            default:
                authorized = true;
        }

        return authorized;
    }
}
