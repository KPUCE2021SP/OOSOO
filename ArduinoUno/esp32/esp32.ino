/* 라이브러리 github.com/mobizt/Firebase-ESP-Client 
   보관함 777_777 아두이노 우노 */
   
#if defined(ESP32)
#include <WiFi.h>
#elif defined(ESP8266)
#include <ESP8266WiFi.h>
#endif
#include <Firebase_ESP_Client.h>

//Provide the token generation process info.
#include "addons/TokenHelper.h"
//Provide the RTDB payload printing info and other helper functions.
#include "addons/RTDBHelper.h"

/* 1. Define the WiFi credentials */
#define WIFI_SSID "Jinho"
#define WIFI_PASSWORD "02280228"

/* 2. Define the API Key */
#define API_KEY "AIzaSyBAj9r-RZXJXq-2BLqNr8R5Cd60rBSIvpw"

/* 3. Define the RTDB URL */
#define DATABASE_URL "ecobasket-c3ef9-default-rtdb.asia-southeast1.firebasedatabase.app"

/* 4. Define the user Email and password that alreadey registerd or added in your project */
#define USER_EMAIL "test@jinho.com"
#define USER_PASSWORD "980118"

//Define Firebase Data object
FirebaseData fbdo;

FirebaseAuth auth;
FirebaseConfig config;

//HardwareSerial unoSerial(2);

unsigned long sendDataPrevMillis = 0;
unsigned long count = 0;

void setup()
{

  Serial.begin(115200); //UART0 
  Serial2.begin(115200); //UART2
  //unoSerial.begin(115200, SERIAL_8N1, 16, 17); //UART2 RX TX

  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED)
  {
    Serial.print(".");
    delay(300);
  }
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();

  Serial.printf("Firebase Client v%s\n\n", FIREBASE_CLIENT_VERSION);

  /* Assign the api key (required) */
  config.api_key = API_KEY;

  /* Assign the user sign in credentials */
  auth.user.email = USER_EMAIL;
  auth.user.password = USER_PASSWORD;

  /* Assign the RTDB URL (required) */
  config.database_url = DATABASE_URL;

  /* Assign the callback function for the long running token generation task */
  config.token_status_callback = tokenStatusCallback; //see addons/TokenHelper.h

  Firebase.begin(&config, &auth);

  //Comment or pass false value when WiFi reconnection will control by your code or third party library
  Firebase.reconnectWiFi(true);

  Firebase.setDoubleDigits(5);
}

void loop()
{
  if (Firebase.ready() &&  (millis() - sendDataPrevMillis > 15000 || sendDataPrevMillis == 0))
  {
    sendDataPrevMillis = millis();
    Serial.printf("get isOpen Data...%s\n", Firebase.RTDB.getBool(&fbdo, "/Cabinet/777_777/isOpen") ?fbdo.to<bool>() ? "true" : "false" : fbdo.errorReason().c_str());
    Serial.println();
    
    if(fbdo.to<bool>() == true ){
      //send to Uno Board
      Serial2.write(1);
      count = 1;
    }
    
  } 
  
  if(Serial2.read() == 1 && count ==1)
    {
      sendDataPrevMillis = millis();
      Serial.printf("set isOpen Data...%s\n", Firebase.RTDB.setBool(&fbdo, "/Cabinet/777_777/isOpen", false) ? "Close Set OK!" : fbdo.errorReason().c_str());
      count =0;
    }
    
}
