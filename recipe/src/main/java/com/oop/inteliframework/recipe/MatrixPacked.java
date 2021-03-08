package com.oop.inteliframework.recipe;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data@AllArgsConstructor
public class MatrixPacked {
    int current;
    int left;
    int down;
    int diag;
}
