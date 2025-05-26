package controller;

import java.awt.KeyboardFocusManager;
import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;

public class ExitKeyHandler {
    private static Runnable restartCallback;
    
    public static void setupExitKeys(Runnable onRestart) {
        restartCallback = onRestart;

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
            
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    
                    if (e.getKeyCode() == KeyEvent.VK_Q && (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0 && (e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) != 0) {
                     if (restartCallback != null) {
                            restartCallback.run();
                        }
                    return true;
                }
                }
                
                return false; 
            }
        });
    }
}