package com.learnreactivespring.controller.v1;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learnreactivespring.document.ItemCapped;
import com.learnreactivespring.repository.ItemReactiveCappedRepository;

import static com.learnreactivespring.constants.ItemConstants.ITEM_STREAM_ENDPOINT_V1;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
public class ItemStreamController {

    private final ItemReactiveCappedRepository itemReactiveCappedRepository;

    @GetMapping(value = ITEM_STREAM_ENDPOINT_V1, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<ItemCapped> getItemsStream() {
        return itemReactiveCappedRepository.findItemsBy();
    }


}
