// MatchIt.java

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.JOptionPane;
import java.util.Random;
import java.io.*;
import javax.imageio.ImageIO;


/**
 *  <p>This class will create a <i>MatchIt</i> object, that plays a 
 *  memory game in which users match pictures based on their memory </p>
 *
 *  <p>The game consists of 8 pairs of cards, arranged in a 4x4 grid, 
 *  that start out face down and shuffled randomly.  On the face of 
 *  the paired cards there are various pictures and the goal of the 
 *  game is to match two cards with the same picture until all 16 cards
 *  are matched.</P>
 *
 *  <p>To play the game, the player clicks on a card to reveal the picture
 *  on it. The timer of the game starts immediately after the first click.  
 *  The card remains face up, till the player clicks on another card. Once
 *  the second card is clicked, there are two possibilities:
 *  <ul>
 *  <li> The picture of this card matches the first card, in which case the 
 *  cards remain open for the remaining duration of the game and are 
 *  considered paired.</li> 
 *  <li> The second card, does not match the first card, in which case both 
 *  the cards return to their original state of being face down precisely 
 *  after 1 second. As such, the player in this scenario gets 1 second to 
 *  memorize the picture on the cards, along with their locations on the grid.
 *  </li>
 *  </ul>
 *  </p>
 *
 *  <p>In order to pair a card, the player needs to successively click on 
 *  two cards with the same picture. In essence, when the player clicks on a 
 *  card with a picture that had previously been revealed to him, he recalls 
 *  the location of the other pair and clicks on the position of the grid 
 *  where he previously saw the card in order to pair them.</p> 
 *  
 *
 *  @author Rajrupa Bakshi (Bonny)
 *  @version Last Modified May 3 2015
 */
public class MatchIt extends JFrame implements ActionListener
{
    // String Array of file names for pictures 
    private static String[] iconFile = {"img/dice64.png", "img/angrybird64.png", "img/Baseball-Ball64.png", 
                                "img/Football-Ball64.png", "img/mariobro64.png", 
                                "img/puzzle64.png", "img/mario64.png", "img/Jerry64.png", };
    
    /* Final variables */
    // width and height of the frame 
    private static final int UI_WIDTH = 550;
    private static final int UI_HEIGHT = 450;

    // number of rows and column for the grid
    private static final int ROW = 4;
    private static final int COL = 4;


    private static final int NUM_CARDS = ROW * COL;
    private static final int FLIP_TIME = 1000;
    /* End of Final variables */
    
    // counter to track number of clicks 
    private static int numClicks = 0;
    // odd click index
    private static int oddClickIndex = 0;
    // current click index
    private static int currentIndex = 0;
    // counter to track matched cards 
    private static int matched = 0;
    

    private JButton cards[];
    private ImageIcon coverIcon;
    private ImageIcon icons[];
    private Timer flipTimer;
    
    // JLabel to time the game 
    private JLabel timer;
    
    // button to exit game
    private JButton exit;

    // button to reset and play again
    private JButton play;

    // boolean value to run time 
    boolean runTime;

    // for TimeThread class 
    private int sec = 0;
    private int min = 0;

    private double bestMin = 60;
    
    private TimeThread t;

    // string to hold minutes and sec 
    private String timeStr;

    // label to display players current time 
    private JLabel currentTime;
    // label to display players best time 
    private JLabel bestTime;
    
    private JMenuItem menuExit;
    private JMenuItem menuHow;

    /**
     *  Constructs a new MatchIt and sets up the user interface and performs
     *  initialization. 
     */ 
    public MatchIt()  
    {
        // frame layout attributes
        setTitle("Match It");
        setSize(UI_WIDTH, UI_HEIGHT);
        setResizable(true);

        // Specify an action for the close button.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /* Set location based on the the user's screen 
        dimension. */
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int midWidth = (int) ( dim.getWidth() - UI_WIDTH ) / 2;
        int midHeight = (int) ( dim.getHeight() - UI_HEIGHT ) / 2;
        setLocation( midWidth, midHeight );
        
        Container c = getContentPane();

        Font f = new Font("Helvetica", Font.BOLD, 14);
        
        /***** Top title panel ****/
        TitlePanel top = new TitlePanel();
        c.add(top, BorderLayout.NORTH);
        top.setPreferredSize(new Dimension(UI_WIDTH, 30));

        JLabel name = new JLabel("MATCH IT");
        name.setFont(new Font("Myriad Pro", Font.BOLD, 20));
        top.add(name);

        /***** Base panel to hold all cards *****/ 
        BasePanel base = new BasePanel();
        base.setLayout(new GridLayout(ROW, COL, 6, 6));
        // give padding to base panel 
        base.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        
        /***** Panel on the east to hold buttons *****/
        BasePanel east = new BasePanel();
        GridBagLayout gb = new GridBagLayout(); 
        east.setLayout(gb);
        // set new dimensions for the east panel
        Dimension panelSize = new Dimension(200, UI_HEIGHT);
        east.setPreferredSize(panelSize);

    
        // add base and east panel to frame 
        c.add(base, BorderLayout.CENTER);
        c.add(east, BorderLayout.EAST);

        
        
        coverIcon = new ImageIcon("img/cover.gif");
        
        cards = new JButton[NUM_CARDS];
        
        icons = new ImageIcon[NUM_CARDS];
        
        
        int index = 0;
        for (int i = 0, j = 0; i < iconFile.length; i++) 
        {
            // add file at index to Image icons
            icons[index] = new ImageIcon(iconFile[i]);
            // new cards(buttons)
            cards[index] = new JButton("");
            // add action listener to cards (buttons)
            cards[index].addActionListener(this);
            // add cover icon
            cards[index].setIcon(coverIcon);
            // add card to base panel
            base.add(cards[index]);
            // increment index
            index++ ; 


            // repeat previous icon for the next image icon
            icons[index] = icons[index - 1];
            // new cards(buttons)
            cards[index] = new JButton("");
            // add action listener to cards (buttons)
            cards[index].addActionListener(this);
            // add cover icon
            cards[index].setIcon(coverIcon);
            // add card to base panel
            base.add(cards[index]);
            // increment index
            index++;

        }
        /**** Exit button ****/
        exit = new JButton("EXIT");
        exit.addActionListener(this);
        exit.setFont(f);

        /**** Play again button *****/
        play = new JButton("PLAY AGAIN");
        play.addActionListener(this);
        play.setFont(f);

        /**** Best time label *****/
        bestTime = new JLabel("BEST TIME 0 : 0");
        bestTime.setForeground(Color.WHITE);
        bestTime.setFont(f);
        
        /**** Current time label *****/
        currentTime = new JLabel("CURRENT TIME 0 : 0");
        currentTime.setForeground(Color.WHITE);
        currentTime.setFont(f);
       
        /**** Time label *****/
        timer = new JLabel("TIMER 0 : 0");
        timer.setForeground(Color.WHITE);
        timer.setFont(f);
        
        // Set gridbag layout for button on east panel
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridwidth = 1;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.insets = new Insets(2, 2, 2, 2);
        
        // set layout for timer 
        gc.gridx = 0;
        gc.gridy = 0;
        gc.anchor = GridBagConstraints.SOUTH;
        east.add(timer, gc);

        // set layout for best time 
        gc.gridx = 0;
        gc.gridy = 1;
        gc.anchor = GridBagConstraints.SOUTH;
        east.add(bestTime, gc);

        // set layout for current time 
        gc.gridx = 0;
        gc.gridy = 2;
        gc.anchor = GridBagConstraints.NORTH;
        east.add(currentTime, gc);
        
        // set layout for exit button
        gc.gridx = 0;
        gc.gridy = 3;
        gc.anchor = GridBagConstraints.SOUTH;
        east.add(exit, gc);

        // set layout for play button
        gc.gridx = 0;
        gc.gridy = 4;
        gc.anchor = GridBagConstraints.NORTH;
        east.add(play, gc);

        /**** Menu Bar *****/
        JMenuBar menuBar = new JMenuBar();
        // help menu
        JMenu help = new JMenu("Help");
        
        // How to play menu
        menuHow = new JMenuItem("How to play"); 
        menuHow.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) 
                {   
                    instructions();
                }
        });
        // Exit menu
        menuExit = new JMenuItem("Exit");
        menuExit.addActionListener(this);

        menuBar.add(help); 
        help.add(menuHow); 
        help.add(menuExit);

        // add menubar to frame
        setJMenuBar(menuBar);
        
        // shuffle before game starts 
        shuffle();
        
       
        // timer to flip the cards 
        flipTimer = new Timer(FLIP_TIME, new ActionListener(){
                public void actionPerformed(ActionEvent e) 
                {   
                    flipCard();
                }
            });  
        
        setVisible(true);
    }

    /**
     *  Inner class of MatchIt used for Title panel 
     *  with background image
     *         
     */
    public class TitlePanel extends JPanel
    {
        Image img;

        /**
         *  A constructor, which sets up all elements and 
         *  lays out the GUI for TitlePanel
         */
        public TitlePanel() 
        {
            // load the background image
            try 
            {
                img = ImageIO.read(new File("img/title.jpg"));
                        
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
        /**
         *  Paints the panel with the background image  
         */
        public void paintComponent( Graphics g ) 
        {
             // scale the Image component to the specified width and height
             int width = this.getWidth();
             int height = this.getHeight();
             g.drawImage( img, 0, 0, width, height, this );
        }
    }

    
    /**
     *  Inner class of MatchIt used for providing a base panel 
     *  with background image
     *       
     */
    public class BasePanel extends JPanel
    {
        Image img;

        /**
         *  The constructor, which sets up all elements and 
         *  lays out the GUI for BasePanel
         */
        public BasePanel() 
        {
            // load the background image
            try 
            {
                img = ImageIO.read(new File("img/background.jpg"));
                        
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
        /**
         *  Paints the panel with the background image  
         */
        public void paintComponent( Graphics g ) 
        {
             // scale the Image component to the specified width and height
             int width = this.getWidth();
             int height = this.getHeight();
             g.drawImage( img, 0, 0, width, height, this );
        }
    }

    /**
     *  Inner class of MatchIt used for calculating players 
     *  time in the game. 
     *       
     */
    public class TimeThread extends Thread implements Runnable
    {   
        public void run()
        { 
            while(runTime)
            {
                {
                    String second = Integer.toString(sec);
                    String minute = Integer.toString(min); 
                    timeStr = minute + " : " + second;
                    timer.setText("TIMER " + timeStr);
                    validate();
                    sec++; 
                    if(sec >= 60)
                    {
                        sec = 1;
                        min++;
                    }
                    try{Thread.sleep(1000);}
                    catch( InterruptedException e){}   
                }
            }
        }  
    }
       
    
    /**
     *  This method shuffles the image icons randomly
     *  
     */ 
    public void shuffle()
    {
        Random r = new Random();
        for (int i = 0; i < NUM_CARDS; i++) 
        {
            int randIndex = r.nextInt(NUM_CARDS);
            ImageIcon temp = icons[i];
            icons[i] = icons[randIndex];
            icons[randIndex] = temp;
        }
    }

    /**
     *  Resets all elements to its initial state and
     *  shuffles the cards by calling shuffle method 
     *       
     */
    public void resetGame()
    {  
        for (int i = 0; i < NUM_CARDS; i++) 
        {
            cards[i].addActionListener(this);
            cards[i].setIcon(coverIcon);

        }
        runTime = false;
        sec = 0;
        min = 0;
        timer.setText("TIMER 0 : 0");
        currentTime.setText("CURRENT TIME 0 : 0" );
        shuffle();
    }

    /**
     *  Flips over the cards to face down.      
     */
    public void flipCard()
    {
            
        cards[currentIndex].setIcon(coverIcon);
        cards[oddClickIndex].setIcon(coverIcon);
        flipTimer.stop();
    }

    /**
     *  Gives instructions to play the game       
     */
    private void instructions()
    {     
        JOptionPane.showMessageDialog( this, "There are 8 pairs of pictures that are arranged" 
           + " randomly in a 4 X 4 grid. \nTo play the game, click on a card and memorize"
           + " the picture and its \nlocation. Click another card, if the picture of this"
           + " card matches the\nfirst card, they are now paired, if not keep trying.\nTo win"
           + " the game, pair all 8 cards.");
    }
    /**
     *  Responds to button clicks and will 
     *  process the corresponding method.
     *
     *  @param  e   The ActionEvent caused by the button click and created by the system.
     */ 
    public void actionPerformed(ActionEvent e) 
    {
        
        if (flipTimer.isRunning())
        return;
        // if exit button is clicked 
        if( e.getSource() == exit || e.getSource() == menuExit ) 
        {   
            System.exit(0);
        }
        // if play button is clicked 
        if(e.getSource() == play)
        {
            resetGame();
        }
        else
        {
            if(runTime == false)
            {
                // run Time Thread
                t = new TimeThread();
                t.start();
                runTime = true; 
            }
            // get the index of the clicked button
            for (int i = 0; i < NUM_CARDS; i++) 
            {
                if (e.getSource() == cards[i]) 
                {
                    cards[i].setIcon(icons[i]);
                    currentIndex = i;
                }
            }
            processClick();
        }     
    } 

    /**
     *  Checks for win and displays a message to let 
     *  the user know that they have won that particular game 
     *    
     */
    public void winGame()
    {
        // if all 8 cards are paired
        if(matched == 8)
        {
            // stop timer
            runTime = false;
            // set current time to players total time taken
            currentTime.setText("CURRENT TIME " + timeStr);
            // process best time to compare current time with best
            bestTime();
            // reset matched counter 
            matched = 0;
            // display win message 
            JOptionPane.showMessageDialog( this, "You Won!!"); 

        }
        
    }

    /**
     *  Calculates and displays best time  
     *       
     */
    public void bestTime()
    { 
        sec = sec - 1;
        // total min taken by the player 
        double totalmin = min + (double)sec / 60;
        if(totalmin < bestMin)
        {
            bestMin = totalmin;
            // display best time
            bestTime.setText("BEST TIME " + min + " : " + sec);
        }
    }

    /**
     *  Process the button click by analyzing the odd and even click.
     *  Also checks for matching cards 
     *     
     */
    public void processClick()
    {
          
        numClicks++;
         
        // check for even click
        if (numClicks % 2 == 0) {
            // check whether same position is clicked twice
            if (currentIndex == oddClickIndex) {
                numClicks--;
                return;
            }

            // if icons are matching the cards are matched and remain open
            // current click icon file description
            String currentStr = icons[currentIndex].getDescription();
            // odd click icon file description
            String oddStr = icons[oddClickIndex].getDescription();
            if (currentStr.equals(oddStr)) 
            {
                matched++; 
                // disable action listener from the matched cards
                // so they cannot be clicked again 
                cards[currentIndex].removeActionListener(this);
                cards[oddClickIndex].removeActionListener(this);
                //check for win
                winGame();
            }
            else // if images are not matching
            {
                // show images for 1 sec, before flipping back
                flipTimer.start();
            }
        } else {
            // record index for odd clicks
            oddClickIndex = currentIndex;
        }
    } 
    public static void main(String[] args) 
    {
        MatchIt game = new MatchIt();
    }
}
