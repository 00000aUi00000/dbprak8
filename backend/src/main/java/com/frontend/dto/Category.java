package com.frontend.model;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Category {
    
    private final long id;
    private final String name;
    private final List<Category> childs;

}
