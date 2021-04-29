package Models;

import java.awt.*;

public class Site {
    int siteId;
    Point position;
    int radius;
    SiteStatus siteStatus;

    public Site(int siteId, int x, int y, int radius) {
        this.siteId = siteId;
        this.position = new Point(x, y);
        this.radius = radius;
        siteStatus = new SiteStatus();
    }

    public int getSiteId() {
        return siteId;
    }

    public Point getPosition() {
        return position;
    }
    
    public int getRadius() {
        return radius;
    }

    public SiteStatus getSiteStatus() {
        return siteStatus;
    }
}
