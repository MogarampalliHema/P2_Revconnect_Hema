# RevConnect — Professional Networking Reimagined

RevConnect is a high-performance Monolithic Web Application designed for professional networking and community engagement. Inspired by modern social platforms, it features a unique "Pastel Haven" aesthetic combined with robust enterprise-grade security and analytics.

 Key Features

- **Advanced Authentication**: Secure login and registration using BCrypt password encryption, supporting both Session-based (Web) and JWT-based (REST API) authentication.
- **Professional Networking**: 
  - **Connections**: Send and receive connection requests to build your professional network.
  - **Following**: Follow users to see their updates in your feed.
- **Rich Content Creation**:
  - Create posts with hashtags and image uploads.
  - Share/Repost existing content.
  - Scheduled posting for automated publishing.
  - Pin important posts to your profile.
- **Dynamic Interactions**:
  - Real-time notification system for likes, comments, connections, and shares.
  - Nested comments and post liking.
- **Private Messaging**: Full-featured inbox with secure one-on-one conversations.
- **Analytics Dashboard**: Comprehensive metrics for user engagement, post reach, and account growth.
- **Search & Discover**: Hashtag-based search and trending posts discovery.

---

## Tech Stack

- **Backend**: Java 17+, Spring Boot 3.x, Spring Security, Spring Data JPA
- **Database**: Oracle SQL (Primary), JDBC, Hibernate ORM
- **Frontend**: Thymeleaf, CSS3 (Custom Design System), JavaScript (ES6+), Lucide Icons
- **Logging**: Log4J2 for enterprise monitoring
- **Security**: JWT (JSON Web Tokens), BCrypt, CSRF Protection

---

##  Database Schema (ERD)

@startuml
skinparam roundcorner 5
skinparam class {
    BackgroundColor White
    BorderColor #1A5F7A
    HeaderBackgroundColor #E9F1F5
    ArrowColor #228B22
    FontName Arial
}
skinparam shadowing false

' Macros to make definitions cleaner
!define Table(name,desc) class name as "desc" << (T,#ADD1B2) >>
!define primary_key(x) <b><color:#B8860B><&key></color> x</b>
!define foreign_key(x) <color:#a81818><&key></color> x
!define column(x) x

'=============================
' ENUMERATIONS
'=============================
enum UserRole {
  PERSONAL
  CREATOR
  BUSINESS
}

enum PrivacySetting {
  PUBLIC
  PRIVATE
}

enum PostType {
  REGULAR
  PROMOTIONAL
  ANNOUNCEMENT
  REPOST
}

enum ConnectionStatus {
  PENDING
  ACCEPTED
  REJECTED
}

enum NotificationType {
  CONNECTION_REQUEST
  CONNECTION_ACCEPTED
  NEW_FOLLOWER
  POST_LIKED
  POST_COMMENTED
  POST_SHARED
  MENTION
  MESSAGE_RECEIVED
}

'=============================
' TABLES (ENTITIES)
'=============================
Table(users, "users") {
  primary_key(id) : Long
  ..
  column(username) : VARCHAR(50) [UNIQUE, NOT NULL]
  column(email) : VARCHAR(255) [UNIQUE, NOT NULL]
  column(password) : VARCHAR(255) [NOT NULL]
  column(full_name) : VARCHAR(255)
  column(bio) : TEXT
  column(profile_picture) : VARCHAR(255)
  column(location) : VARCHAR(255)
  column(website) : VARCHAR(255)
  column(role) : UserRole [NOT NULL]
  column(privacy_setting) : PrivacySetting
  column(is_active) : BOOLEAN
  column(category) : VARCHAR(255)
  column(contact_info) : VARCHAR(255)
  column(business_address) : VARCHAR(255)
  column(business_hours) : VARCHAR(255)
  column(created_at) : DATETIME
  column(updated_at) : DATETIME
}

Table(posts, "posts") {
  primary_key(id) : Long
  ..
  foreign_key(author_id) : Long [NOT NULL]
  foreign_key(original_post_id) : Long [NULLABLE]
  column(content) : TEXT [NOT NULL]
  column(hashtags) : VARCHAR(255)
  column(post_type) : PostType
  column(is_pinned) : BOOLEAN
  column(scheduled_at) : DATETIME
  column(is_published) : BOOLEAN
  column(cta_label) : VARCHAR(255)
  column(cta_url) : VARCHAR(255)
  column(image_url) : VARCHAR(255)
  column(created_at) : DATETIME
  column(updated_at) : DATETIME
}

Table(comments, "comments") {
  primary_key(id) : Long
  ..
  foreign_key(post_id) : Long [NOT NULL]
  foreign_key(author_id) : Long [NOT NULL]
  column(content) : TEXT [NOT NULL]
  column(created_at) : DATETIME
}

Table(likes, "likes") {
  primary_key(id) : Long
  ..
  foreign_key(user_id) : Long [NOT NULL]
  foreign_key(post_id) : Long [NOT NULL]
  column(created_at) : DATETIME
}

Table(follows, "follows") {
  primary_key(id) : Long
  ..
  foreign_key(follower_id) : Long [NOT NULL]
  foreign_key(followed_id) : Long [NOT NULL]
  column(created_at) : DATETIME
}

Table(connections, "connections") {
  primary_key(id) : Long
  ..
  foreign_key(sender_id) : Long [NOT NULL]
  foreign_key(receiver_id) : Long [NOT NULL]
  column(status) : ConnectionStatus [NOT NULL]
  column(created_at) : DATETIME
  column(updated_at) : DATETIME
}

Table(messages, "messages") {
  primary_key(id) : Long
  ..
  foreign_key(sender_id) : Long [NOT NULL]
  foreign_key(recipient_id) : Long [NOT NULL]
  column(content) : TEXT [NOT NULL]
  column(is_read) : BOOLEAN
  column(created_at) : DATETIME
}

Table(notifications, "notifications") {
  primary_key(id) : Long
  ..
  foreign_key(recipient_id) : Long [NOT NULL]
  foreign_key(actor_id) : Long [NULLABLE]
  column(type) : NotificationType [NOT NULL]
  column(message) : TEXT
  column(reference_id) : Long
  column(is_read) : BOOLEAN
  column(created_at) : DATETIME
}

Table(notification_preferences, "notification_preferences") {
  primary_key(id) : Long
  ..
  foreign_key(user_id) : Long [UNIQUE, NOT NULL]
  column(connection_requests) : BOOLEAN
  column(connection_accepted) : BOOLEAN
  column(new_followers) : BOOLEAN
  column(post_likes) : BOOLEAN
  column(post_comments) : BOOLEAN
  column(post_shares) : BOOLEAN
}

Table(password_reset_tokens, "password_reset_tokens") {
  primary_key(id) : Long
  ..
  foreign_key(user_id) : Long [NOT NULL]
  column(token) : VARCHAR(255) [UNIQUE, NOT NULL]
  column(expiry_date) : DATETIME [NOT NULL]
}

Table(blocks, "blocks") {
  primary_key(id) : Long
  ..
  foreign_key(blocker_id) : Long [NOT NULL]
  foreign_key(blocked_id) : Long [NOT NULL]
  column(created_at) : DATETIME
}

'=============================
' RELATIONSHIPS (Crow's Foot Notation)
'=============================
' 1 to Many: ||--o{
' 1 to 1:    ||--||
' 0 or 1:    |o--||

users ||--o{ posts : "author_id"
users ||--o{ comments : "author_id"
posts ||--o{ comments : "post_id"

users ||--o{ likes : "user_id"
posts ||--o{ likes : "post_id"

users ||--o{ follows : "follower_id"
users ||--o{ follows : "followed_id"

users ||--o{ connections : "sender_id"
users ||--o{ connections : "receiver_id"

users ||--o{ messages : "sender_id"
users ||--o{ messages : "recipient_id"

users ||--o{ notifications : "recipient_id"
users |o--o{ notifications : "actor_id"

users ||--|| notification_preferences : "user_id"
users ||--o| password_reset_tokens : "user_id"

users ||--o{ blocks : "blocker_id"
users ||--o{ blocks : "blocked_id"

posts |o--o{ posts : "original_post_id"

'=============================
' ENUM CONNECTIONS (To stop them floating randomly)
'=============================
users ..> UserRole : "uses"
users ..> PrivacySetting : "uses"
posts ..> PostType : "uses"
connections ..> ConnectionStatus : "uses"
notifications ..> NotificationType : "uses"

@enduml


## Setup & Installation

### Prerequisites
- JDK 17 or higher
- Oracle Database 19c/21c
- Maven 3.8+

### Database Configuration
Update `src/main/resources/application.properties` with your Oracle DB credentials:
```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521/orcl
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

### Running the Application
1. Clone the repository.
2. Navigate to the project root.
3. Run the following command:
   ```bash
   mvn spring-boot:run
   ```
4. Access the application at: `http://localhost:8080`

---

##  Testing
The project includes a comprehensive suite of unit and integration tests. Run tests using:
```bash
mvn test
```


 
