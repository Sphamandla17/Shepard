# The Shepherd

**AUNBC Church Management and Member Engagement Application**

> **Tagline:** God is Love

The Shepherd is an Android mobile application developed for the African United National Baptist Church (AUNBC). The application provides church members, administrators, and pastors with a central platform for accessing church information, communicating with members, managing events, submitting prayer requests, and accessing Bible content.

The application was developed as part of the Open Source Coding module.

---

## 1. Project Overview

The Shepherd aims to improve communication and participation within the church by providing members with access to important church services and information through a mobile application.

The application supports three main user roles:

* **Church Member**
* **Administrator**
* **Pastor**

Each role has access to features relevant to their responsibilities.

---

## 2. Main Features

### Authentication

* User registration
* User login
* Firebase Authentication
* Role-based navigation
* Member, Administrator and Pastor access

### Member Features

* Member dashboard
* Prayer requests
* Church events
* Event registration
* Bible reading
* Church announcements
* Notifications
* QR church check-in
* Member profile
* Application settings
* English and isiZulu language support

### Administrator Features

* Administrator dashboard
* Manage church members
* Manage events
* Create events
* View event registrations
* Create/manage church notifications
* Manage church announcements

### Pastor Features

* Pastor dashboard
* View prayer requests
* View individual prayer request details
* Update prayer request status

---

## 3. REST API

The Shepherd uses a custom REST API called **TheShepherdAPI**.

The API was developed using:

* ASP.NET Core
* C#
* Entity Framework Core
* SQL Server
* Swagger/OpenAPI

The API provides endpoints for application data and demonstrates communication between the Android application and a server-side database.

### Current API Features

#### Users

```text
POST /api/Users/register
```

Used to register users through the custom API.

#### Announcements

```text
GET /api/Announcements
GET /api/Announcements/{id}
POST /api/Announcements
DELETE /api/Announcements/{id}
```

These endpoints allow church announcements to be created, retrieved and deleted.

---

## 4. Android Application Technologies

The Android application was developed using:

* **Kotlin**
* **Android Studio**
* **XML layouts**
* **Firebase Authentication**
* **Firebase Firestore**
* **Firebase Cloud Messaging**
* **Retrofit**
* **Gson**
* **RecyclerView**
* **Room/SQLite planned for offline functionality**

The application targets Android devices and has been tested on a physical Android mobile device.

---

## 5. External Bible API

The application uses the **HelloAO Free Use Bible API** to retrieve Bible content.

The Bible feature demonstrates the use of an external REST API from the Android application.

Example endpoint:

```text
https://bible.helloao.org/api/BSB/JHN/3.json
```

The application retrieves Bible chapter information and displays the verses to the user.

---

## 6. Database

The project uses different data storage technologies for different requirements.

### Firebase Firestore

Firestore is used for application data such as:

* Users
* Prayer requests
* Events
* Event registrations
* Notifications

### SQL Server

SQL Server is used by the custom **TheShepherdAPI** for server-side API data.

The database is accessed through Entity Framework Core.

---

## 7. Multilingual Support

The Shepherd supports two languages:

* English
* isiZulu

Language selection is stored using Android preferences and applied throughout the application using Android's locale functionality.

Example:

```text
English → Announcements

isiZulu → Izimemezelo
```

The multilingual functionality is intended to make the application more accessible to South African church members.

---

## 8. Application Architecture

The project consists of two main components:

```text
The Shepherd Android App
          |
          | Retrofit / REST API
          ↓
    TheShepherdAPI
          |
          ↓
      SQL Server
```

Firebase services are also used by the Android application:

```text
The Shepherd Android App
        |
        ├── Firebase Authentication
        |
        ├── Firebase Firestore
        |
        └── Firebase Cloud Messaging
```

---

## 9. GitHub Version Control

Git and GitHub are used for source-code management and version control.

The project is maintained using multiple commits to track development progress.

Development milestones include:

* Initial Android application
* Authentication
* Member/Admin/Pastor dashboards
* Events
* Prayer requests
* Bible API integration
* REST API development
* Announcement API integration
* User interface improvements
* Language support
* Bug fixes and navigation improvements

---

## 10. Testing

Testing is performed throughout development to verify that application features work correctly.

Testing includes:

* Authentication testing
* Navigation testing
* API endpoint testing
* Mobile device testing
* Firebase functionality testing
* Language switching testing
* Event registration testing
* Prayer request testing
* Announcement retrieval testing

The REST API is tested using Swagger/OpenAPI, while the Android application is tested on a physical Android device.

---

## 11. How to Run the Android Application

### Requirements

The following are required:

* Android Studio
* Android SDK
* Kotlin
* Android device or emulator
* Internet connection for online API/Firebase features

### Steps

1. Clone the repository from GitHub.
2. Open the project in Android Studio.
3. Allow Gradle to synchronise.
4. Connect an Android device or start an emulator.
5. Enable USB debugging if using a physical device.
6. Build the project.
7. Run the application.

---

## 12. Running TheShepherdAPI

The custom API is developed using ASP.NET Core.

### Requirements

* Visual Studio
* .NET SDK
* SQL Server
* SQL Server Management Studio (optional)

Run the API project and open Swagger:

```text
https://localhost:7131/swagger
```

For physical Android device testing on the same Wi-Fi network, the API can also be accessed through the computer's local network address.

Example:

```text
http://YOUR-PC-IP:5000/
```

---

## 13. Project Structure

### Android Application

```text
app/
├── manifests/
├── java/
│   └── com.example.shepherd/
│       ├── api/
│       ├── activities/
│       └── Firebase services
│
└── res/
    ├── drawable/
    ├── layout/
    ├── mipmap/
    ├── values/
    └── values-zu/
```

### REST API

```text
TheShepherdAPI/
├── Controllers/
├── Data/
├── Models/
├── Migrations/
├── Properties/
└── Program.cs
```

---

## 14. Design

The application uses a simple black-and-white visual design.

The design focuses on:

* Clear navigation
* Consistent buttons
* Readable typography
* Simple layouts
* Consistent spacing
* Mobile-friendly screens
* Easy access to important church features

The design was created to keep the application professional and easy for church members to use.

---

## 15. Future Improvements

Possible future improvements include:

* Complete offline functionality using Room/SQLite
* Automatic synchronisation when the device reconnects
* Expanded push notification functionality
* Additional church management features
* Improved administrator reporting
* Additional South African language support
* Cloud deployment of the REST API
* Expanded automated testing

---

## 16. Developer

**Developer:** Siphamandla Tshabalala

**Project:** The Shepherd

**Institution:** Rosebank College / The Independent Institute of Education (IIE)

**Module:** Open Source Coding

**Year:** 2026

---

## 17. Conclusion

The Shepherd provides a central digital platform for AUNBC church members, administrators and pastors. The application combines Android development, Firebase services, REST API communication, SQL Server, external API integration and multilingual support.

The project demonstrates the practical application of mobile application development, database management, API development, version control and software testing.
## 10. SHEPHERD DEMONSTRATION VIDEO

DEMONSTRATION VIDEO 

[OneDrive :]
(https://advtechonline-my.sharepoint.com/:v:/g/personal/st10441479_rcconnect_edu_za/IQDxmRRm2ZgHQaaJfmEhafdRAcMuKal4JfBUVhXbve6Lr_A?e=piaQ8f.)
