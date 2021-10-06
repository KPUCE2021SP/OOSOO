#include <Servo.h>

Servo myservo;

const int trigPin = 2;      //초음파 트리거핀 
const int echoPin = 3;      //초음파 에코핀 
const int magnetPin = 10;   //자석센서 디지털핀
int pos = 0;          //모터 각도제어
int count = 0;        // 초 세는 변수 
int val = 0;          //자석센서 입력값 받는 변수 


void setup() {
  Serial.begin(115200);
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);
  pinMode(magnetPin, INPUT);
  myservo.attach(7);
}

void loop() {
  
  int aa  = 0;
  aa = Serial.read();
  
  if(aa == 1){ /* 보관함이 열렸을 때 센서들 동작*/
    Serial.println("Open");
    myservo.write(pos);
    pos += 90;
    delay(3000); //사용자가 보관함을 여는 시간 약 3초
    
    while(1){

    /* 초음파 보내기 */
      digitalWrite(trigPin, HIGH);
      delay(10);
      digitalWrite(trigPin, LOW);
  
    /* echo가 HIGH를 유지한 시간 = 초음파가 갔다가 돌아온 시간 */
      float duration = pulseIn(echoPin, HIGH); 

    /* 소리의 속도(340m/s)를 곱한다.
    마이크로초(ms)를 초(s)로 변환하기 위해 10000을 나눈다.
    마지막으로 왕복 거리이므로 2를 나눈다. */
      float distance = (float) 340*duration / 10000 / 2;

      Serial.print("distance : ");
      Serial.print(distance);
      Serial.println("cm");

      val = digitalRead(magnetPin);
    /*보관함 뚜껑이 닫히면 카운트 세기 시작*/
      if(distance <= 6 && val == 0 ){
        count++;
      }

    /*5초이상 닫혀있으면 잠금.*/
      if (count >= 5){
        myservo.write(pos);
        pos -= 90;
        count = 0;
        Serial.write(1);
        Serial.println("close");
        Serial.flush();
        break;
      }
      delay(1000); // 1초에 한번씩 측정
    }
  }
}
