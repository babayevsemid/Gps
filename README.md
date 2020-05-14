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
        compile 'com.github.quentin7b:android-location-tracker:3.2'
}
```

### Use

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
                .setOnResumeConnect(true)
                .setOnPauseDisconnect(true)
                .setTrackingEnabled(false)
                .create();
```

