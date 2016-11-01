/******************************************************************************************************

 Copyright (c) 2016 EPAM Systems Inc.

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 *****************************************************************************************************/

package com.epam.dlab.dto.computational;

import com.epam.dlab.dto.StatusBaseDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ComputationalStatusDTO extends StatusBaseDTO<ComputationalStatusDTO> {
    @JsonProperty("computational_name")
    private String computationalName;
    @JsonProperty("user_computational_name")
    private String userComputationalName;

    public String getComputationalName() {
        return computationalName;
    }

    public void setComputationalName(String computationalName) {
        this.computationalName = computationalName;
    }

    public ComputationalStatusDTO withComputationalName(String computationalName) {
        setComputationalName(computationalName);
        return this;
    }

    public String getUserComputationalName() {
        return userComputationalName;
    }

    public void setUserComputationalName(String userComputationalName) {
        this.userComputationalName = userComputationalName;
    }

    public ComputationalStatusDTO withUserComputationalName(String userComputationalName) {
        setUserComputationalName(userComputationalName);
        return this;
    }
}
