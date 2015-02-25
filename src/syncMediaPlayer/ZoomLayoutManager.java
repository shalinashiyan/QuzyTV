package syncMediaPlayer;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

public class ZoomLayoutManager implements LayoutManager {

    private final int hgap = 16;
    private final int vgap = 16;

    private final int rows;
    private final int cols;

    private final List<Component> components;;

    private int focusIndex = -1;

    public ZoomLayoutManager(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.components = new ArrayList<Component>(rows * cols);
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
        components.add(comp);
    }

    @Override
    public void removeLayoutComponent(Component comp) {
        components.remove(comp);
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        return parent.getPreferredSize();
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return parent.getMinimumSize();
    }

    @Override
    public void layoutContainer(Container parent) {
        Insets insets = parent.getInsets();

        int width = parent.getWidth() - (insets.left + insets.right);
        int height = parent.getHeight() - (insets.top + insets.bottom);

        int cellWidth = (width - ((cols - 1) * hgap)) / cols;
        int cellHeight = (height - ((rows - 1) * vgap)) / rows;

        int i = 0;
        int x = insets.left;
        int y = insets.top;

        if(focusIndex == -1) {
            outer: for(int r = 0; r < rows; r++) {
                for(int c = 0; c < cols; c++) {
                    if(i < components.size()) {
                        Component comp = components.get(i++);
                        ((CardLayout)((JComponent) comp).getLayout()).show((Container) comp, "video");
                        comp.setBounds(x, y, cellWidth, cellHeight);
                        x += cellWidth + hgap;
                    }
                    else {
                        break outer;
                    }
                }
                x = insets.left;
                y += cellHeight + vgap;
            }
        }
        else {
            for(i = 0; i < components.size(); i++) {
                Component comp = components.get(i);
                if(i != focusIndex) {
                    comp.setBounds(0, 0, 1, 1);
                    ((CardLayout)((JComponent) comp).getLayout()).show((Container) comp, "hide");
                }
                else {
                    ((CardLayout)((JComponent) comp).getLayout()).show((Container) comp, "video");
                    comp.setBounds(x, y, width, height);
                }
            }
        }
    }

    public void focus(int index) {
        focusIndex = index;
    }

    public void blur() {
        focusIndex = -1;
    }
}
