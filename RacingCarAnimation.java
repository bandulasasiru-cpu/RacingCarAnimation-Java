import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Path2D;

public class RacingCarAnimation extends JPanel implements ActionListener {

    private final Timer timer;
    private double trackPosition = 0; // Moves the road forward
    private double carOffsetX = 0;      // Steering position (-1 to 1)
    private double steerAngle = 0;       // Angle for steering animation

    public RacingCarAnimation() {
        // Run loop at ~60 FPS
        timer = new Timer(16, this);
        timer.start();
        setBackground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Enable Anti-Aliasing for smooth, high-quality visuals
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int horizonY = height / 3;

        // 1. Draw Background (Sky & Sunset Gradient)
        drawSkyAndBackground(g2d, width, height, horizonY);

        // 2. Draw 3D Perspective Road & Side Markers
        draw3DRoad(g2d, width, height, horizonY);

        // 3. Draw Luxury Racing Car
        drawLuxuryCar(g2d, width / 2 + (int) (carOffsetX * 150), height - 120);
    }

    private void drawSkyAndBackground(Graphics2D g2d, int width, int height, int horizonY) {
        // Sunset Gradient Sky
        GradientPaint skyGradient = new GradientPaint(
                0, 0, new Color(25, 10, 45),
                0, horizonY, new Color(255, 90, 60)
        );
        g2d.setPaint(skyGradient);
        g2d.fillRect(0, 0, width, horizonY);

        // Neon Sun
        g2d.setColor(new Color(255, 215, 0, 200));
        g2d.fillOval(width / 2 - 80, horizonY - 100, 160, 160);

        // Distant Mountain Silhouettes
        g2d.setColor(new Color(40, 15, 55));
        int[] xPoints = {0, width / 4, width / 2, (3 * width) / 4, width};
        int[] yPoints = {horizonY, horizonY - 40, horizonY - 10, horizonY - 60, horizonY};
        g2d.fillPolygon(xPoints, yPoints, 5);

        // Grass Fields
        GradientPaint grassGradient = new GradientPaint(
                0, horizonY, new Color(15, 35, 15),
                0, height, new Color(5, 15, 5)
        );
        g2d.setPaint(grassGradient);
        g2d.fillRect(0, horizonY, width, height - horizonY);
    }

    private void draw3DRoad(Graphics2D g2d, int width, int height, int horizonY) {
        int numSegments = 100;

        for (int i = numSegments; i > 0; i--) {
            // Calculate perspective scale factor (Z-depth)
            double z1 = i + (trackPosition % 1);
            double z2 = (i - 1) + (trackPosition % 1);

            int y1 = horizonY + (int) (Math.pow(z1 / numSegments, 2) * (height - horizonY));
            int y2 = horizonY + (int) (Math.pow(z2 / numSegments, 2) * (height - horizonY));

            // Road width widens as it approaches the viewer
            int w1 = (int) (width * 0.02 + (z1 / numSegments) * (width * 0.75));
            int w2 = (int) (width * 0.02 + (z2 / numSegments) * (width * 0.75));

            int xCenter = width / 2;

            // Alternating Road Colors (Depth perception)
            boolean isEven = ((int) (i + trackPosition) % 2 == 0);

            // Draw Road Surface
            g2d.setColor(isEven ? new Color(40, 42, 48) : new Color(30, 32, 38));
            Polygon roadSeg = new Polygon(
                    new int[]{xCenter - w1, xCenter + w1, xCenter + w2, xCenter - w2},
                    new int[]{y1, y1, y2, y2}, 4
            );
            g2d.fill(roadSeg);

            // Red & White Side Rumble Strips
            g2d.setColor(isEven ? new Color(220, 20, 60) : Color.WHITE);
            int stripW1 = Math.max(2, w1 / 10);
            int stripW2 = Math.max(2, w2 / 10);

            Polygon leftStrip = new Polygon(
                    new int[]{xCenter - w1 - stripW1, xCenter - w1, xCenter - w2, xCenter - w2 - stripW2},
                    new int[]{y1, y1, y2, y2}, 4
            );
            Polygon rightStrip = new Polygon(
                    new int[]{xCenter + w1, xCenter + w1 + stripW1, xCenter + w2 + stripW2, xCenter + w2},
                    new int[]{y1, y1, y2, y2}, 4
            );
            g2d.fill(leftStrip);
            g2d.fill(rightStrip);

            // Center Dashed White Line
            if (isEven) {
                g2d.setColor(new Color(255, 215, 0)); // Golden/Yellow center line
                int lineW1 = Math.max(1, w1 / 30);
                int lineW2 = Math.max(1, w2 / 30);
                Polygon centerLine = new Polygon(
                        new int[]{xCenter - lineW1, xCenter + lineW1, xCenter + lineW2, xCenter - lineW2},
                        new int[]{y1, y1, y2, y2}, 4
                );
                g2d.fill(centerLine);
            }
        }
    }

    private void drawLuxuryCar(Graphics2D g2d, int cx, int cy) {
        // Shadow beneath the car
        g2d.setColor(new Color(0, 0, 0, 140));
        g2d.fillOval(cx - 110, cy + 50, 220, 35);

        // Rear Wide Tires
        g2d.setColor(new Color(20, 20, 20));
        g2d.fillRoundRect(cx - 115, cy + 20, 35, 45, 10, 10); // Left tire
        g2d.fillRoundRect(cx + 80, cy + 20, 35, 45, 10, 10);  // Right tire

        // Main Car Body (Metallic Blue Luxury Finish)
        GradientPaint bodyGradient = new GradientPaint(
                cx - 100, cy, new Color(0, 150, 255),
                cx + 100, cy + 40, new Color(5, 30, 90)
        );
        g2d.setPaint(bodyGradient);
        g2d.fillRoundRect(cx - 100, cy, 200, 50, 20, 20);

        // Aerodynamic Rear Wing / Spoiler
        g2d.setColor(new Color(15, 15, 15));
        g2d.fillRect(cx - 90, cy - 10, 15, 20); // Left wing support
        g2d.fillRect(cx + 75, cy - 10, 15, 20); // Right wing support

        GradientPaint spoilerGradient = new GradientPaint(
                cx, cy - 20, new Color(255, 50, 50),
                cx, cy - 10, new Color(120, 0, 0)
        );
        g2d.setPaint(spoilerGradient);
        g2d.fillRoundRect(cx - 105, cy - 20, 210, 12, 5, 5);

        // Cabin Glass / Rear Windshield with reflection
        GradientPaint glassGradient = new GradientPaint(
                cx, cy - 25, new Color(40, 50, 70),
                cx, cy, new Color(10, 15, 25)
        );
        g2d.setPaint(glassGradient);

        Path2D cabin = new Path2D.Double();
        cabin.moveTo(cx - 65, cy);
        cabin.lineTo(cx - 45, cy - 25);
        cabin.lineTo(cx + 45, cy - 25);
        cabin.lineTo(cx + 65, cy);
        cabin.closePath();
        g2d.fill(cabin);

        // LED Neon Taillights (Luxury Supercar style)
        g2d.setColor(new Color(255, 0, 50));
        g2d.fillRoundRect(cx - 85, cy + 10, 50, 10, 5, 5);
        g2d.fillRoundRect(cx + 35, cy + 10, 50, 10, 5, 5);

        // Taillight Glow
        g2d.setColor(new Color(255, 0, 50, 80));
        g2d.fillOval(cx - 95, cy + 2, 70, 25);
        g2d.fillOval(cx + 25, cy + 2, 70, 25);

        // Dual Exhaust Pipes with Flame Glow
        g2d.setColor(new Color(180, 180, 180));
        g2d.fillOval(cx - 30, cy + 38, 12, 12);
        g2d.fillOval(cx + 18, cy + 38, 12, 12);

        // Blue Nitro Exhaust Flame
        g2d.setColor(new Color(0, 200, 255, 180));
        g2d.fillOval(cx - 28, cy + 44, 8, 15);
        g2d.fillOval(cx + 20, cy + 44, 8, 15);

        // License Plate / Brand Emblem
        g2d.setColor(new Color(220, 220, 220));
        g2d.fillRect(cx - 20, cy + 15, 40, 12);
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 8));
        g2d.drawString("Mitsubisi", cx - 16, cy + 24);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Move track forward
        trackPosition += 0.15;

        // Simulate subtle luxury car steering/sway movement
        steerAngle += 0.03;
        carOffsetX = Math.sin(steerAngle) * 0.4;

        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("3D Luxury Racing Car Animation");
            RacingCarAnimation panel = new RacingCarAnimation();

            frame.add(panel);
            frame.setSize(900, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
