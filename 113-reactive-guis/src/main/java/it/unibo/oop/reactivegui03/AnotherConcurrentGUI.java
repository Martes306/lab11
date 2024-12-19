package it.unibo.oop.reactivegui03;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import it.unibo.oop.JFrameUtil;

import java.io.Serial;
import java.util.concurrent.TimeUnit;

/**
 * Second example of reactive GUI.
 */
@SuppressWarnings("PMD.AvoidPrintStackTrace")
public final class AnotherConcurrentGUI extends JFrame {

    @Serial
    private static final long serialVersionUID = 1L;
    private final JLabel display = new JLabel("0");
    private static final long WAITING_TIME = TimeUnit.SECONDS.toMillis(10);

    public AnotherConcurrentGUI(){
        super();
        JFrameUtil.dimensionJFrame(this);
        final JPanel panel = new JPanel();
        final JButton up = new JButton("Up");
        final JButton down = new JButton("Down");
        final JButton stop = new JButton("Stop");
        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        final Agent agent = new Agent();
        new Thread(agent).start();
        stop.addActionListener(e -> agent.stop(up, down));
        up.addActionListener(e -> agent.up());
        down.addActionListener(e -> agent.down());
        new Thread(() -> {
            try {
                Thread.sleep(WAITING_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            agent.stop(up, down);
        }).start();
        this.getContentPane().add(panel);
        this.setVisible(true);
    }

    private final class Agent implements Runnable{

        private volatile boolean stop;
        private boolean up;
        private boolean down;
        private int counter;

        @Override
        public void run() {
            while (!this.stop) {
                if(up){
                    try{
                    final var nextText = Integer.toString(this.counter);
                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(nextText));
                    this.counter++;
                    Thread.sleep(100);
                    } catch(Exception e){}
                } else if(down) {
                    try{
                        final var nextText = Integer.toString(this.counter);
                        SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(nextText));
                        this.counter--;
                        Thread.sleep(100);
                    } catch(Exception e){}
                }
            }

        }

        public void up(){
           this.up = true;
           this.down = false;
        }

        public void down(){
            this.down = true;
            this.up = false;
        }

        public void stop(final JButton up, final JButton down){
            this.stop = true;
            up.setEnabled(false);
            down.setEnabled(false);
        }
    }
}
