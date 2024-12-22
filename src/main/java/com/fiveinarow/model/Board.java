package com.fiveinarow.model;

import lombok.Getter;

public class Board {
    private final Place[][] board;
    @Getter
    private final int row;
    @Getter
    private final int col;

    public Board(int row, int col) {
        this.row = row;
        this.col = col;
        board = new Place[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                board[i][j] = Place.EMPTY;
            }
        }
    }

    /**
     * 获取指定位置的棋子
     *
     * @param row 行号
     * @param col 列号
     * @return 返回指定位置的棋子对象
     */
    public Place getPlace(int row, int col) {
        // 通过二维数组board来访问指定行列号的棋子
        return board[row][col];
    }

    /**
     * 在游戏板上设置指定位置的场所
     *
     * @param row   行号，表示在游戏板上的垂直位置
     * @param col   列号，表示在游戏板上的水平位置
     * @param place 要设置的场所对象，代表该位置上的游戏元素
     */
    public void setPlace(int row, int col, Place place) {
        board[row][col] = place;
    }

    public void reset() {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                board[i][j] = Place.EMPTY;
            }
        }
    }
}
