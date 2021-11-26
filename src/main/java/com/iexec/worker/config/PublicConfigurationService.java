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

package com.iexec.worker.config;

import com.iexec.common.config.PublicConfiguration;
import com.iexec.worker.feign.CustomCoreFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PublicConfigurationService {

    private final PublicConfiguration publicConfiguration;

    public PublicConfigurationService(CustomCoreFeignClient customCoreFeignClient) {
        log.info("Fetching configuration from core");
        this.publicConfiguration = customCoreFeignClient.getPublicConfiguration();
        log.info("Fetched configuration from core [config:{}]", publicConfiguration);
        // TODO validate received configuration
    }

    public PublicConfiguration getPublicConfiguration() {
        return publicConfiguration;
    }

    public String getBlockchainAdapterUrl() {
        return publicConfiguration.getBlockchainAdapterUrl();
    }

    public String getWorkerPoolAddress() {
        return publicConfiguration.getWorkerPoolAddress();
    }

    public String getSchedulerPublicAddress() {
        return publicConfiguration.getSchedulerPublicAddress();
    }

    public long getAskForReplicatePeriod() {
        return publicConfiguration.getAskForReplicatePeriod();
    }

    public String getResultRepositoryURL() {
        return publicConfiguration.getResultRepositoryURL();
    }

    public String getSmsURL() {
        return publicConfiguration.getSmsURL();
    }

    public String getRequiredWorkerVersion() {
        return publicConfiguration.getRequiredWorkerVersion();
    }
}
