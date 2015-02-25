package syncMediaPlayer;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;

@SuppressWarnings("serial")
public class VideoPane extends JPanel {

    public final int id;

    private final EmbeddedMediaPlayerComponent mediaPlayer;

    VideoPane(int id, final MediaPlayerFactory mediaPlayerFactory) {
        this.id = id;

        setBackground(Color.black);
        setBorder(BorderFactory.createLineBorder(Color.white));

        setLayout(new CardLayout());

        JPanel vp = new JPanel();
        vp.setLayout(new BorderLayout());
        vp.setBackground(Color.pink);

        JPanel top = new JPanel();
        top.setBorder(BorderFactory.createEmptyBorder(2,  6,  2,  6));
        top.setBackground(Color.black);
        top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));

        JLabel label = new JLabel(String.valueOf(id+1));
        label.setForeground(Color.white);
        label.setFont(new Font("Sansserif", Font.BOLD, 12));
        top.add(label);
        vp.add(top, BorderLayout.NORTH);

        mediaPlayer = new EmbeddedMediaPlayerComponent() {
            @Override
            protected MediaPlayerFactory onGetMediaPlayerFactory() {
                return mediaPlayerFactory;
            }
        };

        vp.add(mediaPlayer, BorderLayout.CENTER);

        add(vp, "video");

        JPanel p = new JPanel();
        p.setBackground(Color.pink);
        add(p, "hide");
    }

    MediaPlayer mediaPlayer() {
        return mediaPlayer.getMediaPlayer();
    }
}

