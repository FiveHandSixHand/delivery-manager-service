package com.fhsh.daitda.deliverymanager.application.port;

import com.fhsh.daitda.deliverymanager.application.port.event.DeliveryCompleteEvent;

public interface DeliveryCompleteEventPort {
    void send(DeliveryCompleteEvent event);
}
