package controller;

import java.awt.KeyboardFocusManager;
import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;

public class ExitKeyHandler {
    
    public static void setupExitKeys() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
            
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    
                    if (e.getKeyCode() == KeyEvent.VK_Q && (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0 && (e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) != 0) {
                    System.exit(0);
                    return true;
                }
                }
                
                return false; 
            }
        });
    }
}