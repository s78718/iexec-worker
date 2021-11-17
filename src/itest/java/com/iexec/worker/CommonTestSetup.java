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

package com.iexec.worker;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.iexec.common.security.Signature;
import com.iexec.worker.chain.CredentialsService;
import com.iexec.worker.chain.IexecHubService;
import com.iexec.worker.chain.Web3jService;
import com.iexec.worker.feign.client.CoreClient;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.web3j.protocol.Web3j;

import java.util.List;

public class CommonTestSetup {

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

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        // hasEnoughGas
        when(iexecHubService.hasEnoughGas()).thenReturn(true);
        // Connection to chain node
        when(web3jService.getWeb3j(anyBoolean())).thenReturn(web3jMock);
        // when(web3jMock.)
        // login
        String walletAddress = credentialsService.getCredentials().getAddress();
        when(coreClient.getChallenge(walletAddress)).thenReturn(ResponseEntity.ok("challenge"));
        Signature mockSignature = mock(Signature.class);
        String mockJwt = "jwt";
        when(coreClient.login(walletAddress, mockSignature)).thenReturn(ResponseEntity.ok(mockJwt));
        // Register worker
        when(coreClient.registerWorker(eq(mockJwt), any())).thenReturn(ResponseEntity.ok().build());
        // Recover replicates
        long blockNumber = 777;
        when(coreClient.getMissedTaskNotifications(mockJwt, blockNumber))
                .thenReturn(ResponseEntity.ok(List.of()));
    }
}
