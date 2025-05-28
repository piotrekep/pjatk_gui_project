package visual;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @class myFrame
 * @brief Klasa potrzebna do przechwycenia zamknięcia okna
 */
public class myFrame_old extends BaseWindow {

    public myFrame_old(String title, Runnable onExitAction) {
        super(title);
        
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onExitAction.run();
                dispose();
               
            }
        });


    }

}