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

import com.github.tomakehurst.wiremock.client.WireMock;
import com.iexec.common.config.PublicConfiguration;
import com.iexec.worker.chain.CredentialsService;
import com.iexec.worker.chain.IexecHubService;
import com.iexec.worker.chain.Web3jService;
import com.iexec.worker.feign.CustomCoreFeignClient;
import com.iexec.worker.feign.client.BlockchainAdapterClient;
import com.iexec.worker.feign.client.CoreClient;
import net.bytebuddy.asm.Advice.This;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.web3j.protocol.Web3j;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.mockito.Mockito.when;

@ActiveProfiles("itest")
@SpringBootTest
// @AutoConfigureWireMock(port = CommonTestSetup.WIREMOCK_SERVER_PORT)
public class TestSetup {

    public static final int WIREMOCK_SERVER_PORT = 5555;
    public static final String WIREMOCK_SERVER_URL = "http://localhost:" + WIREMOCK_SERVER_PORT;

    // @Autowired
    // private WireMockServer wireMockServer;

    // @Autowired
    // private CustomCoreFeignClient customCoreFeignClient;

    @Spy
    @Autowired
    private CredentialsService credentialsService;

    @Spy
    @Autowired
    private BlockchainAdapterClient blockchainAdapterClient;

    @Spy
    @Autowired
    private IexecHubService iexecHubService;

    @Spy
    @Autowired
    private Web3jService web3jService;

    @Mock
    private Web3j web3jMock;

    @BeforeAll
    static void beforeAll() {
        System.out.println("######################################################");
        System.out.println("######################################################");
        System.out.println("BeforeAll");
        System.out.println("######################################################");
        System.out.println("######################################################");
    }

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterAll
    static void afterAll() {

    }
}
