package com.example.tetrispiola.ui

data class Tetromino(
    var shape: Array<Array<Int>>,
    var color: Int,
    var x: Int = 3, // posición inicial en columnas
    var y: Int = 0  // posición inicial en filas
)