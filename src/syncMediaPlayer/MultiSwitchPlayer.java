package syncMediaPlayer;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import uk.co.caprica.vlcj.logger.Logger;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.condition.UnexpectedErrorConditionException;
import uk.co.caprica.vlcj.player.condition.UnexpectedFinishedConditionException;

public class MultiSwitchPlayer extends VLCAdaptor{
	
    private static final String[] medias = {
   	 "test/Tours.mp4",
	  "test/Ideabytes.mp4",
       // Put your MRL's here, this example uses 12
   };

   private final MediaPlayerFactory mediaPlayerFactory;

   private final JFrame mainFrame;
   private final JPanel controlPanel;

   private final ZoomLayoutManager layoutManager;

   private final List<VideoPane> videoPanes = new ArrayList<VideoPane>();

   public static void main(String[] args) {

       SwingUtilities.invokeLater(new Runnable() {
           @Override
           public void run() {
               try {
					new MultiSwitchPlayer().start();
				} catch (UnexpectedErrorConditionException
						| UnexpectedFinishedConditionException
						| InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
           }
       });
   }

   private MultiSwitchPlayer() {
	   
	   mediaPlayerFactory = new MediaPlayerFactory();	   
	   
       controlPanel = new JPanel();
       controlPanel.setBackground(Color.black);
       controlPanel.setBorder(new EmptyBorder(16, 16, 16, 16));

       int rows = 1; // Change this to suit
       int cols = 2;

       layoutManager = new ZoomLayoutManager(rows, cols);
       controlPanel.setLayout(layoutManager);

       for(int i = 0; i < rows * cols; i++) {
           VideoPane videoPane = new VideoPane(i, mediaPlayerFactory);
           videoPanes.add(videoPane);
           controlPanel.add(videoPane, String.valueOf(i));
       }

       mainFrame = new JFrame("VLC Player");
       mainFrame.setIconImage(new ImageIcon("icons/vlcj-logo.png").getImage());
       mainFrame.setContentPane(controlPanel);
       mainFrame.setSize(1000, 600);
       
       mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       mainFrame.addWindowListener(new WindowAdapter() {
           @Override
           public void windowClosing(WindowEvent evt) {
               Logger.debug("windowClosing(evt={})", evt);
               
               for ( int i = 0; i < videoPanes.size(); i++) {
                   videoPanes.get(i).mediaPlayer().release();
               }
               
               mediaPlayerFactory.release();
             
               System.exit(0);
           }
           
       });
      
       mainFrame.setVisible(true);
   }

   private void start() throws UnexpectedErrorConditionException, UnexpectedFinishedConditionException, InterruptedException {


       for(int i = 0; i < videoPanes.size() && i < medias.length; i++) {
           videoPanes.get(i).mediaPlayer().prepareMedia(medias[i]);
       }
       
       periodPlay();
   }
   
   
   public void periodPlay( ) throws UnexpectedErrorConditionException, UnexpectedFinishedConditionException, InterruptedException{
   	
   	long playTime = 15*1000;
   	long pauseTime = 5 * 1000; 
   	
   	Timer timer = new Timer();
   	  		
  	timer.schedule(new pauseTask(), playTime, playTime+pauseTime);
   	timer.schedule(new playTask(), 0, playTime+pauseTime);
    
   	
   }
   
   /*
    * 
    */ 
	private class pauseTask extends TimerTask {
	
		public void run(){
        
			videoPanes.get(0).mediaPlayer().pause();
			layoutManager.focus(videoPanes.get(1).id);
            controlPanel.revalidate();
			videoPanes.get(1).mediaPlayer().play();
		
			System.out.println("pause media in timer");

		}	
	}

	/*
	 * 
	 */
	private class playTask extends TimerTask {
	
		public void run(){

			videoPanes.get(1).mediaPlayer().pause();
			layoutManager.focus(videoPanes.get(0).id);
			controlPanel.revalidate();
			videoPanes.get(0).mediaPlayer().play();
   		
			System.out.println("play media in timer");

   		}	
	}
	

	
}

