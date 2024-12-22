package com.fiveinarow.view;

import com.fiveinarow.model.Board;
import com.fiveinarow.model.ChequerColor;
import com.fiveinarow.model.Place;
import com.fiveinarow.model.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BoardPanel extends JPanel {
    private final Board board;
    private final int startX;
    private final int startY;
    private final int cellSize;
    private final Player player1;
    private final Player player2;
    private Player currentPlayer;

    public BoardPanel(int cellSize, int startX, int startY, Board board, Player currentPlayer, Player player1, Player player2) {
        this.cellSize = cellSize;
        this.startX = startX;
        this.startY = startY;
        this.board = board;
        this.currentPlayer = currentPlayer;
        this.player1 = player1;
        this.player2 = player2;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                int row = (y - startY) / cellSize;
                int col = (x - startX) / cellSize;

                // 检查四个可能的网格位置
                int[][] positions = {{row, col}, {row + 1, col}, {row, col + 1}, {row + 1, col + 1}};
                for (int[] pos : positions) {
                    if (isCloseTo(x, y, pos[0], pos[1])) {
                        Place place = BoardPanel.this.currentPlayer.getColor() == ChequerColor.BLACK ? Place.BLACK : Place.WHITE;
                        handleClick(pos[0], pos[1], place);
                        return;
                    }
                }
            }
        });
    }

    /**
     * 判断给定坐标点是否接近于指定的网格坐标
     * 该方法主要用于判断一个点是否在一个以指定网格坐标为中心、指定半径为边界的圆形区域内
     *
     * @param x   目标点的x坐标
     * @param y   目标点的y坐标
     * @param row 网格的行号
     * @param col 网格的列号
     * @return 如果目标点与网格坐标的距离在10个单位以内，则返回true；否则返回false
     */
    private boolean isCloseTo(int x, int y, int row, int col) {
        // 计算目标点到指定网格坐标的欧氏距离
        double distance = Math.sqrt(Math.pow(x - (startX + col * cellSize), 2) +
                Math.pow(y - (startY + row * cellSize), 2));
        // 判断距离是否小于等于10个单位
        return distance <= 10;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int row = board.getRow();
        int col = board.getCol();

        // 绘制水平线
        for (int i = 0; i < row; i++) {
            g.drawLine(startX, startY + i * cellSize, startX + (col - 1) * cellSize, startY + i * cellSize);
        }

        // 绘制垂直线
        for (int j = 0; j < col; j++) {
            g.drawLine(startX + j * cellSize, startY, startX + j * cellSize, startY + (row - 1) * cellSize);
        }

        // 绘制棋子
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                Place place = board.getPlace(i, j);
                if (place == Place.BLACK) {
                    g.setColor(Color.BLACK);
                    g.fillOval(startX + j * cellSize - 10, startY + i * cellSize - 10, 20, 20);
                } else if (place == Place.WHITE) {
                    g.setColor(Color.WHITE);
                    g.fillOval(startX + j * cellSize - 10, startY + i * cellSize - 10, 20, 20);
                }
            }
        }
    }

    /**
     * 处理点击事件
     * 当玩家点击游戏板上的一个位置时，该方法会被调用
     * 它负责检查点击的位置是否有效，更新游戏板的状态，检查游戏是否结束，并切换玩家
     *
     * @param row   点击的行号
     * @param col   点击的列号
     * @param place 要放置的棋子类型
     */
    private void handleClick(int row, int col, Place place) {
        // 检查点击位置是否在游戏板内且为空
        if (row >= 0 && row < board.getRow() && col >= 0 && col < board.getCol() && board.getPlace(row, col) == Place.EMPTY) {
            // 在点击的位置放置棋子
            board.setPlace(row, col, place);
            // 重新绘制游戏板
            repaint();

            // 检查当前玩家是否获胜
            if (checkWin(row, col, place)) {
                // 显示游戏结束对话框，询问玩家是否想重新开始游戏
                int option = JOptionPane.showOptionDialog(
                        this,
                        currentPlayer.getName() + " wins! Do you want to play again?",
                        "Game Over",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new Object[]{"Play Again", "Exit"},
                        "Play Again"
                );

                // 根据玩家选择进行相应操作
                if (option == JOptionPane.YES_OPTION) {
                    // 重置游戏
                    resetGame();
                    return;
                } else {
                    // 退出游戏
                    System.exit(0);
                }
            }
            // 切换玩家
            currentPlayer = currentPlayer == player1 ? player2 : player1;
        }
    }


    private void resetGame() {
        // 重置棋盘状态
        board.reset();
        // 重置当前玩家
        currentPlayer = player1;
        // 重新绘制棋盘
        repaint();
    }


    /**
     * 检查当前位置是否获胜
     *
     * @param row   行号
     * @param col   列号
     * @param place 当前位置的状态
     * @return 如果当前位置获胜则返回true，否则返回false
     */
    private boolean checkWin(int row, int col, Place place) {
        // 检查行、列、对角线和反对角线是否有获胜条件
        return checkRow(row, col, place) || checkCol(row, col, place) || checkDiagonal(row, col, place) || checkDiagonal2(row, col, place);
    }

    /**
     * 检查在指定位置是否有足够多的相同标识排列在对角线上
     * 此方法用于判断在棋盘的某个位置上，是否有连续的相同标识（例如棋子）排列在对角线上
     * 达到或超过一定数量，通常用于判断胜负条件
     *
     * @param row   行号，表示要检查的位置的行号
     * @param col   列号，表示要检查的位置的列号
     * @param place 标识类型，表示要检查的标识是哪一方
     * @return 如果在对角线上找到足够多的相同标识，则返回true，否则返回false
     */
    private boolean checkDiagonal(int row, int col, Place place) {
        // 初始化计数器，用于统计连续的相同标识的数量
        int cnt = 0;

        // 从指定位置的左上方开始检查，向左上方遍历
        int cur_row = row - 1;
        int cur_col = col - 1;
        while (cur_row >= 0 && cur_col >= 0 && board.getPlace(cur_row, cur_col) == place) {
            cur_row--;
            cur_col--;
            cnt++;
        }

        // 从指定位置的右下方开始检查，向右下方遍历
        cur_row = row + 1;
        cur_col = col + 1;
        while (cur_row < board.getRow() && cur_col < board.getCol() && board.getPlace(cur_row, cur_col) == place) {
            cur_row++;
            cur_col++;
            cnt++;
        }

        // 检查计数器是否达到或超过4，即是否有足够的相同标识排列在对角线上
        return cnt >= 4;
    }

    /**
     * 检查棋子在对角线上的连续数量是否达到或超过4个
     * 此方法专注于检查从左下到右上的对角线方向
     *
     * @param row   棋子放置的行位置
     * @param col   棋子放置的列位置
     * @param place 检查的棋子类型
     * @return 如果在对角线上找到连续的相同棋子数量达到或超过4个，则返回true，否则返回false
     */
    private boolean checkDiagonal2(int row, int col, Place place) {
        // 初始化连续棋子计数器
        int cnt = 0;

        // 从当前棋子位置向左上方向遍历
        int cur_row = row - 1;
        int cur_col = col + 1;
        // 继续遍历直到超出棋盘边界或遇到不同类型的棋子
        while (cur_row >= 0 && cur_col < board.getCol() && board.getPlace(cur_row, cur_col) == place) {
            cur_row--;
            cur_col++;
            cnt++;
        }

        // 重置当前位置以检查右下方向
        cur_row = row + 1;
        cur_col = col - 1;
        // 继续遍历直到超出棋盘边界或遇到不同类型的棋子
        while (cur_row < board.getRow() && cur_col >= 0 && board.getPlace(cur_row, cur_col) == place) {
            cur_row++;
            cur_col--;
            cnt++;
        }

        // 检查连续棋子数量是否达到或超过4个
        return cnt >= 4;
    }

    /**
     * 检查在给定位置后是否在列方向上形成了连续的五个相同棋子
     *
     * @param row   棋子放置的行号，用于检查列状态
     * @param col   棋子放置的列号，用于检查列状态
     * @param place 枚举类型，表示棋盘上某个位置的状态，此处为要检查的棋子类型
     * @return 如果在列方向上形成了连续的五个相同棋子，则返回true；否则返回false
     */
    private boolean checkCol(int row, int col, Place place) {
        int count = 0;
        // 遍历列中的每个位置
        for (int c = 0; c < board.getRow(); c++) {
            // 如果当前位置的棋子与给定的棋子类型相同，则计数加一
            if (board.getPlace(c, col) == place) {
                count++;
                // 如果计数达到5，表示形成了连续的五个相同棋子，返回true
                if (count == 5) {
                    return true;
                }
            } else {
                // 如果当前位置的棋子与给定的棋子类型不同，重置计数
                count = 0;
            }
        }
        // 如果遍历结束时没有形成连续的五个相同棋子，返回false
        return false;
    }

    /**
     * 检查给定位置所在的行是否满足胜利条件
     *
     * @param row   行号，表示需要检查的行
     * @param col   列号，与row参数一起使用以定位到具体位置
     * @param place 枚举类型，表示需要检查的棋子类型
     * @return 如果在该行找到连续的5个指定棋子，则返回true，否则返回false
     */
    private boolean checkRow(int row, int col, Place place) {
        // 初始化计数器，用于记录连续棋子的数量
        int count = 0;
        // 遍历该行的所有列
        for (int c = 0; c < board.getCol(); c++) {
            // 如果当前位置的棋子与指定棋子类型相同，则增加计数器
            if (board.getPlace(row, c) == place) {
                count++;
                // 如果连续棋子数量达到5，则表示满足胜利条件，返回true
                if (count == 5) {
                    return true;
                }
            } else {
                // 如果当前位置的棋子与指定棋子类型不同，则重置计数器
                count = 0;
            }
        }
        // 如果遍历完成后没有找到连续的5个指定棋子，则返回false
        return false;
    }


}
