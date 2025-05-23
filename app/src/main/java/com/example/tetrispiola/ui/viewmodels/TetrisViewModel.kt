package com.example.tetrispiola.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.tetrispiola.ui.Tetromino
import kotlin.random.Random

class TetrisViewModel : ViewModel() {
    val board = Array(20) { Array(10) { 0 } }
    var score = 0
        private set
    var level = 1
        private set
    var speed = 1000L
        private set

    var linesClearedListener: ((Int) -> Unit)? = null

    val shapes = listOf(
        arrayOf( // I
            arrayOf(1, 1, 1, 1)
        ),
        arrayOf( // O
            arrayOf(1, 1),
            arrayOf(1, 1)
        ),
        arrayOf( // T
            arrayOf(0, 1, 0),
            arrayOf(1, 1, 1)
        ),
        arrayOf( // S
            arrayOf(0, 1, 1),
            arrayOf(1, 1, 0)
        ),
        arrayOf( // Z
            arrayOf(1, 1, 0),
            arrayOf(0, 1, 1)
        ),
        arrayOf( // J
            arrayOf(1, 0, 0),
            arrayOf(1, 1, 1)
        ),
        arrayOf( // L
            arrayOf(0, 0, 1),
            arrayOf(1, 1, 1)
        )
    )

    val colors = listOf(
        0xFF00FFFF.toInt(), // Celeste
        0xFFFFFF00.toInt(), // Amarillo
        0xFF800080.toInt(), // Morado
        0xFF00FF00.toInt(), // Verde
        0xFFFF0000.toInt(), // Rojo
        0xFF0000FF.toInt(), // Azul
        0xFFFFA500.toInt()  // Naranja
    )

    var currentPiece: Tetromino = generateRandomPiece()

    private fun isValidMove(shape: Array<Array<Int>>, x: Int, y: Int): Boolean {
        shape.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, cell ->
                if (cell == 1) {
                    val boardX = x + colIndex
                    val boardY = y + rowIndex

                    if (boardX !in 0 until 10 || boardY !in 0 until 20 || board[boardY][boardX] != 0) {
                        return false
                    }
                }
            }
        }
        return true
    }

    private fun generateRandomPiece(): Tetromino {
        val index = Random.nextInt(shapes.size)
        return Tetromino(
            shape = shapes[index],
            color = colors[index]
        )
    }

    fun moveDown(): Boolean {
        if (isValidMove(currentPiece.shape, currentPiece.x, currentPiece.y + 1)) {
            currentPiece.y += 1
            return true
        } else {
            placePiece()
            val linesCleared = clearCompletedLines()
            calculateScore(linesCleared)
            checkLevelUp()
            linesClearedListener?.invoke(linesCleared)
            currentPiece = generateRandomPiece()
            return false
        }
    }

    fun moveLeft() {
        if (isValidMove(currentPiece.shape, currentPiece.x - 1, currentPiece.y)) {
            currentPiece.x -= 1
        }
    }

    fun moveRight() {
        if (isValidMove(currentPiece.shape, currentPiece.x + 1, currentPiece.y)) {
            currentPiece.x += 1
        }
    }

    fun moveToBottom() {
        while (moveDown()) {}
    }

    private fun placePiece() {
        currentPiece.shape.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, cell ->
                if (cell == 1) {
                    val boardX = currentPiece.x + colIndex
                    val boardY = currentPiece.y + rowIndex
                    if (boardY in 0 until 20 && boardX in 0 until 10) {
                        board[boardY][boardX] = currentPiece.color
                    }
                }
            }
        }
    }

    fun rotatePiece() {
        val rotatedShape = rotateMatrix(currentPiece.shape)
        if (isValidMove(rotatedShape, currentPiece.x, currentPiece.y)) {
            currentPiece.shape = rotatedShape
        }
    }

    private fun rotateMatrix(matrix: Array<Array<Int>>): Array<Array<Int>> {
        val rows = matrix.size
        val cols = matrix[0].size
        val rotated = Array(cols) { Array(rows) { 0 } }

        for (i in 0 until rows) {
            for (j in 0 until cols) {
                rotated[j][rows - 1 - i] = matrix[i][j]
            }
        }
        return rotated
    }

    fun clearCompletedLines(): Int {
        var linesCleared = 0
        for (row in board.indices.reversed()) {
            if (board[row].all { it != 0 }) {
                removeLine(row)
                linesCleared++
            }
        }
        return linesCleared
    }

    private fun removeLine(line: Int) {
        for (row in line downTo 1) {
            board[row] = board[row - 1].copyOf()
        }
        board[0] = Array(10) { 0 }
    }


    private fun calculateScore(linesCleared: Int) {
        if (linesCleared >= 1) {
            val earned = if (linesCleared == 1) {
                10
            } else {
                linesCleared * linesCleared * 10
            }
            score += earned
        }
    }

    private fun checkLevelUp() {
        val newLevel = (score / 50) + 1
        if (newLevel > level) {
            level = newLevel
            speed = (speed * 0.8).toLong().coerceAtLeast(100)
            resetBoard()
        }
    }

    private fun resetBoard() {
        for (row in board.indices) {
            board[row].fill(0)
        }
    }
}