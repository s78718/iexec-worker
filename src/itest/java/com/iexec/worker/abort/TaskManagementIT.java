/*
* Copyright 2020 IEXEC BLOCKCHAIN TECH
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.iexec.worker.abort;

import com.iexec.worker.CommonTestSetup;
import com.iexec.worker.chain.CredentialsService;
import com.iexec.worker.chain.IexecHubService;
import com.iexec.worker.chain.Web3jService;
import com.iexec.worker.feign.client.CoreClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.web3j.protocol.Web3j;

@SpringBootTest
public class TaskManagementIT extends CommonTestSetup {

    @Spy
    @Autowired
    private IexecHubService iexecHubService;

    @Spy
    @Autowired
    private Web3jService web3jService;

    @Spy
    @Autowired
    private CredentialsService credentialsService;

    @Spy
    @Autowired
    private CoreClient coreClient;

    @Mock
    private Web3j web3jMock;

    @Override
    public void setup() {
        super.setup();
    }

    @Test
    public void shouldProperlyAbortTask() {

        // Mock http calls
        // Provide task to worker.
        // Start
        // Abort
        // check resources
        System.out.println("hello");
    }
}
