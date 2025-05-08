package model;

import javax.swing.table.AbstractTableModel;

public class GameBoard extends AbstractTableModel {
    CellType[][] table;
    public final int sizeY, sizeX;

    public GameBoard(int Y, int X) {
        this.sizeY = Y;
        this.sizeX = X;
        this.table = new CellType[this.sizeX][this.sizeY];
        for (int i = 0; i < table.length; i++)
            for (int j = 0; j < table[i].length; j++)
                table[i][j] = CellType.WALL;

    }

    public void setCell(int row, int col, CellType newType) {
        table[row][col] = newType;
        fireTableCellUpdated(row, col);
    }

    public CellType[][] getBoard() {
        return table;
    }

    public void setBoard(CellType[][] board) {
        CellType[][] newTable = new CellType[board.length][];
       
        for (int i = 0; i < board.length; i++) {
            newTable[i] = board[i].clone();
        }
        this.table = newTable;
       
        fireTableDataChanged();
    }


    @Override
    public int getRowCount() {
        return this.table.length;
    }

    @Override
    public int getColumnCount() {
        return this.table[0].length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return this.table[rowIndex][columnIndex];
    }

}
