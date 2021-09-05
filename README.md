# Live Crypto
### Mobile application for tracking cryptocurrency prices.

The goal of this project is to create mobile application for Android that allows users to track cryptocurrency prices.

Cryptocurrency prices are collected from [CryptoCompare API](https://min-api.cryptocompare.com/).

## Getting Started

These instructions will get you a copy of the project up and running on 
your local machine for development and testing purposes.

### Prerequisites

* Android SDK
* Android Studio - https://developer.android.com/studio/

Detailed information about installation and configurations are provided at developers' site.

## Technology Stack

* Android
* Java
* SQLite
* Google Charts - https://developers.google.com/chart
* JavaScript

Google Charts are embedded into the application using WebView.

### Setup 

A step by step instruction:
* Navigate to ``app/src/main/res/values/`` in project directory.
* Create ``secrets.xml`` resource file that contains
```
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="crypto_api_key">PUT-YOUR-API-KEY-HERE</string>
</resources>
```
* Replace example value with your CryptoCompare API key.

The application should be ready to build and run now.

## Preview

<table>
    <tr>
        <td>
            <p>Homepage</p>
            <img src="images/img_1.png" alt="homepage" title="Homepage">
        </td>
        <td>
            <p>Cryptocurrency Selector</p>
            <img src="images/img_2.png" alt="cryptocurrency selector" title="Cryptocurrency Selector">
        </td>
        <td>
            <p>Cryptocurrency Exchange Chart</p>
            <img src="images/img_3.png" alt="cryptocurrency exchange chart" title="Cryptocurrency Exchange Chart">
        </td>
    </tr>
</table>


## Author

* **Michał Koziara** 
