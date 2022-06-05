package com.example.walking;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapInfo;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class MainActivity extends AppCompatActivity {

    String API_Key = "l7xx0b116cc83dfd454b8b29b8a1a309791d";

    TMapView tMapView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       LinearLayout linearLayoutTmap = (LinearLayout) findViewById(R.id.linearLayoutTmap);

        tMapView = new TMapView(this);

        tMapView.setSKTMapApiKey(API_Key);

        linearLayoutTmap.addView(tMapView);

        tMapView.setZoomLevel(17);
        tMapView.setIconVisibility(true);
        tMapView.setMapType(TMapView.MAPTYPE_STANDARD);
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN);

        findPathDataAllType(TMapData.TMapPathType.PEDESTRIAN_PATH);
    }

    private String totalDistance = null;
    private String totalTime = null;
    private String totalFare = null;

    public TMapPoint randomTMapPoint() {
        double latitude = ((double) Math.random()) * (37.575113 - 37.483086) + 37.483086;
        double longitude = ((double) Math.random()) * (127.027359 - 126.878357) + 126.878357;

        latitude = Math.min(37.575113, latitude);
        latitude = Math.max(37.483086, latitude);

        longitude = Math.min(127.027359, longitude);
        longitude = Math.max(126.878357, longitude);

        //LogManager.printLog("randomTMapPoint" + latitude + " " + longitude);

        TMapPoint point = new TMapPoint(latitude, longitude);

        return point;
    }

    public TMapPoint randomTMapPoint2() {
        double latitude = ((double) Math.random()) * (37.770555 - 37.404194) + 37.483086;
        double longitude = ((double) Math.random()) * (127.426043 - 126.770296) + 126.878357;

        latitude = Math.min(37.770555, latitude);
        latitude = Math.max(37.404194, latitude);

        longitude = Math.min(127.426043, longitude);
        longitude = Math.max(126.770296, longitude);

        //LogManager.printLog("randomTMapPoint" + latitude + " " + longitude);

        TMapPoint point = new TMapPoint(latitude, longitude);

        return point;
    }

    private String getContentFromNode(Element item, String tagName) {
        NodeList list = item.getElementsByTagName(tagName);
        if (list.getLength() > 0) {
            if (list.item(0).getFirstChild() != null) {
                return list.item(0).getFirstChild().getNodeValue();
            }
        }
        return null;
    }


    private void findPathDataAllType(final TMapData.TMapPathType type) {
        totalDistance = null;
        totalTime = null;
        totalFare = null;

        //TMapPoint point1 = tMapView.getCenterPoint();
        TMapPoint point1 = new TMapPoint(37.519241, 127.067861);
        TMapPoint point2 = null;
        if (type == TMapData.TMapPathType.PEDESTRIAN_PATH) {
            point2 = new TMapPoint(37.501843, 127.081138);
            //point2 = randomTMapPoint2();
        } else {
            point2 = randomTMapPoint();
        }
        TMapData tmapdata = new TMapData();

        tmapdata.findPathDataAllType(type, point1, point2, new TMapData.FindPathDataAllListenerCallback() {
            @Override
            public void onFindPathDataAll(Document doc) {
                TMapPolyLine polyline = new TMapPolyLine();
                polyline.setLineWidth(12);
                polyline.setLineColor(Color.RED);
                if (doc != null) {
                    NodeList list = doc.getElementsByTagName("Document");
                    Element item2 = (Element) list.item(0);
                    totalDistance = getContentFromNode(item2, "tmap:totalDistance");
                    totalTime = getContentFromNode(item2, "tmap:totalTime");
                    if (type == TMapData.TMapPathType.CAR_PATH) {
                        totalFare = getContentFromNode(item2, "tmap:totalFare");
                    }

                    NodeList list2 = doc.getElementsByTagName("LineString");

                    for (int i = 0; i < list2.getLength(); i++) {
                        Element item = (Element) list2.item(i);
                        String str = getContentFromNode(item, "coordinates");
                        if (str == null) {
                            continue;
                        }

                        String[] str2 = str.split(" ");
                        for (int k = 0; k < str2.length; k++) {
                            try {
                                String[] str3 = str2[k].split(",");
                                TMapPoint point = new TMapPoint(Double.parseDouble(str3[1]), Double.parseDouble(str3[0]));
                                polyline.addLinePoint(point);
                            } catch (Exception e) {

                            }
                        }
                    }

                    TMapInfo info = tMapView.getDisplayTMapInfo(polyline.getLinePoint());
                    int zoom = info.getTMapZoomLevel();
                    if (zoom > 12) {
                        zoom = 15;
                    }

                    tMapView.setZoomLevel(zoom);
                    tMapView.setCenterPoint(info.getTMapPoint().getLongitude(), info.getTMapPoint().getLatitude());
                    tMapView.addTMapPath(polyline);
                }
            }
        });
    }
}
