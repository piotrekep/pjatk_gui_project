package visual;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class LevelRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean sel, boolean foc,
            int row, int col) {

        super.getTableCellRendererComponent(table, value, sel, foc, row, col);
        setHorizontalAlignment(CENTER);   

        if (value instanceof CellTypeVisu) {
            CellTypeVisu type = (CellTypeVisu) value;
            
            setBackground(type.getColor());
           
            setText("");
        } else {
            
            setBackground(Color.WHITE);
            setText(value != null ? value.toString() : "");
        }
         
        setOpaque(true);   
        return this;
    }
}
