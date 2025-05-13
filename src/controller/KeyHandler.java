package controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    private volatile boolean up, down, left, right, test;

    public KeyHandler() {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        set(e, true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        set(e, false);
    }

    public void clear(){
        up=false;
        down=false;
        left=false;
        right=false;
        test=false;
    }

    private void set(KeyEvent e, boolean val) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> up = val;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> down = val;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> left = val;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> right = val;
            case KeyEvent.VK_Z -> test = val;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // throw new UnsupportedOperationException("Unimplemented method 'keyTyped'");
    }

    public KeyListener getKeyListener() {
        return this;
    }

    public boolean up() {
        return up;
    }

    public boolean down() {
        return down;
    }

    public boolean left() {
        return left;
    }

    public boolean right() {
        return right;
    }
    
    public boolean test() {
        return test;
    }
}
