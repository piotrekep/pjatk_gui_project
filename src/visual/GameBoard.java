package visual;

import javax.swing.table.AbstractTableModel;

/**
 * @class GameBoard
 * @brief roszerzenie klasy Abstract table model jako wymóg zadania
 */

public class GameBoard extends AbstractTableModel {
    CellTypeVisu[][] table;
    public final int sizeY, sizeX;

    public GameBoard(int Y, int X) {
        this.sizeY = Y;
        this.sizeX = X;
        this.table = new CellTypeVisu[this.sizeX][this.sizeY];
        for (int i = 0; i < table.length; i++)
            for (int j = 0; j < table[i].length; j++)
                table[i][j] = new CellTypeVisu(CellTypeVisu.Type.WALL);

    }

    public void setCell(int row, int col, CellTypeVisu newType) {
        table[row][col] = newType;
        fireTableCellUpdated(row, col);
    }

    public CellTypeVisu[][] getBoard() {
        return table;
    }

    public void setBoard(CellTypeVisu[][] board) {
        CellTypeVisu[][] newTable = new CellTypeVisu[board.length][];

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
        CellTypeVisu old = table[rowIndex][columnIndex];
        visual.SpriteCellType t = new SpriteCellType(null, null);

        t.type = visual.SpriteCellType.Type.valueOf(old.type.name());
        t.agent=old.agent;
        
        return t;
    }

}
