package tornadofx

import core.Board
import javafx.scene.control.Button
import controller.*
import core.*
import javafx.scene.image.*
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane

class CheckersView : View(), BoardListener {
    private val columnsNumber = 8
    private val rowsNumber = 8
    private val board = Board(columnsNumber, rowsNumber)
    private val buttons = mutableMapOf<Cell, Button>()
    private var inProcess = true
    private lateinit var statusLabel: Label
    override val root = BorderPane()

    init {
        title = "Checkers"
        val listener = BoardBasedCellListener(board)
        board.registerListener(this)
        with(root) {
            top {
                vbox {
                    menubar {
                        menu("Game") {
                            item("Restart").action {
                                restartGame()
                            }
                            separator()
                            item("Exit").action {
                                this@CheckersView.close()
                            }
                        }
                    }
                    toolbar {
                        button(graphic = ImageView("/restart.png").apply {
                            fitWidth = 16.0
                            fitHeight = 16.0
                        }).action {
                            restartGame()
                        }
                    }
                }
            }
            center {
                gridpane {
                    for (y in 0 until rowsNumber) {
                        row {
                            for (x in 0 until columnsNumber) {
                                val cell = Cell(x, y)
                                val button = button { graphic = ImageView(getPng(cell))
                                    style {
                                        minHeight = 65.0.px
                                        minWidth = 65.0.px
                                        padding = box(0.px)
                                    }
                                }
                                button.action {
                                    if (inProcess) {
                                        listener.cellClicked(cell)
                                    }
                                }
                                buttons[cell] = button
                            }
                        }
                    }
                }
            }
            bottom {
                statusLabel = label("")
            }
        }
        updateBoardAndStatus()
    }

    private fun restartGame() {
        board.clear()
        for (x in 0 until columnsNumber) {
            for (y in 0 until rowsNumber) {
                updateBoardAndStatus(listOf(Cell(x, y)))
            }
        }
        inProcess = true
    }

    private fun updateBoardAndStatus(cell: List<Cell>? = null) {
        val winner = board.gameOver()
        statusLabel.text = when {
            winner == Color.BLACK -> {
                inProcess = false
                "Blacks Win! Press 'Restart' to continue"
            }
            winner == Color.WHITE -> {
                inProcess = false
                "Whites Win! Press 'Restart' to continue"
            }
            board.turn == Color.BLACK ->
                "Game in process: Blacks turn"
            else ->
                "Game in process: Whites turn"
        }
        if (cell != null) {
            for (cells in cell) {
                buttons[cells]?.apply {
                    graphic = ImageView(getPng(cells))
                    style {
                        minHeight = 65.px
                        minWidth = 65.px
                        padding = box(0.px)
                    }
                }
            }
        }
        else return
    }

    private fun getPng(cell: Cell) = when {
        board[cell] is Queen && board[cell]?.color == Color.WHITE -> "./whiteQueen.png"
        board[cell] is Queen && board[cell]?.color == Color.BLACK -> "./blackQueen.png"
        board[cell]?.color == Color.WHITE -> "./white.png"
        board[cell]?.color == Color.BLACK -> "./black.png"
        (cell.x + cell.y) % 2 == 1 -> "/blackCell.png"
        else -> "./whiteCell.png"
    }

    override fun turnMade(cell: MutableList<Cell>) {
        updateBoardAndStatus(cell)
    }
}