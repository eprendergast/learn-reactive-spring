package com.learnreactivespring.controller.v1;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.learnreactivespring.document.Item;
import com.learnreactivespring.repository.ItemReactiveRepository;

import static com.learnreactivespring.constants.ItemConstants.ITEM_ENDPOINT_V1;
import static org.junit.jupiter.api.Assertions.assertTrue;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ActiveProfiles("test")
@SpringBootTest
@DirtiesContext
@ExtendWith(SpringExtension.class)
@AutoConfigureWebTestClient // this autoconfigures the WebTestClient
class ItemControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    public List<Item> data() {
        return Arrays.asList(new Item(null, "Samsung TV", 399.99),
                             new Item(null, "LG TV", 329.99),
                             new Item(null, "Apple Watch", 349.99),
                             new Item("ABC", "Beats Headphones", 149.99));
    }

    @BeforeEach
    void setUp() {
        itemReactiveRepository.deleteAll()
                              .thenMany(Flux.fromIterable(data()))
                              .flatMap(itemReactiveRepository::save)
                              .doOnNext(item -> {
                                  System.out.println("Item inserted is : " + item);
                              })
                              .blockLast();
    }

    @Test
    void getAllItems() {
        webTestClient.get().uri(ITEM_ENDPOINT_V1)
                     .exchange()
                     .expectStatus().isOk()
                     .expectHeader().contentType(MediaType.APPLICATION_JSON)
                     .expectBodyList(Item.class).hasSize(4);
    }

    @Test
    void getAllItems_approach2() {
        webTestClient.get().uri(ITEM_ENDPOINT_V1)
                     .exchange()
                     .expectStatus().isOk()
                     .expectHeader().contentType(MediaType.APPLICATION_JSON)
                     .expectBodyList(Item.class).hasSize(4)
                     .consumeWith(response -> {
                         List<Item> items = response.getResponseBody();
                         items.forEach(item -> {
                             assertTrue(item.getId() != null);
                         });
                     });
    }

    @Test
    void getAllItems_approach3() {
        Flux<Item> itemFlux = webTestClient.get().uri(ITEM_ENDPOINT_V1)
                                           .exchange()
                                           .expectStatus().isOk()
                                           .expectHeader().contentType(MediaType.APPLICATION_JSON)
                                           .returnResult(Item.class)
                                           .getResponseBody();

        StepVerifier.create(itemFlux.log())
                    .expectNextCount(4)
                    .verifyComplete();
    }

    @Test
    void getOneItem() {
        webTestClient.get().uri(ITEM_ENDPOINT_V1.concat("/{id}"), "ABC")
                     .exchange()
                     .expectStatus().isOk()
                     .expectBody() // gives access to the body
                     .jsonPath("$.price", 149.99);
    }

    @Test
    void getOneItem_notFound() {
        webTestClient.get().uri(ITEM_ENDPOINT_V1.concat("/{id}"), "DEF")
                     .exchange()
                     .expectStatus().isNotFound();
    }

    @Test
    void createItem() {

        Item item = new Item(null, "iPhone X", 999.99);

        webTestClient.post().uri(ITEM_ENDPOINT_V1)
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(Mono.just(item), Item.class)
                     .exchange()
                     .expectStatus().isCreated()
                     .expectBody()
                     .jsonPath("$.id").isNotEmpty()
                     .jsonPath("$.description").isEqualTo("iPhone X")
                     .jsonPath("$.price").isEqualTo(999.99);
    }

    @Test
    void delteItem() {
        webTestClient.delete().uri(ITEM_ENDPOINT_V1.concat("/{id}"), "ABC")
                     .exchange()
                     .expectStatus().isOk()
                     .expectBody(Void.class);
    }

    @Test
    void updateItem() {
        Double newPrice = 129.99;
        Item item = new Item(null, "Beats Headphones", newPrice);

        webTestClient.put().uri(ITEM_ENDPOINT_V1.concat("/{id}"), "ABC")
                     .contentType(MediaType.APPLICATION_JSON)
                     .accept(MediaType.APPLICATION_JSON)
                     .body(Mono.just(item), Item.class)
                     .exchange()
                     .expectStatus().isOk()
                     .expectBody()
                     .jsonPath("$.price", newPrice);
    }

    @Test
    void updateItem_notFOund() {
        Double newPrice = 129.99;
        Item item = new Item(null, "Beats Headphones", newPrice);

        webTestClient.put().uri(ITEM_ENDPOINT_V1.concat("/{id}"), "DEF")
                     .contentType(MediaType.APPLICATION_JSON)
                     .accept(MediaType.APPLICATION_JSON)
                     .body(Mono.just(item), Item.class)
                     .exchange()
                     .expectStatus().isNotFound();
    }

}