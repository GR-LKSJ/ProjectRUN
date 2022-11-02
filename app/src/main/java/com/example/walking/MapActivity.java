package com.example.walking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapInfo;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class MapActivity extends AppCompatActivity {

    String API_Key = "l7xx0b116cc83dfd454b8b29b8a1a309791d";
    double slatitude;
    double slongtitude;
    double elatitude;
    double elongtitude;
    TMapView tMapView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent intent = getIntent();
        intent.getStringExtra("data");
        String[] array = intent.getStringExtra("data").split(" ");

        for(int i=0;i<array.length;i++) {
            System.out.println(array[i]);
        }

        slatitude = Double.parseDouble(array[2]);
        slongtitude = Double.parseDouble(array[3]);
        elatitude = Double.parseDouble(array[4]);
        elongtitude = Double.parseDouble(array[5]);

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

    //출발지 지정
    public TMapPoint getPoint1() {
        double latitude = slatitude;
        double longtitude = slongtitude;

        TMapPoint point = new TMapPoint(latitude, longtitude);
        return point;
    }

    //도착지 지정
    public TMapPoint getPoint2() {
        double latitude = elatitude;
        double longtitude = elongtitude;

        TMapPoint point = new TMapPoint(latitude, longtitude);
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

        TMapPoint point1 = getPoint1();
        TMapPoint point2 = getPoint2();
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