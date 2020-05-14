# Android location tracker

### Installation

Add this to your ```build.gradle``` file

```
repositories {
    maven {
        url "https://jitpack.io"
    }
}

dependencies {
    implementation 'com.github.babayevsemid:Gps:0.0.1'
}
```

### Use with AppCompatActivity

Note: It works if location permission is active, if it is not, try to give permission

```
GpsManager.LocationCallback callback = new GpsManager.LocationCallback() {
            @Override
            public void onNewLocationAvailable(double lat, double lon) {
                Log.e("onNewLocationAvailable", lat + "," + lon);
            }

            @Override
            public void onLastKnownLocation(double lat, double lon) {
                Log.e("onLastKnownLocation", lat + "," + lon);
            }

            @Override
            public void onNotAvailable() {
                Log.e("onNotAvailable", "onNotAvailable");
            }
        };

        new GpsManager.Builder()
                .setActivity(this)
                .setDistance(1)
                .setUpdateTime(2000)
                .setListener(callback)
                .setTrackingEnabled(true)
                .setOnResumeConnect(true)
                .setOnPauseDisconnect(true)
                .create();
```

### Use with Context

Note: Works if GPS is active, otherwise not works

```
GpsManager.LocationCallback callback = new GpsManager.LocationCallback() {
            @Override
            public void onNewLocationAvailable(double lat, double lon) {
                Log.e("onNewLocationAvailable", lat + "," + lon);
            }

            @Override
            public void onLastKnownLocation(double lat, double lon) {
                Log.e("onLastKnownLocation", lat + "," + lon);
            }

            @Override
            public void onNotAvailable() {
                Log.e("onNotAvailable", "onNotAvailable");
            }
        };

        new GpsManager.Builder()
                .setContext(getApplicationContext())
                .setDistance(1)
                .setUpdateTime(2000) 
                .setListener(callback) 
                .setTrackingEnabled(true)
                .create();
```

### Defaults
        * int distance = 10; //Meter
        * int updateTime = 2000;
        * boolean trackingEnabled;
        * boolean onResumeConnect = true;
        * boolean onPauseDisconnect = true;


