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
import com.iexec.worker.chain.CredentialsService;
import com.iexec.worker.chain.IexecHubService;
import com.iexec.worker.chain.Web3jService;
import com.iexec.worker.feign.client.BlockchainAdapterClient;
import com.iexec.worker.feign.client.CoreClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.web3j.protocol.Web3j;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@ActiveProfiles("itest")
@SpringBootTest
@AutoConfigureWireMock(port = CommonTestSetup.WIREMOCK_SERVER_PORT)
public class CommonTestSetup {

    public static final int WIREMOCK_SERVER_PORT = 5555;
    public static final String WIREMOCK_SERVER_URL = "http://localhost:" + WIREMOCK_SERVER_PORT;

    // @Autowired
    // private WireMockServer wireMockServer;

    @Spy
    @Autowired
    private CredentialsService credentialsService;

    @Spy
    @Autowired
    private CoreClient coreClient;

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

        // stubFor(
        //     get(urlEqualTo("/todos"))
        //         .willReturn(aResponse()
        //             // .withHeader("Content-Type", "text/plain")
        //             .withBody("{\"id\": \"55\"}")));

        stubFor(
                get(urlPathEqualTo("/workers/config"))
                        .willReturn(WireMock.ok().withBody("{}"))
        );

        // WireMock.stubFor(
        //         WireMock.get(WireMock.urlEqualTo("/workers/config"))
        //                 .willReturn(
        //                         WireMock.aResponse()
        //                                 .withStatus(HttpStatus.OK.value())
        //                                 // .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        //                                 .withBody(
        //     copyToString(
        //       BookMocks.class.getClassLoader().getResourceAsStream("payload/get-books-response.json"),
        //       defaultCharset()))));

        // wireMockServer.stubFor(WireMock.urlPathEqualTo("/everything")
        // // .withHeader("Accept", containing("xml"))
        // // .withCookie("session", matching(".*12345.*"))
        // // .withQueryParam("search_term", equalTo("WireMock"))
        // // .withBasicAuth("jeff@example.com", "jeffteenjefftyjeff")
        // // .withRequestBody(equalToXml("<search-results />"))
        // // .withRequestBody(matchingXPath("//search-results"))
        // // .withMultipartRequestBody(
        // //     aMultipart()
        // //         .withName("info")
        // //         .withHeader("Content-Type", containing("charset"))
        // //         .withBody(equalToJson("{}"))
        // // )
        // .willReturn(WireMock.aResponse()));
    }

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        // wireMockServer.stubFor(
        //     WireMock.get(WireMock.urlPathEqualTo("/workers/config"))
        //             .willReturn(WireMock.ok().withBody("{}"))
        // );
    }

    @AfterAll
    static void afterAll() {}

    // private void mockAll() {
    //     // Get public configuration from core
    //     PublicConfiguration publicConfigurationMock = mock(PublicConfiguration.class);
    //     // when(customCoreFeignClient.getPublicConfiguration()).thenReturn(publicConfigurationMock);
    //     doReturn(publicConfigurationMock).when(coreClient).getPublicConfiguration();
    //     // Connection to chain node
    //     // when(web3jService.getWeb3j(anyBoolean())).thenReturn(web3jMock);
    //     when(web3jService.isBlockchainNodeReachable()).thenReturn(true);
    //     when(web3jService.getWeb3ClientVersion()).thenReturn("x.x.x-mock-chain-version");
    //     // core version
    //     when(customCoreFeignClient.getCoreVersion()).thenReturn("x.x.x-mock-core-version");
    //     // login
    //     String walletAddress = credentialsService.getCredentials().getAddress();
    //     String coreChallengeMock = "coreChallengeMock";
    //     // when(customCoreFeignClient.getChallenge(walletAddress)).thenReturn(coreChallengeMock);
    //     Signature signatureMock = mock(Signature.class);
    //     String jwtMock = "jwtMock";
    //     when(coreClient.login(walletAddress, signatureMock)).thenReturn(ResponseEntity.ok(jwtMock));
    //     // Register worker
    //     when(coreClient.registerWorker(eq(jwtMock), any())).thenReturn(ResponseEntity.ok().build());
    //     // Get public chain configuration
    //     PublicChainConfig publicChainConfigMock = mock(PublicChainConfig.class);
    //     when(blockchainAdapterClient.getPublicChainConfig()).thenReturn(ResponseEntity.ok(publicChainConfigMock));
    //     // ping
    //     String sessionIdMock = "sessionIdMock";
    //     when(coreClient.ping(eq(jwtMock))).thenReturn(ResponseEntity.ok(sessionIdMock));
    //     // hasEnoughGas
    //     when(iexecHubService.hasEnoughGas()).thenReturn(true);
    //     // Recover replicates
    //     long blockNumber = 777;
    //     when(coreClient.getMissedTaskNotifications(jwtMock, blockNumber))
    //             .thenReturn(ResponseEntity.ok(List.of()));
    //     // Get available replicate        
    // } 
}
