package controller;

import java.awt.KeyboardFocusManager;
import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;

/**
 * @class ExitKeyHandler
 * @brief Implementacja skrótu ctrl+shift+q do restartu
 *
 *        Klasa implementuje skrót klawiszowy do restartu aplikacji.
 *        Wywołuje obiekt Runnable w momencie gdy zarejestruje even klawiatury
 */

public class ExitKeyHandler {
    /**
     * Obiekt runnable wywoływany przy naciśnięciu
     */
    private static Runnable restartCallback;

    /**
     * Metoda obsługuje zdarzenia klawiatury na poziomie globalnym
     * Metoda sprawdza czy nasąpiło zdarzenie KEY_PRESSED, został naciśnięty
     * przycisk Q z modyfikatorami CTRL i Shift
     * 
     * Jeśli wszystkie warunki są spełnione i callback nie jest null,
     * wykonywany jest callback i metoda zwraca true
     */

    public static void setupExitKeys(Runnable onRestart) {

        restartCallback = onRestart;

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {

                if (e.getID() == KeyEvent.KEY_PRESSED) {

                    if (e.getKeyCode() == KeyEvent.VK_Q && (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0
                            && (e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) != 0) {
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