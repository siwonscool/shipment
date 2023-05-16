package com.example.shipment.consumer;

import com.example.shipment.dto.CheckOutDto;
import com.example.shipment.service.SaveService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {

    private static final String TOPIC_NAME = "checkout.complete.v1";

    private static final String GROUP_ID = "shipment.group.v1";

    private final SaveService saveService;

    //발행자(producer) 측에서 현재 구독자(consumer)가 가지고 있지 않은 필드를 추가하더라도 장애가 발생하지 않도록 Unknown field ignore 처리
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @KafkaListener(topics = TOPIC_NAME, groupId = GROUP_ID)
    public void recordListener(String jsonMessage) {
        try {
            CheckOutDto checkOutDto = objectMapper.readValue(jsonMessage, CheckOutDto.class);
            log.info(checkOutDto.toString());
            saveService.saveCheckOutData(checkOutDto);
        } catch (Exception e) {
            log.error("recordListener ERROR message = {}", jsonMessage, e);
        }
    }
}
