package com.fiveinarow;

import com.fiveinarow.model.Board;
import com.fiveinarow.model.ChequerColor;
import com.fiveinarow.model.Player;
import com.fiveinarow.view.GUIView;

public class FiveInARowApplication {
    public static void main(String[] args) {
        // 初始化棋盘
        Board board = new Board(15, 15);

        // 初始化玩家
        Player player1 = new Player("Player 1", ChequerColor.BLACK);
        Player player2 = new Player("Player 2", ChequerColor.WHITE);

        // 选择图形用户界面
        GUIView guiView = new GUIView(board, player1, player2);
        guiView.startGame();
    }
}
