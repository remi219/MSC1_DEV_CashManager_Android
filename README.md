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

## Screens

### Login and register

Enter your username and password to login to the application. If you do not have an account, tap the **register** button and create an account. Username and password are mandatory.

### Cash register and product picker

Here is the content of the cart. Cart content is retrieved from the API if existing. Tap the **Add products** button to add articles to your cart with the list or by scanning a barcode (id of the barcode is the id of the product in the API). Obviously, you can only proceed if your cart is not empty. Proceed will save the content of your cart online. You can also select you payment method here (Cheque or NFC). Use the API Swagger to create new items.

### Bill

This page show the total bill of your cart. An order is created and sent to the API if you proceed further.

### Payment

This page allow the user to pay their bill using their selected payment method. See the paragraph above for details about cheque payment. On success the customer cart stored on the API will be cleared.
User can also cancel their order by cancelling the operation and return to previous screen.
