package nctu.cs.cgv.itour.object;

import nctu.cs.cgv.itour.activity.MainActivity;

/**
 * Created by lobst3rd on 2017/8/4.
 */

public class SpotNode extends ImageNode {

    public String lat;
    public String lng;
    public int order;
    public String name;
    public String category;
    public CheckinNode checkinNode;

    public SpotNode(float x, float y, String lat, String lng, String name, int order) {
        super(x, y);
        if (MainActivity.spotCategory == null) MainActivity.spotCategory = new SpotCategory();
        this.lat = lat;
        this.lng = lng;
        this.order = order;
        this.name = name;
        this.category = MainActivity.spotCategory.getCategory(name);
        this.checkinNode = null;
    }
}
