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

package com.iexec.worker.mock;

import com.iexec.common.config.PublicConfiguration;
import com.iexec.worker.feign.CustomCoreFeignClient;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@Profile("itest")
@Configuration
// @Primary
// @Configuration("customCoreFeignClient") // override default bean
public class CustomCoreFeignClientMock {

    public CustomCoreFeignClientMock() {
        
    }
    @Bean
    @Primary
    public CustomCoreFeignClient coreClient() {
        CustomCoreFeignClient spiedBean = spy(CustomCoreFeignClient.class);
        MockitoAnnotations.openMocks(this);
        when(spiedBean.getPublicConfiguration()).thenReturn(
            PublicConfiguration.builder()
                    .workerPoolAddress("0xWorkerpool")
                    .blockchainAdapterUrl("http://adapter.url")
                    .schedulerPublicAddress("0xSchedulerAddress")
                    .resultRepositoryURL("http://result.url")
                    .smsURL("http://sms.url")
                    .askForReplicatePeriod(5000) // 5s
                    .requiredWorkerVersion("")
                    .build()
        );
        return spiedBean;
    }
}
