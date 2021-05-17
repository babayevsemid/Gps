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
    implementation 'com.github.babayevsemid:Gps-tracker:2.0.0'
    
    //And Play services location
    implementation 'com.google.android.gms:play-services-location:18.0.0'
}
```

Note: (AppCompatActivity, Fragment) Returns location if GPS is active, otherwise will prompt to enable it and return consequently
Note: (Context) Works if GPS is active, otherwise will not function

### Use in Kotlin
 
```
val manager = GpsBuilder(this) // this: AppCompatActivity, Fragment or Context
                .build()

manager.onNewLocationAvailable = { lat: Double, lng: Double ->
    
}

manager.onLastKnownLocation = { lat: Double, lng: Double ->
    
}

manager.onBackgroundNotAvailable = {
            
}

manager.onNotAvailable = {
     
}

manager.connect()
```

### Use in Java

```
GpsManager manager = new GpsBuilder(this)
                .build()

manager.onNewLocationAvailable = (lat, lng) -> {
           
      return null;
};

manager.onLastKnownLocation = (lat, lng) -> {
          
      return null;
};

manager.onBackgroundNotAvailable = () -> {

      return null;
};

manager.onNotAvailable = () -> {

      return null;
};

manager.connect()
```

### Tracking
 
```
val manager = GpsBuilder(this) // this: AppCompatActivity, Fragment or Context
		.configDistance(1) // 1 meter
                .configUpdateTime(2000) // 2 sec
                .configTrackingEnabled(true)
                .configOnResumeConnect(true)
                .configOnPauseDisconnect(false)
                .configDefaultLocation(42.235476235, 41.236453265) // default location
                .build()

manager.onNewLocationAvailable = { lat: Double, lng: Double ->
    
}

manager.onLastKnownLocation = { lat: Double, lng: Double ->
    
}

manager.onBackgroundNotAvailable = {
            
}

manager.onNotAvailable = {
     
}

manager.connect()
```

### Gps enable live data

```
 GpsManager.gpsEnableLiveData.observeForever(aBoolean -> {
            if (aBoolean)
                //enabled
            else
                //disabled
        });
        
 GpsManager.gpsEnableLiveData.postValue(GpsPermission.isGpsEnabled(getApplicationContext()));
        
```
### Use backgrouund location

```
    <uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
    
    *.configWithBackgroundPermission(true)
    
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
        * onResumeConnect - false;
        * onPauseDisconnect - true;
        * withBackgroundPermission - false;
```

### Last getted location

* This will change when you get a location and you can use it anywhere
       
```
       GpsManager.location;
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
         
* Full check location and gps is Enabled
```         
        if(GpsPermission.checkFullLocation(getApplicationContext(), false)){
            
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
