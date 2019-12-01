# CashManager project: Android application

Android application for the Cash Manager project

## Payment information

### Cheque payment

Order can be payed using a QRcode.
The QRcode must contain the following Json structure:
  {
    id: "123abc",
    value: 1.24
  }
*Id* is not relevant and is not tested. However the field *value* must match the order total, otherwise the cheque will be rejected.
