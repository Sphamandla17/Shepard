# The Shepherd

**AUNBC Church Management and Member Engagement Application**

> **Tagline:** God is Love

The Shepherd is an Android mobile application developed for the African United National Baptist Church (AUNBC). The application provides church members, administrators, and pastors with a central platform for accessing church information, communicating with members, managing events, submitting prayer requests, and accessing Bible content.

The application was developed as part of the Open Source Coding module at Rosebank College.

---

## 1. Project Overview

The Shepherd aims to improve communication and participation within the church by providing members with access to important church services and information through a mobile application.

The application supports three main user roles:

- **Church Member**
- **Administrator**
- **Pastor**

Each role has access to features relevant to their responsibilities.

## 2. Main Features

### Authentication
- User registration and login
- Firebase Authentication
- Google Sign-In
- Role-based navigation
- Member, Administrator, and Pastor access

### Member Features
- Member dashboard
- Prayer requests
- Church events
- Event registration
- Bible reading and searching
- Church announcements
- Push notifications
- Member profile
- Application settings
- English and isiZulu interface support
- Offline access to cached Bible content

### Administrator Features
- Administrator dashboard
- Manage church members
- Manage events
- Create events
- View event registrations
- Create and manage church announcements
- Manage church notifications

### Pastor Features
- Pastor dashboard
- View prayer requests
- View individual prayer request details
- Update prayer request status

## 3. REST API

The Shepherd uses a custom REST API called **TheShepherdAPI**, developed separately from the Android application.

The API was developed using:

- ASP.NET Core
- C#
- Entity Framework Core
- SQL Server
- Swagger/OpenAPI

The API provides server-side functionality and demonstrates communication between the Android application and a backend database.

### Current API Features

#### Users

```http
POST /api/Users/register
```

Used for user registration through the custom API.

#### Announcements

```http
GET /api/Announcements
GET /api/Announcements/{id}
POST /api/Announcements
```

These endpoints support retrieving announcements and creating new announcements.

The announcement functionality is integrated with the notification service.

**Backend repository:**  
[TheShepherdAPI – GitHub](https://github.com/Sphamandla17/TheShepherdAPI)

## 4. Android Application Technologies

The Android application was developed using:

- **Kotlin**
- **Android Studio**
- **XML layouts**
- **ViewBinding**
- **Firebase Authentication**
- **Firebase Firestore**
- **Firebase Cloud Messaging**
- **Retrofit**
- **Gson**
- **RecyclerView**
- **SQLite** for local Bible data and offline access
- **Credential Manager** for Google Sign-In

The application has been built and tested on a physical Android device.

## 5. External Bible API

The application uses the **HelloAO Free Use Bible API** to retrieve Bible content.

Example endpoint:

```text
https://bible.helloao.org/api/BSB/JHN/3.json
```

The application retrieves Bible chapter information and displays verses to the user.

Bible content is cached locally to support offline reading and searching after the relevant content has been downloaded.

**Note:** The application supports English and isiZulu interface translations. The currently integrated Bible text is the English Berean Standard Bible (BSB).

## 6. Database and Data Storage

The project uses different data storage technologies for different requirements.

### Firebase Firestore

Firestore is used for application data such as:

- User information
- Prayer requests
- Events
- Event registrations
- Notifications
- Church announcements

### SQL Server

SQL Server is used by the custom **TheShepherdAPI** for server-side data storage.

The database is accessed through Entity Framework Core.

### SQLite

SQLite is used locally by the Android application to store Bible content and support offline Bible functionality.

## 7. Multilingual and Offline Support

### Multilingual Support

The Shepherd supports two interface languages:

- English
- isiZulu

Language selection is stored using Android preferences and applied throughout the application.

Example:

```text
English → Announcements
isiZulu → Izimemezelo
```

The multilingual functionality is intended to make the application more accessible to South African church members.

### Offline Support

The application includes offline handling to improve usability when an internet connection is unavailable.

Features include:

- Network connectivity detection
- An offline status message on the member dashboard
- Cached Bible content for offline reading
- Local Bible searching
- An online status update when connectivity returns

Online features that depend on Firebase or the backend API require an internet connection.

## 8. Application Architecture

The project consists of two main components: the Android application and the ASP.NET Core backend API.

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

Firebase services are also integrated into the Android application:

```text
       The Shepherd Android App
                 |
       ┌─────────┼──────────┐
       ↓         ↓          ↓
   Firebase   Firebase   Firebase
    Auth     Firestore     FCM
```

The Android application communicates with the backend API for supported server-side operations while Firebase provides authentication, cloud data storage, and push notification services.

## 9. GitHub Version Control

Git and GitHub are used for source-code management and version control.

The project is maintained in two separate repositories:

### Android Application

[The Shepherd Android Repository](https://github.com/Sphamandla17/Shepard)

### Backend API

[The Shepherd API Repository](https://github.com/Sphamandla17/TheShepherdAPI)

Both repositories form part of the same project. The Android repository contains the mobile application, while the backend repository contains the ASP.NET Core REST API.

Development milestones include:

- Initial Android application
- Authentication and Google Sign-In
- Member, Administrator, and Pastor dashboards
- Events and event registration
- Prayer requests
- Bible API integration
- REST API development
- Announcement API integration
- Push notification functionality
- User interface redesign
- English and isiZulu language support
- Offline Bible functionality
- Automated testing
- Bug fixes and navigation improvements

## 10. Testing

Testing was performed throughout development to verify that application features work correctly.

### Local Unit Testing

The Android application includes local unit tests covering:

- Announcement JSON parsing
- Notification model properties
- Bible reference parsing
- Search result model properties

**Reported result:** 7 successful local unit test executions.

### Instrumented Testing

Instrumented tests were executed on a physical Samsung Android device.

Testing covered:

- Member dashboard loading
- Announcements navigation
- Language preference persistence
- Network connectivity handling
- Bible repository functionality
- Local database schema
- Bible controls
- Events list
- Settings navigation

**Reported result:** 12 successful instrumented test executions.

### Overall Testing Results

| Test category | Successful executions |
|---|---:|
| Local unit tests | 7 |
| Instrumented tests | 12 |
| **Total** | **19** |

The project also underwent a final regression review, and the Android debug build completed successfully.

The REST API can be tested using Swagger/OpenAPI.

## 11. How to Run the Android Application

### Requirements

- Android Studio
- Android SDK
- Kotlin
- Android device or emulator
- Internet connection for online API and Firebase features

### Steps

1. Clone the Android repository from GitHub.
2. Open the project in Android Studio.
3. Allow Gradle to synchronise.
4. Connect an Android device or start an emulator.
5. Enable USB debugging when using a physical device.
6. Build the project.
7. Run the application.

## 12. Running TheShepherdAPI

The custom API is developed using ASP.NET Core.

### Requirements

- Visual Studio
- .NET SDK
- SQL Server
- SQL Server Management Studio (optional)

### Steps

1. Clone the backend repository.
2. Open the solution in Visual Studio.
3. Configure the required database connection and Firebase settings.
4. Ensure SQL Server is available.
5. Run the API project.
6. Open Swagger to view and test the available endpoints.

Example Swagger address:

```text
https://localhost:7131/swagger
```

For physical Android device testing on the same Wi-Fi network, the API can also be accessed through the computer's local network address. The Android application must use the correct API base URL.

Example:

```text
http://YOUR-PC-IP:YOUR-PORT/
```

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
├── Security/
├── Services/
└── Program.cs
```

## 14. Design

The application uses a modern, minimalist black-and-white visual design with restrained gold accents.

The design focuses on:

- Clear navigation
- Consistent buttons
- Readable typography
- Simple layouts
- Consistent spacing
- Mobile-friendly screens
- Easy access to important church features

The redesign aims to provide a professional, accessible, and consistent experience for church members.

## 15. Future Improvements

Possible future improvements include:

- Expanding offline functionality to more application features
- Automatic synchronisation of queued data when connectivity returns
- Additional church management features
- Improved administrator reporting
- Additional South African language support
- Cloud deployment of the REST API
- Expanded automated testing
- Additional Bible translations

## 16. Shepherd Demonstration Video

The following video demonstrates the development and functionality of The Shepherd Android application.

**Project Demonstration Video:**  
[Watch The Shepherd – OneDrive](https://advtechonline-my.sharepoint.com/:v:/g/personal/st10441479_rcconnect_edu_za/IQDxmRRm2ZgHQaaJfmEhafdRAcMuKal4JfBUVhXbve6Lr_A?e=piaQ8f.)

The demonstration provides a practical overview of the application's interface, main features, and functionality.

## 17. Developer

**Developer:** Siphamandla Tshabalala

**Project:** The Shepherd

**Institution:** Rosebank College / The Independent Institute of Education (IIE)

**Module:** Open Source Coding

**Year:** 2026

## 18. Conclusion

The Shepherd provides a central digital platform for AUNBC church members, administrators, and pastors.

The application combines Android development, Firebase services, REST API communication, SQL Server, external API integration, multilingual interface support, offline Bible functionality, and automated testing.

The project demonstrates the practical application of mobile application development, database management, API development, version control, and software testing.
