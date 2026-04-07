package com.fhsh.daitda.deliverymanager.infrastructure.messaging.producer;

import com.fhsh.daitda.deliverymanager.application.port.event.DeliveryCompleteEvent;
import com.fhsh.daitda.deliverymanager.infrastructure.config.KafkaTopicConfig;
import com.fhsh.daitda.deliverymanager.infrastructure.messaging.DeliveryCompleteMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeliveryCompleteRequestProducerTest {

    @Mock
    private KafkaTemplate<String, DeliveryCompleteMessage> kafkaTemplate;

    @InjectMocks
    private DeliveryCompleteRequestProducer producer;

    @Test
    @DisplayName("성공 케이스 - 배송 완료 이벤트를 올바른 토픽과 key, message로 발행")
    void send_success() {
        // given
        UUID deliveryId = UUID.randomUUID();
        UUID deliveryManagerId = UUID.randomUUID();

        DeliveryCompleteEvent event = new DeliveryCompleteEvent(deliveryId, deliveryManagerId);

        // when
        producer.send(event);

        // then
        ArgumentCaptor<DeliveryCompleteMessage> captor =
                ArgumentCaptor.forClass(DeliveryCompleteMessage.class);

        verify(kafkaTemplate).send(
                eq(KafkaTopicConfig.DELIVERY_COMPLETE_REQUEST),
                eq(deliveryId.toString()),
                captor.capture()
        );

        DeliveryCompleteMessage message = captor.getValue();
        assertThat(message.deliveryId()).isEqualTo(deliveryId);
        assertThat(message.deliveryManagerId()).isEqualTo(deliveryManagerId);
    }
}
