import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.rsbot.bot.Bot;
import org.rsbot.script.*;

@ScriptManifest(authors={"Durka Durka Mahn"}, category="Misc", description="A small autotyper for RSBot.", version=0.1,name="AutoTyper")
public class AutoTyper extends Script {

    private int index, time;
    private boolean paused, started;
    private ArrayList<String> messages;
    private AutoTyperGUI gui;

    @Override
    public boolean onStart(Map<String, String> args) {
        paused = true;
        gui = new AutoTyperGUI();
        messages = new ArrayList<String>();
        gui.setVisible(true);
        return true;
    }

    @Override
    public void onFinish() {
        gui.dispose();
    }

    public int loop() {
        if(paused)
            return 15;
        if(index >= messages.size())
            index = 0;
        sendMessage(messages.get(index));
        index++;
        return random(time-250, time+250);
    }

    private synchronized void sendMessage(String text) {
        if(text.length() > 63)
            text = text.substring(0, 63);
        Bot.getInputManager().sendKeysInstant(text, true);
    }

    private void loadMessages(String unformatted) {
        messages.clear();
		messages.addAll(Arrays.asList(unformatted.split("\n")));
    }

    private class AutoTyperGUI extends javax.swing.JFrame {
		private static final long serialVersionUID = 1L;
		public AutoTyperGUI() {
            initComponents();
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        }
        private void initComponents() {

            jLabel1 = new javax.swing.JLabel();
            jScrollPane1 = new javax.swing.JScrollPane();
            messageBox = new javax.swing.JTextArea();
            jLabel2 = new javax.swing.JLabel();
            timeBox = new javax.swing.JTextField();
            jLabel3 = new javax.swing.JLabel();
            jLabel4 = new javax.swing.JLabel();
            startPauseButton = new javax.swing.JButton();
            resetButton = new javax.swing.JButton();
            resetButton.setEnabled(false);

            setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

            jLabel1.setText("The RSBot AutoTyper by Durka Durka Mahn - Enjoy!");

            messageBox.setColumns(63);
            messageBox.setRows(5);
            messageBox.setToolTipText("<html>Place the messages you want to be auto-typed here. Each new line will be used as a separate message.<br>\nAny chat effects (including the clan chat '/') should be placed here. <br>\n\n<b>Note that there is a 63 character limit per message - any longer and it will get cut off!</b></html>");
            jScrollPane1.setViewportView(messageBox);

            jLabel2.setText("Hover over a form to see how to use it.");

            timeBox.setText("2750");
            timeBox.setToolTipText("<html>Use this to set the time between each message. <br>\nThe minimum is 2750 - any lower and some of your messages may get cut off. <br>\nThe time will be randomized by +/- 250 milliseconds (.25 seconds) to make it less detectable. <br><br>\n\n1 second = 1000 milliseconds.</html>");
            timeBox.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    timeBoxFocusLost(evt);
                }
            });

            jLabel3.setText("Messages");

            jLabel4.setText("Time between messages (in Milliseconds)");

            startPauseButton.setText("Start");
            startPauseButton.setToolTipText("<html>Click this to start/pause the autotyper.<br>\nOnce started, the messages from the Messages box will be loaded into the autotyper,<br>\nand any changes made to the Messages box will be ignored unless you hit \"Reset\".</html>");
            startPauseButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    startPauseButtonActionPerformed(evt);
                }
            });

            resetButton.setText("Reset");
            resetButton.setToolTipText("Resets the autotyper, reloading the messages in the Messages text box.");
            resetButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    resetButtonActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(10, 10, 10)
                            .addComponent(jLabel2))
                        .addComponent(jLabel1)
                        .addComponent(jLabel3)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 527, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel4)
                                .addComponent(timeBox, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 209, Short.MAX_VALUE)
                            .addComponent(resetButton)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(startPauseButton)))
                    .addContainerGap())
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel1)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabel2)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabel3)
                    .addGap(3, 3, 3)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabel4)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(startPauseButton)
                            .addComponent(resetButton))
                        .addComponent(timeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            pack();
        }// </editor-fold>

        private void timeBoxFocusLost(java.awt.event.FocusEvent evt) {
            try {
                // int l = Integer.parseInt(timeBox.getText());
                timeBox.setBackground(Color.WHITE);
                timeBox.setForeground(Color.BLACK);
                startPauseButton.setEnabled(true);
                resetButton.setEnabled(started);
            } catch(NumberFormatException e) {
                timeBox.setBackground(Color.RED);
                timeBox.setForeground(Color.WHITE);
                startPauseButton.setEnabled(false);
                resetButton.setEnabled(false);
            }
        }

        private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {
            startPauseButton.setText("Start");
            paused = true;
            index = 0;
            loadMessages(messageBox.getText());
            time = Integer.parseInt(timeBox.getText());
            started = false;
        }

        private void startPauseButtonActionPerformed(java.awt.event.ActionEvent evt) {
            if(!started) {
                started = true;
                resetButton.setEnabled(true);
                loadMessages(messageBox.getText());
                time = Integer.parseInt(timeBox.getText());
            }
            startPauseButton.setText((paused = !paused) ? "Resume":"Pause");
        }

        // Variables declaration - do not modify
        private javax.swing.JLabel jLabel1;
        private javax.swing.JLabel jLabel2;
        private javax.swing.JLabel jLabel3;
        private javax.swing.JLabel jLabel4;
        private javax.swing.JScrollPane jScrollPane1;
        private javax.swing.JTextArea messageBox;
        private javax.swing.JButton resetButton;
        private javax.swing.JButton startPauseButton;
        private javax.swing.JTextField timeBox;
        // End of variables declaration

    }


}