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
    
    //And Play services location
    implementation 'com.google.android.gms:play-services-location:17.0.0'
}
```

### Use with AppCompatActivity

Note: Returns location if GPS is active, otherwise will prompt to enable it and return consequently

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
                .setTrackingEnabled(true)
                .setWithBackgoundPermission(false)
                .create();
```

### Use with Context

Note: Works if GPS is active, otherwise will not function

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


### setWithBackgoundPermission false

![alt text](screenshots/simple.gif?raw=true)
 
### setWithBackgoundPermission true

![alt text](screenshots/all_time.gif?raw=true)


### Defaults
```
        * distance - 10; //10 Meter
        * updateTime - 2000; //2 sec
        * trackingEnabled - false;
        * onResumeConnect - true;
        * onPauseDisconnect - true;
        * withBackgoundPermission - true;
```
 
 
## Extra Info

* Check location permission
```
            if(GpsPermission.checkLocation(getApplicationContext(), false)){
            
            }
```
            
* Check background location permission
```
        //boolean checkLocation(Context context, boolean withBackground)

            if(GpsPermission.checkLocation(getApplicationContext(), true)){
            
            }
```
            
* Check GPS is Enabled
```
        //boolean isGpsEnabled(Context context)
            
            if(GpsPermission.isGpsEnabled(getApplicationContext())){
            
            }
```
    
* Request location permission
``` 
        //MutableLiveData<Boolean> requestLocation(Context context, boolean withBackground)

             GpsPermission.requestLocation(getApplicationContext(),false)
                .observeForever(new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        if(aBoolean){
                            //Allow
                        }else{
                            //Deny
                        }
                    }
                });
```
