package com.iexec.worker.feign;

import com.iexec.common.chain.ChainReceipt;
import com.iexec.common.chain.ContributionAuthorization;
import com.iexec.common.config.PublicConfiguration;
import com.iexec.common.config.WorkerConfigurationModel;
import com.iexec.common.replicate.ReplicateDetails;
import com.iexec.common.replicate.ReplicateStatus;
import com.iexec.common.security.Signature;
import com.iexec.common.utils.SignatureUtils;
import com.iexec.worker.chain.CredentialsService;
import com.iexec.worker.config.CoreConfigurationService;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.web3j.crypto.ECKeyPair;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class CustomFeignClient {


    private static final int RETRY_TIME = 5000;
    private static final String TOKEN_PREFIX = "Bearer ";
    private final String url;
    private CoreWorkerClient coreWorkerClient;
    private CoreTaskClient coreTaskClient;
    private CredentialsService credentialsService;
    private String currentToken;

    public CustomFeignClient(CoreWorkerClient coreWorkerClient,
                             CoreTaskClient coreTaskClient,
                             CoreConfigurationService coreConfigurationService,
                             CredentialsService credentialsService) {
        this.credentialsService = credentialsService;
        this.coreWorkerClient = coreWorkerClient;
        this.coreTaskClient = coreTaskClient;
        this.url = coreConfigurationService.getUrl();
        this.currentToken = "";
    }

    public PublicConfiguration getPublicConfiguration() {
        try {
            return coreWorkerClient.getPublicConfiguration();
        } catch (FeignException e) {
            if (e.status() == 0) {
                log.error("Failed to getPublicConfiguration, will retry");
                sleep();
                return getPublicConfiguration();
            }
        }
        return null;
    }

    public String getCoreVersion() {
        try {
            return coreWorkerClient.getCoreVersion();
        } catch (FeignException e) {
            if (e.status() == 0) {
                log.error("Failed to getCoreVersion, will retry");
                sleep();
                return getCoreVersion();
            }
        }
        return null;
    }

    public String ping() {
        try {
            return coreWorkerClient.ping(getToken());
        } catch (FeignException e) {
            if (e.status() == 0) {
                log.error("Failed to ping [instance:{}]", url);
            } else if (HttpStatus.valueOf(e.status()).equals(HttpStatus.UNAUTHORIZED)) {
                generateNewToken();
                return coreWorkerClient.ping(getToken());
            }
        }

        return "";
    }

    public void registerWorker(WorkerConfigurationModel model) {
        try {
            coreWorkerClient.registerWorker(getToken(), model);
        } catch (FeignException e) {
            if (e.status() == 0) {
                log.error("Failed to registerWorker, will retry [instance:{}]", url);
                sleep();
                registerWorker(model);
            } else if (HttpStatus.valueOf(e.status()).equals(HttpStatus.UNAUTHORIZED)) {
                generateNewToken();
                coreWorkerClient.registerWorker(getToken(), model);
            }
        }
    }

    public List<String> getTasksInProgress(){
        try {
            return coreWorkerClient.getCurrentTasks(getToken());
        } catch (FeignException e) {
            if (e.status() == 0) {
                log.error("Failed to get tasks in progress, will retry [instance:{}]", url);
                sleep();
            } else if (HttpStatus.valueOf(e.status()).equals(HttpStatus.UNAUTHORIZED)) {
                generateNewToken();
                return coreWorkerClient.getCurrentTasks(getToken());
            }
        }

        return Collections.emptyList();
    }

    public ContributionAuthorization getAvailableReplicate(long lastAvailableBlockNumber) {
        try {
            return coreTaskClient.getAvailableReplicate(lastAvailableBlockNumber, getToken());
        } catch (FeignException e) {
            if (e.status() == 0) {
                log.error("Failed to getAvailableReplicate [instance:{}]", url);
            } else if (HttpStatus.valueOf(e.status()).equals(HttpStatus.UNAUTHORIZED)) {
                generateNewToken();
                return coreTaskClient.getAvailableReplicate(lastAvailableBlockNumber, getToken());
            }
        }
        return null;
    }

    // TODO: those next 4 methods need to be refactored
    public void updateReplicateStatus(String chainTaskId, ReplicateStatus status) {
        updateReplicateStatus(chainTaskId, status, null, "");
    }

    public void updateReplicateStatus(String chainTaskId, ReplicateStatus status, String resultLink) {
        updateReplicateStatus(chainTaskId, status, null, resultLink);
    }

    public void updateReplicateStatus(String chainTaskId, ReplicateStatus status, ChainReceipt chainReceipt) {
        updateReplicateStatus(chainTaskId, status, chainReceipt, "");
    }

    public void updateReplicateStatus(String chainTaskId, ReplicateStatus status, ChainReceipt chainReceipt, String resultLink) {
        log.info(status.toString() + " [chainTaskId:{}]", chainTaskId);

        ReplicateDetails details = ReplicateDetails.builder()
                .chainReceipt(chainReceipt)
                .resultLink(resultLink)
                .build();

        try {
            coreTaskClient.updateReplicateStatus(chainTaskId, status, getToken(), details);
        } catch (FeignException e) {
            if (e.status() == 0) {
                log.error("Failed to updateReplicateStatus, will retry [instance:{}]", url);
                sleep();
                updateReplicateStatus(chainTaskId, status, chainReceipt, resultLink);
                return;
            }

            if (HttpStatus.valueOf(e.status()).equals(HttpStatus.UNAUTHORIZED)) {
                generateNewToken();
                log.info(status.toString() + " [chainTaskId:{}]", chainTaskId);
                coreTaskClient.updateReplicateStatus(chainTaskId, status, getToken(), details);
            }
        }
    }

    private String getChallenge(String workerAddress) {
        try {
            return coreWorkerClient.getChallenge(workerAddress);
        } catch (FeignException e) {
            if (e.status() == 0) {
                log.error("Failed to getChallenge, will retry [instance:{}]", url);
                sleep();
                return getChallenge(workerAddress);
            }
        }
        return null;
    }

    private String login(String workerAddress, Signature signature) {
        try {
            return coreWorkerClient.login(workerAddress, signature);
        } catch (FeignException e) {
            if (e.status() == 0) {
                log.error("Failed to login, will retry [instance:{}]", url);
                sleep();
                return login(workerAddress, signature);
            }
        }
        return null;
    }

    private void sleep() {
        try {
            Thread.sleep(RETRY_TIME);
        } catch (InterruptedException e) {
        }
    }

    private String getToken() {
        if (currentToken.isEmpty()) {
            String workerAddress = credentialsService.getCredentials().getAddress();
            ECKeyPair ecKeyPair = credentialsService.getCredentials().getEcKeyPair();
            String challenge = getChallenge(workerAddress);

            Signature signature = SignatureUtils.hashAndSign(challenge, workerAddress, ecKeyPair);
            currentToken = TOKEN_PREFIX + login(workerAddress, signature);
        }

        return currentToken;
    }

    private void expireToken() {
        currentToken = "";
    }

    private String generateNewToken() {
        expireToken();
        return getToken();
    }

}
