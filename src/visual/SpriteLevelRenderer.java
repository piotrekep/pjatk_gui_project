package visual;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

class SpriteLevelRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setText("");
        setIcon(null);
        setHorizontalAlignment(CENTER);

        if (value instanceof SpriteCellType) {
            SpriteCellType cell = (SpriteCellType) value;
            Image img = cell.type.getSprite(0);
            setIcon(new ImageIcon(img));
        } else {
            setBackground(Color.WHITE);
        }
        setOpaque(true);
        return this;
    }
}
