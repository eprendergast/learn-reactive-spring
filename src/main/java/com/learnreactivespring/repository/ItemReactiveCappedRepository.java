package com.learnreactivespring.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;

import com.learnreactivespring.document.Item;
import com.learnreactivespring.document.ItemCapped;

import reactor.core.publisher.Flux;

public interface ItemReactiveCappedRepository extends ReactiveMongoRepository<ItemCapped, String> {

    @Tailable // this opens up the capability of the tailable cusor
    Flux<ItemCapped> findItemsBy();

}
