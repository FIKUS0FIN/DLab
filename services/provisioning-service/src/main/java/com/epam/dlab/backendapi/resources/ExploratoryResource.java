/******************************************************************************************************

 Copyright (c) 2016 EPAM Systems Inc.

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 *****************************************************************************************************/

package com.epam.dlab.backendapi.resources;

import com.epam.dlab.backendapi.ProvisioningServiceApplicationConfiguration;
import com.epam.dlab.backendapi.core.CommandBuilder;
import com.epam.dlab.backendapi.core.CommandExecutor;
import com.epam.dlab.backendapi.core.DockerCommands;
import com.epam.dlab.backendapi.core.docker.command.DockerAction;
import com.epam.dlab.backendapi.core.docker.command.RunDockerCommand;
import com.epam.dlab.backendapi.core.response.folderlistener.FileHandlerCallback;
import com.epam.dlab.backendapi.core.response.folderlistener.FolderListenerExecutor;
import com.epam.dlab.backendapi.resources.handler.ResourceCallbackHandler;
import com.epam.dlab.client.restclient.RESTService;
import com.epam.dlab.dto.exploratory.ExploratoryActionDTO;
import com.epam.dlab.dto.exploratory.ExploratoryBaseDTO;
import com.epam.dlab.dto.exploratory.ExploratoryCreateDTO;
import com.epam.dlab.dto.exploratory.ExploratoryStatusDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

import static com.epam.dlab.registry.ApiCallbacks.STATUS_URI;
import static com.epam.dlab.registry.ApiCallbacks.EXPLORATORY;

@Path("/exploratory")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ExploratoryResource implements DockerCommands {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExploratoryResource.class);
    private static final String EXPLORATORY_NAME_FIELD = "Notebook_name";

    @Inject
    private ProvisioningServiceApplicationConfiguration configuration;
    @Inject
    private FolderListenerExecutor folderListenerExecutor;
    @Inject
    private CommandExecutor commandExecuter;
    @Inject
    private CommandBuilder commandBuilder;
    @Inject
    private RESTService selfService;


    @Path("/create")
    @POST
    public String create(ExploratoryCreateDTO dto) throws IOException, InterruptedException {
        LOGGER.debug("create exploratory environment");
        String uuid = DockerCommands.generateUUID();
        folderListenerExecutor.start(configuration.getImagesDirectory(),
                configuration.getResourceStatusPollTimeout(),
                getFileHandlerCallback(dto.getNotebookUserName(), uuid, DockerAction.CREATE));
        commandExecuter.executeAsync(
                commandBuilder.buildCommand(
                        new RunDockerCommand()
                                .withDetached()
                                .withVolumeForRootKeys(configuration.getKeyDirectory())
                                .withVolumeForResponse(configuration.getImagesDirectory())
                                .withRequestId(uuid)
                                .withConfServiceBaseName(dto.getServiceBaseName())
                                .withNotebookUserName(dto.getNotebookUserName())
                                .withNotebookInstanceType(dto.getNotebookInstanceType())
                                .withCredsRegion(dto.getRegion())
                                .withCredsSecurityGroupsIds(dto.getSecurityGroupIds())
                                .withCredsKeyName(configuration.getAdminKey())
                                .withImage(configuration.getNotebookImage())
                                .withAction(DockerAction.CREATE)
                )
        );
        return uuid;
    }

    @Path("/start")
    @POST
    public String start(ExploratoryActionDTO dto) throws IOException, InterruptedException {
        return action(dto, DockerAction.START);
    }

    @Path("/terminate")
    @POST
    public String terminate(ExploratoryActionDTO dto) throws IOException, InterruptedException {
        return action(dto, DockerAction.TERMINATE);
    }

    @Path("/stop")
    @POST
    public String stop(ExploratoryActionDTO dto) throws IOException, InterruptedException {
        return action(dto, DockerAction.STOP);
    }

    private String action(ExploratoryBaseDTO dto, DockerAction action) throws IOException, InterruptedException {
        LOGGER.debug("{} exploratory environment", action);
        String uuid = DockerCommands.generateUUID();
        folderListenerExecutor.start(configuration.getImagesDirectory(),
                configuration.getResourceStatusPollTimeout(),
                getFileHandlerCallback(dto.getNotebookUserName(), uuid, action));
        commandExecuter.executeAsync(
                commandBuilder.buildCommand(
                        new RunDockerCommand()
                                .withInteractive()
                                .withVolumeForRootKeys(configuration.getKeyDirectory())
                                .withVolumeForResponse(configuration.getImagesDirectory())
                                .withRequestId(uuid)
                                .withCredsKeyName(configuration.getAdminKey())
                                .withImage(configuration.getNotebookImage())
                                .withAction(action),
                        dto
                )
        );
        return uuid;
    }

    private FileHandlerCallback getFileHandlerCallback(String user, String originalUuid, DockerAction action) {
        return new ResourceCallbackHandler<ExploratoryStatusDTO>(selfService, user, originalUuid, action) {
            @Override
            protected String getCallbackURI() {
                return EXPLORATORY+STATUS_URI;
            }

            @Override
            protected ExploratoryStatusDTO parseOutResponse(JsonNode resultNode, ExploratoryStatusDTO statusResult) {
                String userExploratoryName = resultNode.get(USER_EXPLORATORY_NAME_FIELD).textValue();
                String exploratoryName = resultNode.get(EXPLORATORY_NAME_FIELD).textValue();
                return statusResult
                        .withUserExploratoryName(userExploratoryName)
                        .withExploratoryName(exploratoryName);
            }
        };
    }

}
