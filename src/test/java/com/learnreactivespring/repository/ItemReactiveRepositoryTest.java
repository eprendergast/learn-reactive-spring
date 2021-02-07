package com.learnreactivespring.repository;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.learnreactivespring.document.Item;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataMongoTest // will load all the necessary classes that are required for Mongo test case
@ExtendWith(SpringExtension.class)
class ItemReactiveRepositoryTest {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    List<Item> items = Arrays.asList(new Item(null, "Samsung TV", 400.00),
                                     new Item(null, "LG TV", 420.00),
                                     new Item(null, "Apple Watch", 299.99),
                                     new Item(null, "Bose Headphones", 149.98),
                                     new Item("ABC", "Beats Headphones", 149.98));

    @BeforeEach
    public void setUp() {
        itemReactiveRepository.deleteAll()
                              .thenMany(Flux.fromIterable(items))
                              .flatMap(item -> itemReactiveRepository.save(item))
                              .doOnNext(item -> System.out.println("Inserted item is : " + item))
                              .blockLast(); // will wait until all operations above are complete
        // do not use this in actual code
    }

    @Test
    public void getAllItems() {
        StepVerifier.create(itemReactiveRepository.findAll())
                    .expectSubscription()
                    .expectNextCount(5)
                    .verifyComplete();
    }

    @Test
    public void getItemById() {
        StepVerifier.create(itemReactiveRepository.findById("ABC"))
                    .expectSubscription()
                    .expectNextMatches(item -> item.getDescription().equals("Beats Headphones"))
                    .verifyComplete();
    }

    @Test
    public void getItemByDescription() {
        StepVerifier.create(itemReactiveRepository.findByDescription("Samsung TV"))
                    .expectSubscription()
                    //                    .expectNextCount(1)
                    .expectNextMatches(item -> item.getDescription().equals("Samsung TV"))
                    .verifyComplete();
    }

    @Test
    public void saveItem() {
        Item item = new Item("DEF", "Google Home mini", 30.00);

        Mono<Item> savedItem = itemReactiveRepository.save(item);

        StepVerifier.create(savedItem.log("savedItem: "))
                    .expectSubscription()
                    .expectNextMatches(item1 -> item1.getId() != null && item1.getDescription().equals("Google Home mini"))
                    .verifyComplete();
    }

    @Test
    public void updateItem() {

        Double newPrice = 520.00;

        Flux<Item> updatedItem = itemReactiveRepository.findByDescription("LG TV")
                                                       .map(item -> {
                                                           item.setPrice(newPrice); // setting new price
                                                           return item;
                                                       })
                                                       .flatMap(item -> {
                                                           return itemReactiveRepository.save(item); // saving the item with the new price
                                                       });

        StepVerifier.create(updatedItem)
                    .expectSubscription()
                    .expectNextMatches(item -> item.getPrice() == 520.00)
                    .verifyComplete();
    }

    @Test
    public void deleteItemById() {

        Mono<Void> deletedItem = itemReactiveRepository.findById("ABC") // Mono<Item>
                                                       .map(Item::getId) // get Id -> Transform from one type to another type
                                                       .flatMap(id -> itemReactiveRepository.deleteById(id));

        StepVerifier.create(deletedItem)
                    .expectSubscription()
                    .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log("The new Item List: "))
                    .expectNextCount(4)
                    .verifyComplete();
    }

    @Test
    public void deleteItemByDescription() {

        Flux<Object> deletedItem = itemReactiveRepository.findByDescription("LG TV")
                                                         .flatMap(item -> itemReactiveRepository.delete(item));

        StepVerifier.create(deletedItem.log())
                    .expectSubscription()
                    .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log("The new Item List: "))
                    .expectSubscription()
                    .expectNextCount(4)
                    .verifyComplete();
    }

}