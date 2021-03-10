package com.learnreactivespring.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class ItemCapped { // can never use Capped Collection for permanent storage because it is a fixed size

    @Id // identifies the primary key
    private String id;

    private String description;

    private Double price;

}
