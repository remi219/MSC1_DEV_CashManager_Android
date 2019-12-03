# CashManager project: Android application

Android application for the Cash Manager project

## How to connect to a local API for debugging

API address is set in the file service/ServiceBuilder.kt

### Using the Android emulator

Use for baseUrl: http://10.0.2.2:8080. This allow the emulator running on a VM to access its host.

### Using a real device

Your phone must be on the same network. Retrieve the local ip address where the API is running (in wifi parameters for example), then replace it in the file.
Ip address format is "http://192.168.___.___:8080".

## Payment information

### Cheque payment

Order can be payed using a QRcode.
The QRcode must contain the following Json structure:
``` json
  {
    "id": "123abc",
    "value": 1.24
  }
```
*Id* is not relevant and is not tested for now. However the field *value* must match the order total, otherwise the cheque will be rejected.
