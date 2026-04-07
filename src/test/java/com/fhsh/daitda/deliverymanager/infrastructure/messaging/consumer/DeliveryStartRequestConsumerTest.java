package com.fhsh.daitda.deliverymanager.infrastructure.messaging.consumer;

import com.fhsh.daitda.deliverymanager.application.command.StartDeliveryCommand;
import com.fhsh.daitda.deliverymanager.application.port.event.DeliveryStartEvent;
import com.fhsh.daitda.deliverymanager.application.service.command.DeliveryManagerCommandService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeliveryStartRequestConsumerTest {

    @Mock
    private DeliveryManagerCommandService commandService;

    @InjectMocks
    private DeliveryStartRequestConsumer consumer;

    @Test
    @DisplayName("성공 케이스 - 배송 시작 이벤트를 받으면 startDelivery 호출")
    void consume_success() {
        // given
        UUID deliveryId = UUID.randomUUID();
        UUID deliveryManagerId = UUID.randomUUID();

        DeliveryStartEvent event = new DeliveryStartEvent(deliveryId, deliveryManagerId);

        // when
        consumer.consume(event);

        // then
        ArgumentCaptor<StartDeliveryCommand> captor =
                ArgumentCaptor.forClass(StartDeliveryCommand.class);

        verify(commandService).startDelivery(captor.capture());

        StartDeliveryCommand command = captor.getValue();
        assertThat(command.deliveryId()).isEqualTo(deliveryId);
        assertThat(command.deliveryManagerId()).isEqualTo(deliveryManagerId);
    }
}
