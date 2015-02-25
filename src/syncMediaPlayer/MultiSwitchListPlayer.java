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

import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.logger.Logger;
import uk.co.caprica.vlcj.medialist.MediaList;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.condition.Condition;
import uk.co.caprica.vlcj.player.condition.UnexpectedErrorConditionException;
import uk.co.caprica.vlcj.player.condition.UnexpectedFinishedConditionException;
import uk.co.caprica.vlcj.player.condition.conditions.PlayingCondition;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;
import uk.co.caprica.vlcj.player.list.MediaListPlayerEventAdapter;
import uk.co.caprica.vlcj.player.list.MediaListPlayerMode;

public class MultiSwitchListPlayer extends VLCAdaptor{
	

   private final MediaPlayerFactory mediaPlayerFactory;

   private final JFrame mainFrame;
   private final JPanel controlPanel;

   private final ZoomLayoutManager layoutManager;

   private final List<VideoPane> videoPanes = new ArrayList<VideoPane>();
   
   private MediaListPlayer mediaListPlayer;	
   private MediaList mediaList;


   public static void main(String[] args) {

       SwingUtilities.invokeLater(new Runnable() {
           @Override
           public void run() {
               try {
					new MultiSwitchListPlayer().start();
				} catch (UnexpectedErrorConditionException
						| UnexpectedFinishedConditionException
						| InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
           }
       });
   }

   private MultiSwitchListPlayer() {
	   
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
               
               if(mediaListPlayer != null) {
                   mediaListPlayer.release();
                   mediaListPlayer = null;
               }
               mediaPlayerFactory.release();
             
               System.exit(0);
           }
           
       });
      
       mainFrame.setVisible(true);
   }

   private void start() throws UnexpectedErrorConditionException, UnexpectedFinishedConditionException, InterruptedException {

	   
	   videoPanes.get(0).mediaPlayer().prepareMedia("test/Ideabytes.mp4");
//	   videoPanes.get(1).mediaPlayer().prepareMedia("test/coca-cola-christmas2014.mp4");
	   videoPanes.get(1).mediaPlayer().prepareMedia("test/chrisler.mp4");
       
       mediaListPlayer = mediaPlayerFactory.newMediaListPlayer();
       mediaList = mediaPlayerFactory.newMediaList();
       createMediaList();
       
       // Associate a mediaPlayer to the mediaListPlayer
       mediaListPlayer.setMediaPlayer(videoPanes.get(1).mediaPlayer());
       mediaListPlayer.addMediaListPlayerEventListener(new MediaListPlayerEventAdapter() {
           @Override
           public void nextItem(MediaListPlayer mediaListPlayer, libvlc_media_t item, String itemMrl) {
               System.out.println("nextItem()");
           }
       });
       mediaListPlayer.setMediaList(mediaList);
       mediaListPlayer.setMode(MediaListPlayerMode.LOOP);
       
       
       periodPlay();
   }
   
   
   public void periodPlay( ) throws UnexpectedErrorConditionException, UnexpectedFinishedConditionException, InterruptedException{
   	
   	long playTime = 25 * 1000;
   	long pauseTime = 35 * 1000; 
   	
   	Timer timer = new Timer();
   	  		
  	timer.schedule(new pauseTask(), playTime, playTime+pauseTime);
   	timer.schedule(new playTask(), 0, playTime+pauseTime);
    
   	
   }
   
   
	public void createMediaList(){
				
		// Create play list from 0 to n-1
		mediaList.addMedia("test/chrysler.mp4");
		mediaList.addMedia("test/coca-cola-christmas2014.mp4");
//		mediaList.addMedia("test/Apple-iPhone.mp4");
		
		  				      
        mediaList.addMedia("test/coke2012.mp4");
		
	}
   
   /*
    * 
    */ 
	private class pauseTask extends TimerTask {
	
		public void run(){
        
			videoPanes.get(0).mediaPlayer().pause();
			layoutManager.focus(videoPanes.get(1).id);
            controlPanel.revalidate();
			//videoPanes.get(1).mediaPlayer().play();
            mediaListPlayer.play();
		
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

