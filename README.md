# CardioHealth-Kit
Continuous cardiovascular monitoring system using a **MAX30100 sensor** and **ESP32**, integrated with an **Android app** and **Machine Learning algorithms** for real-time biometric analysis, early detection of cardiac events, and automatic alerts.

## Overview
The **CardioHealth-Kit** is a prototype designed to help in the **early detection of heart attacks and strokes (AVC)**.  
It combines a **wearable bracelet**, **biometric sensors**, and **AI-powered analysis** to continuously track vital signs and identify anomalies that may indicate an emergency situation.

## Features
- Continuous monitoring of **heart rate (bpm)** and **blood oxygen saturation (SpO₂)**; 
- Wireless communication using **ESP32 with Bluetooth**;
- **Android app** for real-time visualization, alerts, and history tracking ; 
- **Facial analysis (MediaPipe)** to detect asymmetry related to stroke;  
- **Voice analysis (TarsosDSP)** to identify anomalies in speech patterns;
- **SOS button** for automatic emergency call or message to a contact.

## System Components
### Hardware
- **MAX30100**: optical sensor for heart rate and SpO₂;
- **ESP32**: microcontroller for processing and Bluetooth transmission;
- **Wearable Bracelet**: 3D-printed prototype with integrated PCB and portable power supply.

### Software
- **Android Application**: monitoring, analysis, alerts, SOS, and history; 
- **Web Service (Java) + MySQL database** for data management;
- **Machine Learning Modules**:  
  - Facial asymmetry detection (**MediaPipe**);
  - Voice anomaly detection (**TarsosDSP**).  

## Technologies Used
- **Hardware:** MAX30100, ESP32, portable power supply;
- **Programming:** C++ (Arduino IDE), Java (Android + Web Service), PHP (server); 
- **Machine Learning:** MediaPipe, TarsosDSP; 
- **Database:** MySQL (via Laragon);  
- **Design Tools:** EasyEDA (PCB), SolidWorks (bracelet prototype). 

## Installation & Usage
1. **Set up hardware**: connect MAX30100 to ESP32, insert into bracelet case, and power on;
2. **Install Android app**: pair with device via Bluetooth;
3. **Start monitoring**: view real-time data and history in the app; 
4. **Emergency use**: press **SOS button** to trigger automatic call/message. 
