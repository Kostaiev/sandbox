package com.sandbox.controller.pojo;


import java.util.List;

public record Recipe(String name, List<Ingredient> ingredients) {
}
