package com.learnreactivespring.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Document // equivalent to @Entity with relational databases
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    @Id // identifies the primary key
    private String id;

    private String description;

    private Double price;

}
