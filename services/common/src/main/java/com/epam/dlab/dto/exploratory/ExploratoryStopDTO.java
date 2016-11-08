/******************************************************************************************************

 Copyright (c) 2016 EPAM Systems Inc.

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 *****************************************************************************************************/

package com.epam.dlab.dto.exploratory;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExploratoryStopDTO extends ExploratoryActionDTO<ExploratoryStopDTO> {
    @JsonProperty("notebook_ssh_user")
    private String sshUser;
    @JsonProperty("creds_key_dir")
    private String keyDir;

    public String getSshUser() {
        return sshUser;
    }

    public void setSshUser(String sshUser) {
        this.sshUser = sshUser;
    }

    public ExploratoryStopDTO withSshUser(String sshUser) {
        setSshUser(sshUser);
        return this;
    }

    public String getKeyDir() {
        return keyDir;
    }

    public void setKeyDir(String keyDir) {
        this.keyDir = keyDir;
    }

    public ExploratoryStopDTO withKeyDir(String keyDir) {
        setKeyDir(keyDir);
        return this;
    }
}
