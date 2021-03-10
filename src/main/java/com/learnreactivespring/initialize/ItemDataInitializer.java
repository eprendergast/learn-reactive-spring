package com.learnreactivespring.initialize;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.stereotype.Component;

import com.learnreactivespring.document.Item;
import com.learnreactivespring.document.ItemCapped;
import com.learnreactivespring.repository.ItemReactiveCappedRepository;
import com.learnreactivespring.repository.ItemReactiveRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Profile("!test")
@Component
@RequiredArgsConstructor
public class ItemDataInitializer implements CommandLineRunner {

    private final ItemReactiveRepository itemReactiveRepository;
    private final ItemReactiveCappedRepository itemReactiveCappedRepository;
    private final ReactiveMongoOperations mongoOperations;

    @Override
    public void run(String... args) throws Exception {
        initialDataSetUp();
        createCappedCollection();
        dataSetUpForCappedCollection();
    }

    private void createCappedCollection() {
        mongoOperations.dropCollection(ItemCapped.class)
                       .then(mongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty().maxDocuments(20).size(50000).capped())).subscribe();
        // maxDocuments(20) means that we will only ever be able to retrieve a maximum of 20 documents at once
    }

    public List<Item> data() {
        return Arrays.asList(new Item(null, "Samsung TV", 399.99),
                             new Item(null, "LG TV", 329.99),
                             new Item(null, "Apple Watch", 349.99),
                             new Item("ABC", "Beats Headphones", 149.99));
    }

    private void initialDataSetUp() {
        itemReactiveRepository.deleteAll()
                              .thenMany(Flux.fromIterable(data()))
                              .flatMap(itemReactiveRepository::save)
                              .thenMany(itemReactiveRepository.findAll())
                              .subscribe(item -> {
                                  System.out.println("Item inserted from CommandLineRunner : " + item);
                              });
    }

    public void dataSetUpForCappedCollection() {
        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(2))
                                              .map(i -> new ItemCapped(null, "Random Item " + i, (100.00 + i)));
        itemReactiveCappedRepository.insert(itemCappedFlux)
                                    .subscribe(itemCapped -> log.info("Inserted item is " + itemCapped));
    }


}
