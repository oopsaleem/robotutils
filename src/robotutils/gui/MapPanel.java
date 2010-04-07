/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package robotutils.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.MemoryImageSource;
import java.util.LinkedHashMap;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

/**
 * A display widget that can display a map background and icon overlays.  It
 * support mouse-based panning and zooming.
 * @author pkv
 */
public class MapPanel extends JPanel {
    public static final double SCALE_FACTOR = 1.2;

    private final LinkedHashMap<String, MapIcon> icons = new LinkedHashMap<String, MapIcon>();
    private Image background;

    private final Point2D mapOffset = new Point2D.Double(0.0, 0.0);
    private final Point2D mapScale = new Point2D.Double(1.0, 1.0);
    private final AffineTransform mapImageTransform = new AffineTransform();

    private Point currPoint;
    private Point downPoint;
    private final Point2D offset = new Point2D.Double(0.0, 0.0);
    private final Point2D scale = new Point2D.Double(1.0, 1.0);
    private final MapMouseListener mt = new MapMouseListener();
    private final MapComponentListener cl = new MapComponentListener();

    {
        this.addMouseListener(mt);
        this.addMouseMotionListener(mt);
        this.addMouseWheelListener(mt);
        this.addComponentListener(cl);
    }

    private class MapIcon {
        Image icon;
        final AffineTransform xform = new AffineTransform();
    }

    public void setMapRect(double left, double right, double top, double bottom) {
        mapOffset.setLocation(-(left + right)/2, -(top + bottom)/2);
        mapScale.setLocation(1/(right - left), 1/(bottom - top));
    }

    public void setMapImage(Image img) {
        background = img;

        int width = background.getWidth(this);
        int height = background.getHeight(this);
        if (width > 0 && height > 0) {
            setMapTransform(width, height);
        } else {
            mapImageTransform.setToIdentity();
            System.err.println("Need to use ImageObserver to set transform.");
        }
    }

    private void setMapTransform(int width, int height) {
        mapImageTransform.setToIdentity();
        mapImageTransform.scale(1/(double)width, 1/(double)height);
        mapImageTransform.translate(-width/2, -height/2);
    }

    public Image makeDot(int width, int height, Color c) {
        int[] pixels = new int [width*height];

        // Fill in pixels according to an ellipse equation
        for (int index=0, y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                boolean inEllipse = 4.0*(x - width/2)*(x - width/2)/(width * width + 1.0) +
                        4.0*(y - height/2)*(y - height/2)/(height * height + 1.0) <= 1.0;

                pixels[index++] = (inEllipse) ? c.getRGB() : 0;
            }
        }

        return this.createImage( new MemoryImageSource(width, height, pixels, 0, width) );
    }

    public void setDotIcon(String name, Color c, int w, int h, double x, double y, double s) {
        setIcon(name, makeDot(w, h, c), s, x, y, 0.0);
    }

    public void setIcon(String name, Image img, double s, double x, double y, double th) {
        MapIcon mi = getIcon(name);
        mi.icon = img;
        setIconTransform(mi, x, y, th, s);
        this.repaint();
    }

    public void setIcon(String name, Image img, double x, double y) {
        setIcon(name, img, 1.0, x, y, 0.0);
    }

    public void setIcon(String name, double x, double y, double t) {
        MapIcon mi = getIcon(name);
        setIconTransform(mi, x, y, t, 1.0);
        this.repaint();
    }

    private MapIcon getIcon(String name) {
        synchronized(icons) {
            if (icons.containsKey(name)) {
                return icons.get(name);
            } else {
                MapIcon mi = new MapIcon();
                icons.put(name, mi);
                return mi;
            }
        }
    }

    private void setIconTransform(MapIcon mi, double x, double y, double theta, double scale) {
        int width = mi.icon.getWidth(this);
        int height = mi.icon.getHeight(this);

        mi.xform.setToIdentity();
        mi.xform.translate(x - scale*width/2, y - scale*height/2);
        mi.xform.rotate(theta);
        mi.xform.scale(scale, scale);
    }

    public void removeIcon(String name) {
        synchronized(icons) {
            icons.remove(name);
            this.repaint();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
        // Apply transform from frame coords to image coords
        g2.translate(this.getWidth()/2, this.getHeight()/2);
        if (downPoint != null && currPoint != null) {
            g2.translate(currPoint.getX() - downPoint.getX(),
                    currPoint.getY() - downPoint.getY());
        }
        g2.scale(scale.getX(), scale.getY());
        g2.translate(offset.getX(), offset.getY());

        // Draw the map in the window
        drawMap(g2);

        // Apply transform from image coords to map coords
        g2.scale(mapScale.getX(), mapScale.getY());
        g2.translate(mapOffset.getX(), mapOffset.getY());

        // Draw overlays in the window
        synchronized(icons) {
            for (MapIcon mi : icons.values()) {
                drawIcon(mi, g2);
            }
        }

        g2.dispose();
    }

    private void drawMap(Graphics2D g) {
        if (background == null) return;
        if (!mapImageTransform.isIdentity()) {
            g.drawImage(background, mapImageTransform, this);
        }
    }

    private void drawIcon(MapIcon mi, Graphics2D g) {
        if (mi.icon == null) return;
        if (!mi.xform.isIdentity()) {
            g.drawImage(mi.icon, mi.xform, this);
        }
    }

    private class MapComponentListener implements ComponentListener {
        public void componentResized(ComponentEvent e) {
            scale.setLocation(MapPanel.this.getWidth(), MapPanel.this.getHeight());
        }

        public void componentMoved(ComponentEvent e) {}
        public void componentShown(ComponentEvent e) {}
        public void componentHidden(ComponentEvent e) {}
    }

    private class MapMouseListener implements MouseInputListener, MouseWheelListener {

        public void mouseDragged(MouseEvent e) {
            currPoint = e.getPoint();
            MapPanel.this.repaint();
        }

        public void mouseMoved(MouseEvent e) {
            currPoint = e.getPoint();
        }

        public void mouseWheelMoved(MouseWheelEvent e) {
            double exp = Math.pow(SCALE_FACTOR, e.getWheelRotation());
            scale.setLocation(scale.getX()*exp, scale.getY()*exp);
            MapPanel.this.repaint();
        }

        public void mouseClicked(MouseEvent evt) {
            Point2D mousePos = new Point2D.Double(evt.getX(), evt.getY());

            AffineTransform at = new AffineTransform();
            at.translate(MapPanel.this.getWidth()/2, MapPanel.this.getHeight()/2);
            if (downPoint != null && currPoint != null) {
                at.translate(currPoint.getX() - downPoint.getX(),
                        currPoint.getY() - downPoint.getY());
            }
            at.scale(scale.getX(), scale.getY());
            at.translate(offset.getX(), offset.getY());

            Point2D map = null;
            try {
                map = at.inverseTransform(mousePos, null);
                map = mapImageTransform.inverseTransform(map, null);
            } catch (NoninvertibleTransformException e) {}
            
            at.scale(mapScale.getX(), mapScale.getY());
            at.translate(mapOffset.getX(), mapOffset.getY());

            Point2D world = null;
            try { world = at.inverseTransform(mousePos, null); }
            catch (NoninvertibleTransformException e) {}
            
            System.out.println("Map: [" + map + "], " +
                    "World: [" + world + "]");
        }

        public void mousePressed(MouseEvent e) {
            downPoint = e.getPoint();
        }

        public void mouseReleased(MouseEvent e) {
            double dx = (e.getPoint().getX() - downPoint.getX())/scale.getX();
            double dy = (e.getPoint().getY() - downPoint.getY())/scale.getY();
            offset.setLocation(offset.getX() + dx, offset.getY() + dy);
            downPoint = null;
            MapPanel.this.repaint();
        }

        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
    }
}
