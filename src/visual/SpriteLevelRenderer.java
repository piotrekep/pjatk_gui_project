package visual;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import model.Personality;
import model.Player;
import model.Npc;
import model.Powerup;
import model.PowerupType;

/**
 * @class SpriteLevelRenderer
 * @brief klasa implementująca renderer tabeli obsługujący sprite'y
 */

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
            Image img;
            SpriteCellType.Type spriteType = null;
            if (cell.agent != null){

                if (cell.agent instanceof Player) {
                    spriteType = SpriteCellType.Type.PLAYER;
                    img = spriteType.getSprite(cell.agent.getMoveProgress(), cell.agent.getDirection());
                } else if (cell.agent instanceof Npc) {
                    Personality personality = ((Npc) cell.agent).getPersonality();
                    spriteType = SpriteCellType.Type.valueOf("NPC_" + personality.name());
                    img = spriteType.getSprite(cell.agent.getDirection());

                } else if (cell.agent instanceof Powerup) {
                    PowerupType powerType = ((Powerup) cell.agent).getPowerup();
                    spriteType = SpriteCellType.Type.valueOf("POWERUP_" + powerType.name());
                    img = spriteType.getSprite(0);
                } else {
                    img = SpriteCellType.Type.EMPTY.getSprite(0);
                }

                ImageIcon icon = new ImageIcon(img);
                
                setIcon(icon);
            }
            else {
                img = cell.type.getSprite(0);
                setIcon(new ImageIcon(img));
            }

            
        } else {
            setBackground(Color.WHITE);
        }
        setOpaque(true);
        return this;
    }

}
