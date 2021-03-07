package com.learnreactivespring.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.learnreactivespring.document.Item;
import com.learnreactivespring.repository.ItemReactiveRepository;

import reactor.core.publisher.Mono;

@Component
public class ItemHandler {

    private static Mono<ServerResponse> notFound = ServerResponse.notFound().build();

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    public Mono<ServerResponse> getAllItems(ServerRequest serverRequest) {
        return ServerResponse.ok()
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(itemReactiveRepository.findAll(), Item.class);


    }

    public Mono<ServerResponse> getOneItem(ServerRequest serverRequest) {
        return itemReactiveRepository.findById(serverRequest.pathVariable("id"))
                                     .flatMap(item -> ServerResponse.ok()
                                                                    .contentType(MediaType.APPLICATION_JSON)
                                                                    .body(BodyInserters.fromObject(item)) // accepts a publisher

                                     ).switchIfEmpty(notFound);

    }

    public Mono<ServerResponse> createItem(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(Item.class)
                            .flatMap(item -> ServerResponse.created(serverRequest.uri())
                                                           .contentType(MediaType.APPLICATION_JSON)
                                                           .body(itemReactiveRepository.save(item), Item.class));
    }

    public Mono<ServerResponse> deleteItem(ServerRequest serverRequest) {
        return itemReactiveRepository.deleteById(serverRequest.pathVariable("id"))
                                     .flatMap(deletedItem -> ServerResponse.ok()
                                                                           .contentType(MediaType.APPLICATION_JSON)
                                                                           .body(deletedItem, Void.class));
    }

    public Mono<ServerResponse> updateItem(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(Item.class)
                            .flatMap(item -> itemReactiveRepository.findById(serverRequest.pathVariable("id"))
                                                                   .flatMap(currentItem -> {
                                                                       currentItem.setDescription(item.getDescription());
                                                                       currentItem.setPrice(item.getPrice());
                                                                       return itemReactiveRepository.save(currentItem);
                                                                   }))
                            .flatMap(item -> ServerResponse.ok()
                                                           .contentType(MediaType.APPLICATION_JSON)
                                                           .body(BodyInserters.fromObject(item)))
                            .switchIfEmpty(notFound);
    }
}