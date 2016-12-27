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

package com.epam.dlab.backendapi.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** Stores info about the user notebook.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInstanceDTO {
    @JsonProperty("_id")
    private String id;
    @JsonProperty
    private String user;
    @JsonProperty("exploratory_name")
    private String exploratoryName;
    @JsonProperty("exploratory_id")
    private String exploratoryId;
    @JsonProperty
    private String status;
    @JsonProperty
    private String shape;
    @JsonProperty("exploratory_url")
    private String url;
    @JsonProperty("up_time")
    private Date uptime;
    @JsonProperty("computational_resources")
    private List<UserComputationalResourceDTO> resources = new ArrayList<>();

    /** Returns the unique id for the notebook. */
    public String getId() {
        return id;
    }

    /** Returns the user login name. */
    public String getUser() {
        return user;
    }

    /** Sets the user login name. */
    public void setUser(String user) {
        this.user = user;
    }

    /** Sets the user login name. */
    public UserInstanceDTO withUser(String user) {
        setUser(user);
        return this;
    }

    /** Returns the name of exploratory. */
    public String getExploratoryName() {
        return exploratoryName;
    }

    /** Sets the name of exploratory. */
    public void setExploratoryName(String exploratoryName) {
        this.exploratoryName = exploratoryName;
    }

    /** Sets the name of exploratory. */
    public UserInstanceDTO withExploratoryName(String exploratoryName) {
        setExploratoryName(exploratoryName);
        return this;
    }

    /** Returns the exploratory id. */
    public String getExploratoryId() {
        return exploratoryId;
    }

    /** Sets the exploratory id. */
    public void setExploratoryId(String exploratoryId) {
        this.exploratoryId = exploratoryId;
    }

    /** Sets the exploratory id. */
    public UserInstanceDTO withExploratoryId(String exploratoryId) {
        setExploratoryId(exploratoryId);
        return this;
    }

    /** Returns the status of notebook. */
    public String getStatus() {
        return status;
    }

    /** Sets the status of notebook. */
    public void setStatus(String status) {
        this.status = status;
    }

    /** Sets the status of notebook. */
    public UserInstanceDTO withStatus(String status) {
        setStatus(status);
        return this;
    }

    /** Returns the name of notebook shape. */
    public String getShape() {
        return shape;
    }

    /** Sets the name of notebook shape. */
    public void setShape(String shape) {
        this.shape = shape;
    }

    /** Sets the name of notebook shape. */
    public UserInstanceDTO withShape(String shape) {
        setShape(shape);
        return this;
    }

    /** Returns the URL of notebook. */
    public String getUrl() {
        return url;
    }

    /** Sets the URL of notebook. */
    public void setUrl(String url) {
        this.url = url;
    }

    /** Sets the URL of notebook. */
    public UserInstanceDTO withUrl(String url) {
        setUrl(url);
        return this;
    }

    /** Returns the date and time when the notebook has created. */
    public Date getUptime() {
        return uptime;
    }

    /** Sets the date and time when the notebook has created. */
    public void setUptime(Date uptime) {
        this.uptime = uptime;
    }

    /** Sets the date and time when the notebook has created. */
    public UserInstanceDTO withUptime(Date uptime) {
        setUptime(uptime);
        return this;
    }

    /** Returns a list of user's computational resources for notebook. */
    public List<UserComputationalResourceDTO> getResources() {
        return resources;
    }

    /** Sets a list of user's computational resources for notebook. */
    public void setResources(List<UserComputationalResourceDTO> resources) {
        this.resources = resources;
    }

    /** Sets a list of user's computational resources for notebook. */
    public UserInstanceDTO withResources(List<UserComputationalResourceDTO> resources) {
        setResources(resources);
        return this;
    }
}
