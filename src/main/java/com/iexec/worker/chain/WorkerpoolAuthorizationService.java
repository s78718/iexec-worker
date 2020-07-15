package com.iexec.worker.chain;

import com.iexec.common.chain.WorkerpoolAuthorization;
import com.iexec.common.utils.BytesUtils;
import com.iexec.common.utils.HashUtils;
import com.iexec.common.utils.SignatureUtils;
import com.iexec.worker.config.PublicConfigurationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Service
public class WorkerpoolAuthorizationService {

    private PublicConfigurationService publicConfigurationService;
    private Map<String, WorkerpoolAuthorization> workerpoolAuthorizations;
    private String corePublicAddress;

    public WorkerpoolAuthorizationService(PublicConfigurationService publicConfigurationService) {
        this.publicConfigurationService = publicConfigurationService;
    }

    @PostConstruct
    public void initIt() {
        corePublicAddress = publicConfigurationService.getSchedulerPublicAddress();
        workerpoolAuthorizations = new ConcurrentHashMap<>();
    }


    public boolean isWorkerpoolAuthorizationValid(WorkerpoolAuthorization auth, String signerAddress) {
        // create the hash that was used in the signature in the core
        byte[] message = BytesUtils.stringToBytes(
                HashUtils.concatenateAndHash(auth.getWorkerWallet(), auth.getChainTaskId(), auth.getEnclaveChallenge()));

        return SignatureUtils.isSignatureValid(message, auth.getSignature(), signerAddress);
    }

    public boolean putWorkerpoolAuthorization(WorkerpoolAuthorization workerpoolAuthorization) {
        if (workerpoolAuthorization == null || workerpoolAuthorization.getChainTaskId() == null) {
            log.error("Cant putWorkerpoolAuthorization (null) [workerpoolAuthorization:{}]", workerpoolAuthorization);
            return false;
        }

        if (!isWorkerpoolAuthorizationValid(workerpoolAuthorization, corePublicAddress)) {
            log.error("Cant putWorkerpoolAuthorization (invalid) [workerpoolAuthorization:{}]", workerpoolAuthorization);
            return false;
        }
        workerpoolAuthorizations.putIfAbsent(workerpoolAuthorization.getChainTaskId(), workerpoolAuthorization);
        return true;
    }

    WorkerpoolAuthorization getWorkerpoolAuthorization(String chainTaskId) {
        return workerpoolAuthorizations.get(chainTaskId);
    }
}
