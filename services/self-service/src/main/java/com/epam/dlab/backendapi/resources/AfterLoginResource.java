package com.epam.dlab.backendapi.resources;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/afterlogin")
@Produces(MediaType.TEXT_HTML)
@RolesAllowed("TestService")
public class AfterLoginResource {
//
//    @Inject
//    AuthenticationServiceConfig authenticationConfig;
//
//    public AfterLoginResource() {
//
//    }
//
//    @GET
//    public AfterLoginView afterLogin(@Auth UserInfo user, @QueryParam("access_token") String token) {
//        return new AfterLoginView(user, token, authenticationConfig);
//    }

}