package com.frontend.dto;

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
