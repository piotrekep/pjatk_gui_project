package visual;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class myFrame extends BaseWindow {

    public myFrame(String title, Runnable onExitAction) {
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