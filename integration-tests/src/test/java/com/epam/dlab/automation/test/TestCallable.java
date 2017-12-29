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

package com.epam.dlab.automation.test;

import static org.testng.Assert.fail;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import com.epam.dlab.automation.test.libs.TestLibGroupStep;
import com.epam.dlab.automation.test.libs.TestLibInstallStep;
import com.epam.dlab.automation.test.libs.TestLibListStep;
import com.epam.dlab.automation.test.libs.models.Lib;
import com.epam.dlab.automation.test.libs.models.LibToSearchData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import com.epam.dlab.automation.aws.AmazonHelper;
import com.epam.dlab.automation.aws.AmazonInstanceState;
import com.epam.dlab.automation.docker.Docker;
import com.epam.dlab.automation.helper.ConfigPropertyValue;
import com.epam.dlab.automation.helper.PropertiesResolver;
import com.epam.dlab.automation.helper.NamingHelper;
import com.epam.dlab.automation.helper.WaitForStatus;
import com.epam.dlab.automation.http.ApiPath;
import com.epam.dlab.automation.http.ContentType;
import com.epam.dlab.automation.http.HttpRequest;
import com.epam.dlab.automation.http.HttpStatusCode;
import com.epam.dlab.automation.model.CreateNotebookDto;
import com.epam.dlab.automation.model.DeployClusterDto;
import com.epam.dlab.automation.model.DeployEMRDto;
import com.epam.dlab.automation.model.DeploySparkDto;
import com.epam.dlab.automation.model.JsonMapperDto;
import com.epam.dlab.automation.model.NotebookConfig;
import com.jayway.restassured.response.Response;

public class TestCallable implements Callable<Boolean> {
    private final static Logger LOGGER = LogManager.getLogger(TestCallable.class);

    private final String notebookTemplate;
    private final boolean fullTest;
    private final String token, ssnExpEnvURL, ssnProUserResURL,ssnCompResURL;
    private final String bucketName;
    private final String notebookName, clusterName, dataEngineType;
    private final NotebookConfig notebookConfig;

    public TestCallable(NotebookConfig notebookConfig) {
    	this.notebookTemplate = notebookConfig.getNotebookTemplate();
    	this.dataEngineType = notebookConfig.getDataEngineType();
        this.fullTest = notebookConfig.isFullTest();

        this.notebookConfig=notebookConfig;
        
        this.token = NamingHelper.getSsnToken();
        this.ssnExpEnvURL = NamingHelper.getSelfServiceURL(ApiPath.EXP_ENVIRONMENT);
        this.ssnProUserResURL = NamingHelper.getSelfServiceURL(ApiPath.PROVISIONED_RES);
        this.bucketName = NamingHelper.getBucketName();

        final String suffixName = NamingHelper.generateRandomValue(notebookTemplate);
        notebookName = "nb" + suffixName;
        
        if ("dataengine".equals(dataEngineType)) {
        	this.ssnCompResURL=NamingHelper.getSelfServiceURL(ApiPath.COMPUTATIONAL_RES_SPARK);
			clusterName = "spark" + suffixName;
        } else if ("dataengine-service".equals(dataEngineType)) {
        	this.ssnCompResURL=NamingHelper.getSelfServiceURL(ApiPath.COMPUTATIONAL_RES);
			clusterName = "eimr" + suffixName;
        } else {
        	ssnCompResURL="";
			clusterName="";
        	LOGGER.error("illegal argument dataEngineType {} , should be dataengine or dataengine-service", dataEngineType);
			fail("illegal argument dataEngineType "+dataEngineType +" , should be dataengine or dataengine-service");
        }

        LOGGER.info("   SSN exploratory environment URL is {}", ssnExpEnvURL);
        LOGGER.info("   SSN provisioned user resources URL is {}", ssnProUserResURL);
    }

    private static Duration getDuration(String duration) {
    	return Duration.parse("PT" + duration);
    }

	@Override
    public Boolean call() throws Exception {
        
		try {
			final String notebookIp = "172.31.56.227";//createNotebook(notebookName);
			//testLibs();

			//final DeployClusterDto deployClusterDto = createClusterDto();

			final String actualClusterName = NamingHelper.getClusterName(
					NamingHelper.getClusterInstanceNameForTestEmr(notebookName, clusterName, dataEngineType),
					dataEngineType);

			if (!ConfigPropertyValue.isRunModeLocal()) {
				TestEmr test = new TestEmr();
				test.run(notebookName, actualClusterName);

				String notebookFilesLocation = PropertiesResolver.getPropertyByName(
						String.format(PropertiesResolver.NOTEBOOK_FILES_LOCATION_PROPERTY_TEMPLATE, notebookTemplate));
				test.run2(NamingHelper.getSsnIp(), notebookIp, actualClusterName, new File(notebookFilesLocation),
						notebookName);
			}

			stopEnvironment();

			if (fullTest) {
				//restartNotebookAndRedeployToTerminate(deployClusterDto);
			}
			//if (deployClusterDto != null) {
			//	terminateNotebook(deployClusterDto);
			//}

			// Create notebook from AMI
			//String notebookNewName = "AMI" + notebookName;
			//createNotebook(notebookNewName);

			//terminateNotebook(notebookNewName);

			LOGGER.info("{} All tests finished successfully", notebookName);
			return true;
		} catch (AssertionError | Exception e) {
			LOGGER.error("Error occurred while testing notebook {} with configuration {}", notebookName, notebookConfig, e);
			throw e;
		}
   }
    

  

private DeployClusterDto createClusterDto() throws Exception {
	String gettingStatus;
    LOGGER.info("7. {} cluster {} will be deployed for {} ...",dataEngineType, clusterName, notebookName);
    LOGGER.info("  {} : SSN computational resources URL is {}", notebookName, ssnCompResURL);

    DeployClusterDto clusterDto = null;
    if ("dataengine".equals(dataEngineType)) {
		clusterDto = JsonMapperDto.readNode(
					Paths.get(String.format("%s/%s", PropertiesResolver.getClusterConfFileLocation(), notebookTemplate), "spark_cluster.json").toString(),
					DeploySparkDto.class);
    } else if ("dataengine-service".equals(dataEngineType)) {
		clusterDto = JsonMapperDto.readNode(
					Paths.get(String.format("%s/%s", PropertiesResolver.getClusterConfFileLocation(), notebookTemplate), "EMR.json").toString(),
					DeployEMRDto.class);
    } else {
		LOGGER.error("illegal argument dataEngineType {} , should be dataengine or dataengine-service", dataEngineType);
		fail("illegal argument dataEngineType "+dataEngineType +" , should be dataengine or dataengine-service");
	}

    clusterDto.setName(clusterName);
    clusterDto.setNotebook_name(notebookName);
    LOGGER.info("{}: {} cluster = {}",notebookName,dataEngineType, clusterDto);
    Response responseDeployingCluster = new HttpRequest().webApiPut(ssnCompResURL, ContentType.JSON,
    		clusterDto, token);
    LOGGER.info("{}:   responseDeployingCluster.getBody() is {}",notebookName, responseDeployingCluster.getBody().asString());
    Assert.assertEquals(responseDeployingCluster.statusCode(), HttpStatusCode.OK, dataEngineType + " cluster " + clusterName + " was not deployed");

    gettingStatus = WaitForStatus.cluster(ssnProUserResURL, token, notebookName, clusterName, "creating", getDuration(notebookConfig.getTimeoutClusterCreate()));
    if(!ConfigPropertyValue.isRunModeLocal()) {
        if (!(gettingStatus.contains("configuring") || gettingStatus.contains("running")))
            throw new Exception(notebookName + ": " + dataEngineType + " cluster " + clusterName + " has not been deployed. Cluster status is " + gettingStatus);
        LOGGER.info("{}: {} cluster {} has been deployed", notebookName, dataEngineType, clusterName);

        AmazonHelper.checkAmazonStatus(NamingHelper.getClusterInstanceName(notebookName, clusterName, dataEngineType), AmazonInstanceState.RUNNING);
        Docker.checkDockerStatus(NamingHelper.getClusterContainerName(clusterName, "create"), NamingHelper.getSsnIp());
    }
    LOGGER.info("{}:   Waiting until {} cluster {} has been configured ...", notebookName,dataEngineType,clusterName);

    gettingStatus = WaitForStatus.cluster(ssnProUserResURL, token, notebookName, clusterName, "configuring", getDuration(notebookConfig.getTimeoutClusterCreate()));
    if (!gettingStatus.contains("running"))
        throw new Exception(notebookName + ": " + dataEngineType + " cluster " + clusterName + " has not been configured. Spark cluster status is " + gettingStatus);
    LOGGER.info(" {}: {} cluster {} has been configured", notebookName, dataEngineType , clusterName);

    if(!ConfigPropertyValue.isRunModeLocal()) {
        AmazonHelper.checkAmazonStatus(NamingHelper.getClusterInstanceName(notebookName, clusterName, dataEngineType), AmazonInstanceState.RUNNING);
        Docker.checkDockerStatus(NamingHelper.getClusterContainerName(clusterName, "create"), NamingHelper.getSsnIp());
    }

    LOGGER.info("{}:   Check bucket {}", notebookName, bucketName);
    AmazonHelper.printBucketGrants(bucketName);

    return clusterDto;
}

private String  createNotebook(String notebookName) throws Exception {
       LOGGER.info("6. Notebook {} will be created ...", notebookName);
       String notebookConfigurationFile = String.format(PropertiesResolver.NOTEBOOK_CONFIGURATION_FILE_TEMPLATE, notebookTemplate, notebookTemplate);
       LOGGER.info("{} notebook configuration file: {}", notebookName, notebookConfigurationFile);

       CreateNotebookDto createNoteBookRequest =
               JsonMapperDto.readNode(
                       Paths.get(PropertiesResolver.getClusterConfFileLocation(), notebookConfigurationFile).toString(),
                       CreateNotebookDto.class);

       createNoteBookRequest.setName(notebookName);

       Response responseCreateNotebook = new HttpRequest().webApiPut(ssnExpEnvURL, ContentType.JSON,
                   createNoteBookRequest, token);
       LOGGER.info(" {}:  responseCreateNotebook.getBody() is {}", notebookName, responseCreateNotebook.getBody().asString());
       Assert.assertEquals(responseCreateNotebook.statusCode(), HttpStatusCode.OK, "Notebook " + notebookName + " was not created");

       String gettingStatus = WaitForStatus.notebook(ssnProUserResURL, token, notebookName, "creating", getDuration(notebookConfig.getTimeoutNotebookCreate()));
       if (!gettingStatus.contains("running")) {
           LOGGER.error("Notebook {} is in state {}", notebookName, gettingStatus);
           throw new Exception("Notebook " + notebookName + " has not been created. Notebook status is " + gettingStatus);
       }
       LOGGER.info("   Notebook {} has been created", notebookName);

       AmazonHelper.checkAmazonStatus(NamingHelper.getNotebookInstanceName(notebookName), AmazonInstanceState.RUNNING);
       Docker.checkDockerStatus(NamingHelper.getNotebookContainerName(notebookName, "create"), NamingHelper.getSsnIp());

       LOGGER.info("   Notebook {} status has been verified", notebookName);
       //get notebook IP
       String notebookIp = AmazonHelper.getInstance(NamingHelper.getNotebookInstanceName(notebookName))
    		   .getPrivateIpAddress();
       LOGGER.info("   Notebook {} IP is {}", notebookName, notebookIp);

       return notebookIp;
   }

   private void testLibs() throws Exception {
       LOGGER.info("Install libraries  ...", notebookName);

       TestLibGroupStep testLibGroupStep = new TestLibGroupStep(ApiPath.LIB_GROUPS, token, notebookName,
               getDuration(notebookConfig.getTimeoutLibGroups()).getSeconds(),
               getTemplateTestLibFile("lib_groups.json"));

       testLibGroupStep.init();
       testLibGroupStep.verify();

       List<LibToSearchData> libToSearchDataList = JsonMapperDto.readListOf(getTemplateTestLibFile("lib_list.json"),
               LibToSearchData.class);

       for (LibToSearchData libToSearchData : libToSearchDataList) {
           TestLibListStep testLibListStep = new TestLibListStep(ApiPath.LIB_LIST, token, notebookName,
        		   getDuration(notebookConfig.getTimeoutLibList()).getSeconds(), libToSearchData);

           testLibListStep.init();
           testLibListStep.verify();

           Lib lib = testLibListStep.getLibs().get(new Random().nextInt(testLibListStep.getLibs().size()));

           TestLibInstallStep testLibInstallStep = new TestLibInstallStep(ApiPath.LIB_INSTALL, ApiPath.LIB_LIST_EXPLORATORY_FORMATTED,
                   token, notebookName, getDuration(notebookConfig.getTimeoutLibInstall()).getSeconds(), lib);

           testLibInstallStep.init();
           testLibInstallStep.verify();
       }
   }

   private String getTemplateTestLibFile(String fileName) {
        String absoluteFileName;
        if (PropertiesResolver.DEV_MODE) {
            absoluteFileName = Paths.get(PropertiesResolver.getNotebookTestLibLocation(), fileName).toString();
        } else {
            absoluteFileName = Paths.get(PropertiesResolver.getNotebookTestLibLocation(), notebookTemplate,
                    fileName).toString();
        }
        LOGGER.info("Absolute file name is {}", absoluteFileName);
        return absoluteFileName;
   }

   private void restartNotebookAndRedeployToTerminate(DeployClusterDto deployClusterDto) throws Exception {
	   restartNotebook();
	   final String clusterNewName = redeployCluster(deployClusterDto);
	   terminateCluster(clusterNewName);
   }
   

private void restartNotebook() throws Exception {
       LOGGER.info("9. Notebook {} will be re-started ...", notebookName);
       String requestBody = "{\"notebook_instance_name\":\"" + notebookName + "\"}";
       Response respStartNotebook = new HttpRequest().webApiPost(ssnExpEnvURL, ContentType.JSON, requestBody, token);
       LOGGER.info("    respStartNotebook.getBody() is {}", respStartNotebook.getBody().asString());
       Assert.assertEquals(respStartNotebook.statusCode(), HttpStatusCode.OK);

       String gettingStatus = WaitForStatus.notebook(ssnProUserResURL, token, notebookName, "starting", getDuration(notebookConfig.getTimeoutNotebookStartup()));
       if (!gettingStatus.contains(AmazonInstanceState.RUNNING.toString())){
           throw new Exception("Notebook " + notebookName + " has not been started. Notebook status is " + gettingStatus);
       }
       LOGGER.info("    Notebook {} has been started", notebookName);

       AmazonHelper.checkAmazonStatus(NamingHelper.getNotebookInstanceName(notebookName), AmazonInstanceState.RUNNING);
       Docker.checkDockerStatus(NamingHelper.getNotebookContainerName(notebookName, "start"), NamingHelper.getSsnIp());
   }

   private void terminateNotebook(String notebookName) throws Exception {
       String gettingStatus;
       LOGGER.info("12. Notebook {} will be terminated ...", notebookName);
       final String ssnTerminateNotebookURL = NamingHelper.getSelfServiceURL(ApiPath.getTerminateNotebookUrl(notebookName));
       Response respTerminateNotebook = new HttpRequest().webApiDelete(ssnTerminateNotebookURL, ContentType.JSON, token);
       LOGGER.info("    respTerminateNotebook.getBody() is {}", respTerminateNotebook.getBody().asString());
       Assert.assertEquals(respTerminateNotebook.statusCode(), HttpStatusCode.OK);

       gettingStatus = WaitForStatus.notebook(ssnProUserResURL, token, notebookName, "terminating", getDuration(notebookConfig.getTimeoutClusterTerminate()));
       if (!gettingStatus.contains("terminated"))
           throw new Exception("Notebook" + notebookName + " has not been terminated. Notebook status is " + gettingStatus);
       AmazonHelper.checkAmazonStatus(NamingHelper.getNotebookInstanceName(notebookName), AmazonInstanceState.TERMINATED);
       Docker.checkDockerStatus(NamingHelper.getNotebookContainerName(notebookName, "terminate"), NamingHelper.getSsnIp());
   }

   private void terminateNotebook(DeployClusterDto deployCluster) throws Exception {
       terminateNotebook(deployCluster.getNotebook_name());

       String gettingStatus = WaitForStatus.getClusterStatus(
				new HttpRequest()
					.webApiGet(ssnProUserResURL, token)
					.getBody()
					.jsonPath(),
					deployCluster.getNotebook_name(), deployCluster.getName());
       if (!gettingStatus.contains("terminated"))
           throw new Exception(dataEngineType+" cluster "+ deployCluster.getName() + " has not been terminated for Notebook " + deployCluster.getNotebook_name() + ". Cluster status is " + gettingStatus);
       LOGGER.info("    {} cluster {} has been terminated for Notebook {}",dataEngineType, deployCluster.getName(), deployCluster.getNotebook_name());

       AmazonHelper.checkAmazonStatus(NamingHelper.getClusterInstanceName(deployCluster.getNotebook_name(), deployCluster.getName(), dataEngineType), AmazonInstanceState.TERMINATED);
   }
   
   private void terminateCluster(String clusterNewName) throws Exception {
       String gettingStatus;
       LOGGER.info("    New cluster {} will be terminated for notebook {} ...", clusterNewName, notebookName);
       final String ssnTerminateClusterURL = NamingHelper.getSelfServiceURL(ApiPath.getTerminateClusterUrl(notebookName, clusterNewName));
       LOGGER.info("    SSN terminate cluster URL is {}", ssnTerminateClusterURL);

       Response respTerminateCluster = new HttpRequest().webApiDelete(ssnTerminateClusterURL, ContentType.JSON, token);
       LOGGER.info("    respTerminateCluster.getBody() is {}", respTerminateCluster.getBody().asString());
       Assert.assertEquals(respTerminateCluster.statusCode(), HttpStatusCode.OK);

       gettingStatus = WaitForStatus.cluster(ssnProUserResURL, token, notebookName, clusterNewName, "terminating", getDuration(notebookConfig.getTimeoutClusterTerminate()));
       if (!gettingStatus.contains("terminated"))
           throw new Exception("New "+dataEngineType+" cluster " + clusterNewName + " has not been terminated. Cluster status is " + gettingStatus);
       LOGGER.info("    New {} cluster {} has been terminated for notebook {}",dataEngineType, clusterNewName, notebookName);

       AmazonHelper.checkAmazonStatus(NamingHelper.getClusterInstanceName(notebookName, clusterNewName, dataEngineType), AmazonInstanceState.TERMINATED);
       Docker.checkDockerStatus(NamingHelper.getClusterContainerName(clusterNewName, "terminate"), NamingHelper.getSsnIp());
   }

   private String redeployCluster(DeployClusterDto deployCluster) throws Exception {
       final String clusterNewName = "New" + clusterName;
       String gettingStatus;

       LOGGER.info("10. New {} cluster {} will be deployed for termination for notebook {} ...",dataEngineType, clusterNewName, notebookName);

       deployCluster.setName(clusterNewName);
       deployCluster.setNotebook_name(notebookName);
       Response responseDeployingClusterNew = new HttpRequest().webApiPut(ssnCompResURL, ContentType.JSON, deployCluster, token);
       LOGGER.info("    responseDeployingClusterNew.getBody() is {}", responseDeployingClusterNew.getBody().asString());
       Assert.assertEquals(responseDeployingClusterNew.statusCode(), HttpStatusCode.OK);

       gettingStatus = WaitForStatus.cluster(ssnProUserResURL, token, notebookName, clusterNewName, "creating", getDuration(notebookConfig.getTimeoutClusterCreate()));
       if (!(gettingStatus.contains("configuring") || gettingStatus.contains("running")))
           throw new Exception("New cluster " + clusterNewName + " has not been deployed. Cluster status is " + gettingStatus);
       LOGGER.info("    New cluster {} has been deployed", clusterNewName);

       LOGGER.info("   Waiting until cluster {} has been configured ...", clusterNewName);
       gettingStatus = WaitForStatus.cluster(ssnProUserResURL, token, notebookName, clusterNewName, "configuring", getDuration(notebookConfig.getTimeoutClusterCreate()));
       if (!gettingStatus.contains(AmazonInstanceState.RUNNING.toString()))
           throw new Exception("Cluster " + clusterNewName + " has not been configured. Cluster status is " + gettingStatus);
       LOGGER.info("   Cluster {} has been configured", clusterNewName);

       AmazonHelper.checkAmazonStatus(NamingHelper.getClusterInstanceName(notebookName, clusterNewName, dataEngineType), AmazonInstanceState.RUNNING);
       Docker.checkDockerStatus(NamingHelper.getClusterContainerName(clusterNewName, "create"), NamingHelper.getSsnIp());
       return clusterNewName;
   }

   private void stopEnvironment() throws Exception {
       String gettingStatus;
       LOGGER.info("8. Notebook {} will be stopped ...", notebookName);
       final String ssnStopNotebookURL = NamingHelper.getSelfServiceURL(ApiPath.getStopNotebookUrl(notebookName));
       LOGGER.info("   SSN stop notebook URL is {}", ssnStopNotebookURL);

       Response responseStopNotebook = new HttpRequest().webApiDelete(ssnStopNotebookURL, ContentType.JSON, token);
       LOGGER.info("   responseStopNotebook.getBody() is {}", responseStopNotebook.getBody().asString());
       Assert.assertEquals(responseStopNotebook.statusCode(), HttpStatusCode.OK, "Notebook " + notebookName + " was not stopped");

       gettingStatus = WaitForStatus.notebook(ssnProUserResURL, token, notebookName, "stopping", getDuration(notebookConfig.getTimeoutNotebookShutdown()));
       if (!gettingStatus.contains("stopped"))
           throw new Exception("Notebook " + notebookName + " has not been stopped. Notebook status is " + gettingStatus);
       LOGGER.info("   Notebook {} has been stopped", notebookName);
       gettingStatus = WaitForStatus.getClusterStatus(
               new HttpRequest()
                       .webApiGet(ssnProUserResURL, token)
                       .getBody()
                       .jsonPath(),
               notebookName, clusterName);

       if (!gettingStatus.contains("terminated"))
           throw new Exception("Computational resources has not been terminated for Notebook " + notebookName + ". EMR status is " + gettingStatus);
       LOGGER.info("   Computational resources has been terminated for notebook {}", notebookName);

       AmazonHelper.checkAmazonStatus(NamingHelper.getClusterInstanceName(notebookName, clusterName, dataEngineType), AmazonInstanceState.TERMINATED);
       Docker.checkDockerStatus(NamingHelper.getNotebookContainerName(notebookName, "stop"), NamingHelper.getSsnIp());
   }
}
