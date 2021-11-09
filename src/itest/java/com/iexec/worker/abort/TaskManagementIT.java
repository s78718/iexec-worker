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

import com.iexec.worker.chain.IexecHubService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.when;

@SpringBootTest
public class TaskManagementIT {

    @Spy
    @Autowired
    private IexecHubService iexecHubService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        when(iexecHubService.hasEnoughGas()).thenReturn(true);
    }

    @Test
    public void shouldProperlyAbortTask() {

        // Mock http calls
        // Provide task to worker.
        // Start
        // Abort
        // check resources
    }
}
