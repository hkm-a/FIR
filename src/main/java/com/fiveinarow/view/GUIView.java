package com.fiveinarow.view;

import com.fiveinarow.model.Board;
import com.fiveinarow.model.Player;

import javax.swing.*;

public class GUIView extends JFrame {
    private final Board board;
    private final Player player1;
    private final Player player2;

    public GUIView(Board board, Player player1, Player player2) {
        this.board = board;
        this.player1 = player1;
        this.player2 = player2;
    }

    /**
     * 启动五子棋游戏的窗口
     * 该方法初始化游戏窗口，设置窗口标题、大小，并确保窗口关闭时退出程序
     */
    public void startGame() {
        // 设置窗口标题为"五子棋"
        setTitle("五子棋");
        // 设置窗口大小为600x600像素
        setSize(590, 615);
        // 设置窗口关闭操作，当用户关闭窗口时退出应用程序
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 窗口居中
        setLocationRelativeTo(null);

        add(new BoardPanel(40, 10, 10, board, player1, player1, player2));
        // 使窗口可见
        setVisible(true);
    }


}
