package com.iexec.worker.replicate;

import com.iexec.common.chain.ContributionAuthorization;
import com.iexec.common.notification.TaskNotification;
import com.iexec.common.notification.TaskNotificationExtra;
import com.iexec.common.notification.TaskNotificationType;
import com.iexec.common.task.TaskDescription;
import com.iexec.worker.chain.IexecHubService;
import com.iexec.worker.feign.CustomFeignClient;
import com.iexec.worker.pubsub.SubscriptionService;
import com.iexec.worker.result.ResultService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


public class ReplicateRecoveryServiceTests {

    @Mock private CustomFeignClient customFeignClient;
    @Mock private ResultService resultService;
    @Mock private IexecHubService iexecHubService;
    @Mock private SubscriptionService subscriptionService;
    @Mock private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    ReplicateRecoveryService replicateRecoveryService;

    private final static String CHAIN_TASK_ID = "0xfoobar";
    long blockNumber = 5;


    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldNotRecoverSinceNothingToRecover() {
        when(iexecHubService.getLatestBlockNumber()).thenReturn(blockNumber);
        when(customFeignClient.getMissedTaskNotifications(blockNumber))
                .thenReturn(Collections.emptyList());

        List<String> recovered = replicateRecoveryService.recoverInterruptedReplicates();

        assertThat(recovered).isEmpty();
    }

    @Test
    public void shouldNotRecoverSinceCannotGetTaskDescriptionFromChain() {
        when(iexecHubService.getLatestBlockNumber()).thenReturn(blockNumber);
        TaskNotification notif = getStubInterruptedTask(TaskNotificationType.PLEASE_REVEAL);
        when(customFeignClient.getMissedTaskNotifications(blockNumber))
                .thenReturn(Collections.singletonList(notif));
        when(iexecHubService.getTaskDescriptionFromChain(CHAIN_TASK_ID)).thenReturn(Optional.empty());
        when(resultService.isResultAvailable(CHAIN_TASK_ID)).thenReturn(true);

        List<String> recovered = replicateRecoveryService.recoverInterruptedReplicates();

        assertThat(recovered).isEmpty();

        Mockito.verify(subscriptionService, Mockito.times(0))
                .subscribeToTopic(CHAIN_TASK_ID);
    }

    @Test
    public void shouldNotRecoverByRevealingWhenResultNotFound() {
        when(iexecHubService.getLatestBlockNumber()).thenReturn(blockNumber);
        TaskNotification notif = getStubInterruptedTask(TaskNotificationType.PLEASE_REVEAL);
        when(customFeignClient.getMissedTaskNotifications(blockNumber))
                .thenReturn(Collections.singletonList(notif));
        when(iexecHubService.getTaskDescriptionFromChain(any())).thenReturn(getStubModel());
        when(resultService.isResultFolderFound(CHAIN_TASK_ID)).thenReturn(false);

        List<String> recovered = replicateRecoveryService.recoverInterruptedReplicates();

        assertThat(recovered).isEmpty();

        Mockito.verify(subscriptionService, Mockito.times(0))
                .subscribeToTopic(CHAIN_TASK_ID);
    }

    @Test
    public void shouldNotRecoverByUploadingWhenResultNotFound() {
        when(iexecHubService.getLatestBlockNumber()).thenReturn(blockNumber);
        TaskNotification notif = getStubInterruptedTask(TaskNotificationType.PLEASE_UPLOAD);
        when(customFeignClient.getMissedTaskNotifications(blockNumber))
                .thenReturn(Collections.singletonList(notif));
        when(iexecHubService.getTaskDescriptionFromChain(any())).thenReturn(getStubModel());
        when(resultService.isResultFolderFound(CHAIN_TASK_ID)).thenReturn(false);

        List<String> recovered = replicateRecoveryService.recoverInterruptedReplicates();

        assertThat(recovered).isEmpty();

        Mockito.verify(subscriptionService, Mockito.times(0))
                .subscribeToTopic(CHAIN_TASK_ID);
    }

    // The notification type does not matter here since it is handled on the subscription service
    @Test
    public void shouldNotificationPassedToSubscriptionService() {
        when(iexecHubService.getLatestBlockNumber()).thenReturn(blockNumber);
        TaskNotification notif = getStubInterruptedTask(TaskNotificationType.PLEASE_COMPLETE);
        when(customFeignClient.getMissedTaskNotifications(blockNumber))
                .thenReturn(Collections.singletonList(notif));
        when(resultService.isResultAvailable(CHAIN_TASK_ID)).thenReturn(true);
        when(iexecHubService.getTaskDescriptionFromChain(any())).thenReturn(getStubModel());

        List<String> recovered = replicateRecoveryService.recoverInterruptedReplicates();

        assertThat(recovered).isNotEmpty();
        assertThat(recovered.get(0)).isEqualTo(CHAIN_TASK_ID);

        Mockito.verify(subscriptionService, Mockito.times(1))
                .subscribeToTopic(CHAIN_TASK_ID);
    }

    private TaskNotification getStubInterruptedTask(TaskNotificationType notificationType) {
        return TaskNotification.builder()
                .chainTaskId(CHAIN_TASK_ID)
                .taskNotificationType(notificationType)
                .taskNotificationExtra(TaskNotificationExtra.builder()
                        .contributionAuthorization(getStubAuth())
                        .build())
                .build();

    }

    private ContributionAuthorization getStubAuth() {
        return ContributionAuthorization.builder()
                .chainTaskId(CHAIN_TASK_ID)
                .build();
    }

    private Optional<TaskDescription> getStubModel() {
        return Optional.of(TaskDescription.builder()
                .chainTaskId(CHAIN_TASK_ID)
                .build());
    }
    
}