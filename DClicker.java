import java.util.ArrayList;
import java.util.Map;
import org.rsbot.script.*;

@ScriptManifest(authors = { "Durka Durka Mahn" }, category = "Misc", name = "DClicker", version = 2.35)
public class DClicker extends Script {

    private boolean paused, started;
    private int index;
    private ArrayList<Command> commands;
    private AutoClickerGUI gui;

    @Override
    public boolean onStart(Map<String, String> args) {
        gui = new AutoClickerGUI();
        commands = new ArrayList<Command>();
        paused = true;
        return true;
    }

    @Override
    public void onFinish() {
        gui.dispose();
    }

    public int loop() {
        if(paused)
            return 15;
        Command c = commands.get(index);
        c.click();
        index++;
        if(index >= commands.size())
            index = 0;
        return c.getWait();
    }

    private int randomSign() {
        return (random(0,2) == 1) ? 1:-1;
    }

    private void loadCommands(String commandText) {
        commands.clear();
        int lineNum = 0;
        for(String s : commandText.split("\n")) {
            if(!s.startsWith("/")) {
                try {
                    Command c = parse(s, lineNum);
                    commands.add(c);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            lineNum++;
        }
    }

    private Command parse(String command, int lineNumber) throws Exception {
        command = command.replaceAll(" ", "");
        boolean leftClick = true;
        if(command.startsWith("r") || command.startsWith("R")) {
            command = command.substring(1);
            leftClick = false;
        }
        String[] args = command.split(",");
        switch(args.length) {
            case 3:
                return new Command(Integer.parseInt(args[0]),Integer.parseInt(args[1]),0,0,Integer.parseInt(args[2]),Integer.parseInt(args[2])+1, leftClick);
            case 4:
                return new Command(Integer.parseInt(args[0]),Integer.parseInt(args[1]),0,0,Integer.parseInt(args[2]),Integer.parseInt(args[3]), leftClick);
            case 5:
                return new Command(Integer.parseInt(args[0]),Integer.parseInt(args[1]),Integer.parseInt(args[2]),Integer.parseInt(args[3]),Integer.parseInt(args[4]),Integer.parseInt(args[4])+1, leftClick);
            case 6:
                return new Command(Integer.parseInt(args[0]),Integer.parseInt(args[1]),Integer.parseInt(args[2]),Integer.parseInt(args[3]),Integer.parseInt(args[4]),Integer.parseInt(args[5]), leftClick);
        }
        throw new Exception("Invalid parameter count on line " + lineNumber);
    }

    private class Command {

        private int x, y, dx, dy, waitMin, waitMax;
        private boolean leftClick;

        public Command(int x, int y, int dx, int dy, int waitMin, int waitMax, boolean leftClick) {
            this.x = x;
            this.y = y;
            this.dx = dx;
            this.dy = dy;
            this.waitMin = waitMin;
            this.waitMax = waitMax;
            this.leftClick = leftClick;
        }

        public void click() {
            clickMouse(x + randomSign()*random(0,dx+1), y + randomSign()*random(0,dy+1), leftClick);
        }

        public int getWait() {
            return random(waitMin, waitMax);
        }

    }

    private class AutoClickerGUI extends javax.swing.JFrame {
		private static final long serialVersionUID = 1L;
		public AutoClickerGUI() {
            initComponents();
            setVisible(true);
        }
        private void initComponents() {

            jLabel2 = new javax.swing.JLabel();
            jLabel1 = new javax.swing.JLabel();
            jScrollPane1 = new javax.swing.JScrollPane();
            commandBox = new javax.swing.JTextArea();
            jLabel3 = new javax.swing.JLabel();
            helpButton = new javax.swing.JButton();
            startButton = new javax.swing.JButton();
            resetButton = new javax.swing.JButton();
            resetButton.setEnabled(false);

            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            setTitle("AutoClicker Window");
            setResizable(false);

            jLabel2.setText("Hover over a form to see how to use it.");

            jLabel1.setText("The RSBot AutoClicker by Durka Durka Mahn - Enjoy!");

            commandBox.setColumns(20);
            commandBox.setLineWrap(true);
            commandBox.setRows(5);
            commandBox.setToolTipText("Put each command on a separate line. Click the 'Help' button above to learn how to use commands.");
            jScrollPane1.setViewportView(commandBox);

            jLabel3.setText("Clicking Instructions");

            helpButton.setText("Help");
            helpButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    helpButtonActionPerformed(evt);
                }
            });

            startButton.setText("Start");
            startButton.setToolTipText("Starts, Pauses, and Resumes the script (depending on what the button says at the time).");
            startButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    startButtonActionPerformed(evt);
                }
            });

            resetButton.setText("Reset");
            resetButton.setToolTipText("Resets the autoclicker and reloads the commands.");
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
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(10, 10, 10)
                            .addComponent(jLabel2))
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel3)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(helpButton))
                        .addComponent(jScrollPane1))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(140, Short.MAX_VALUE)
                    .addComponent(resetButton)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(startButton)
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
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(helpButton))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(startButton)
                        .addComponent(resetButton))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            pack();
        }// </editor-fold>

        private void helpButtonActionPerformed(java.awt.event.ActionEvent evt) {
            new ACHelpWindow(this,true);
        }

        private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {
            if(!started) {
                started = true;
                resetButton.setEnabled(true);
                loadCommands(commandBox.getText());
            }
            startButton.setText((paused = !paused) ? "Resume":"Pause");
        }

        private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {
            started = false;
            paused = true;
            index = 0;
            startButton.setText("Start");
        }


        // Variables declaration - do not modify
        private javax.swing.JButton helpButton;
        private javax.swing.JLabel jLabel1;
        private javax.swing.JLabel jLabel2;
        private javax.swing.JLabel jLabel3;
        private javax.swing.JScrollPane jScrollPane1;
        private javax.swing.JTextArea commandBox;
        private javax.swing.JButton resetButton;
        private javax.swing.JButton startButton;
        // End of variables declaration

    }

    private class ACHelpWindow extends javax.swing.JDialog {
		private static final long serialVersionUID = 1L;

		public ACHelpWindow(java.awt.Frame parent, boolean modal) {
            super(parent, modal);
            initComponents();
            setVisible(true);
        }
        private void initComponents() {

            jLabel1 = new javax.swing.JLabel();

            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            setTitle("Help");
            setBackground(java.awt.Color.white);

            jLabel1.setText("<HTML> <h1>AutoClicker Command Help:</h1>\nUsing the AutoClicker commands is very easy, simply use any combo of these formats:<br><br> \n\nr?<br>\nx,y,wait<br>\nx,y,wait min,wait max<br>\nx,y,dx,dy,wait<br>\nx,y,dx,dy,wait min, wait max<br><br>\n\nSpaces will be ignored so feel free to throw them in where ever you want to, just make sure you use commas.<br><br>\n\nExplanation of commands:<br>\nr -> put an 'r' at the beginning of the command to make it right click instead of left click<br>\nx -> x coordinate of where the mouse should click<br>\ny -> y coordinate of where the mouse should click<br>\nwait -> amount of time the script should wait after clicking before performing the next click - NO DEVIATION<br>\ndx -> deviation in x coord (Deviation of 5 will randomize the x coordinate by +/- 5)<br>\ndy -> deviation in y coord (Deviation of 5 will randomize the y coordinate by +/- 5)<br>\nwait min -> wait deviation minimum (min of 1000 and max of 2000 will wait between 1000 and 1999)<br>\nwait max -> wait deviation maximum (see above)<br><br>\n\nSo, the command <i>300, 300, 5 , 10, 1000, 2000</i> would...<br>\n-Click at the coordinate (296 to 304, 291 to 309)<br>\n-Wait between 1 and 2 seconds before repeating<br><br>\n\nYou can put more than one command into the text box, and they will be executed in descending order.<br><br>\n\nTo get mouse coordinates, hit View -> Actual Mouse Position in your RSBot window. </HTML>");

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel1)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
            );

            pack();
        }// </editor-fold>

        private javax.swing.JLabel jLabel1;

    }

}  